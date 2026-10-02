package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.FdService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class FdServiceImpl implements FdService {

    private final FixedDepositDAO fdDAO = new FixedDepositDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public FixedDeposit openFd(Long customerId, Long accountId, BigDecimal principal, int tenureMonths) throws BankException {
        if (!ValidationUtil.isValidAmount(principal) || principal.compareTo(new BigDecimal("1000")) < 0) {
            throw new BankException("Minimum FD amount is ₹1,000");
        }
        if (tenureMonths < 3 || tenureMonths > 120) {
            throw new BankException("FD tenure must be between 3 and 120 months");
        }

        // Determine interest rate based on tenure
        BigDecimal annualRate = new BigDecimal("6.50"); // Default 6.5%
        if (tenureMonths >= 12 && tenureMonths < 36) {
            annualRate = new BigDecimal("7.25");
        } else if (tenureMonths >= 36) {
            annualRate = new BigDecimal("7.75");
        }

        // Calculate maturity amount = Principal + (Principal * Rate * TenureYears)
        BigDecimal tenureYears = new BigDecimal(tenureMonths).divide(new BigDecimal("12"), 4, RoundingMode.HALF_UP);
        BigDecimal interest = principal.multiply(annualRate).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(tenureYears);
        BigDecimal maturityAmount = principal.add(interest).setScale(2, RoundingMode.HALF_UP);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findForUpdate(conn, accountId);
            if (account == null || !account.getCustomerId().equals(customerId)) {
                throw new BankException("Invalid account for FD creation");
            }
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Account is not active");
            }
            if (account.getAvailableBalance().compareTo(principal) < 0) {
                throw new InsufficientBalanceException("Insufficient balance to open FD. Required: ₹" + principal + ", Available: ₹" + account.getAvailableBalance());
            }

            // Debit principal from account
            BigDecimal before = account.getBalance();
            BigDecimal after = before.subtract(principal);
            accountDAO.updateBalance(conn, accountId, after, after);

            // Create FD record
            String fdNum = "SKFD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
            LocalDate startDate = LocalDate.now();
            LocalDate maturityDate = startDate.plusMonths(tenureMonths);

            FixedDeposit fd = new FixedDeposit();
            fd.setCustomerId(customerId);
            fd.setAccountId(accountId);
            fd.setFdNumber(fdNum);
            fd.setPrincipalAmount(principal);
            fd.setInterestRate(annualRate);
            fd.setTenureMonths(tenureMonths);
            fd.setMaturityAmount(maturityAmount);
            fd.setStartDate(Date.valueOf(startDate));
            fd.setMaturityDate(Date.valueOf(maturityDate));
            fd.setStatus("ACTIVE");

            Long fdId = fdDAO.create(conn, fd);
            fd.setFdId(fdId);

            // Create transaction record
            Transaction txn = new Transaction();
            txn.setTransactionReference("FD-OPEN-" + fdNum);
            txn.setAccountId(accountId);
            txn.setTransactionType(TransactionType.FD_INVESTMENT);
            txn.setAmount(principal);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription("FD Opening - " + fdNum + " (" + tenureMonths + " months @ " + annualRate + "%)");
            txn.setStatus(TransactionStatus.SUCCESS);
            transactionDAO.create(conn, txn);

            // Notification
            Notification n = new Notification();
            n.setUserId(customerId);
            n.setTitle("Fixed Deposit Opened");
            n.setMessage("FD " + fdNum + " of ₹" + principal + " created successfully. Maturity Amount: ₹" + maturityAmount);
            n.setNotificationType("FD");
            notificationDAO.create(conn, n);

            conn.commit();
            return fd;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Failed to open FD: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<FixedDeposit> getCustomerFds(Long customerId) throws BankException {
        try {
            return fdDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching FDs", e);
        }
    }

    @Override
    public FixedDeposit getFdById(Long fdId) throws BankException {
        try {
            FixedDeposit fd = fdDAO.findById(fdId);
            if (fd == null) throw new BankException("FD record not found");
            return fd;
        } catch (Exception e) {
            throw new BankException("Error fetching FD details", e);
        }
    }
}
