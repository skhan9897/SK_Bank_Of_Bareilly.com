package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.dto.EmiCalculatorDTO;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.LoanService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class LoanServiceImpl implements LoanService {

    private final LoanDAO loanDAO = new LoanDAOImpl();
    private final LoanPaymentDAO loanPaymentDAO = new LoanPaymentDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public EmiCalculatorDTO calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        EmiCalculatorDTO dto = new EmiCalculatorDTO();
        dto.setPrincipalAmount(principal);
        dto.setAnnualInterestRate(annualRate);
        dto.setTenureMonths(tenureMonths);

        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0 ||
            annualRate == null || annualRate.compareTo(BigDecimal.ZERO) <= 0 ||
            tenureMonths <= 0) {
            return dto;
        }

        // Monthly rate r = annualRate / (12 * 100)
        double r = annualRate.doubleValue() / (12.0 * 100.0);
        double P = principal.doubleValue();
        int n = tenureMonths;

        // Formula: P * r * (1+r)^n / ((1+r)^n - 1)
        double numerator = P * r * Math.pow(1 + r, n);
        double denominator = Math.pow(1 + r, n) - 1;
        double emi = numerator / denominator;

        BigDecimal emiVal = BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPayable = emiVal.multiply(new BigDecimal(tenureMonths)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalPayable.subtract(principal).setScale(2, RoundingMode.HALF_UP);

        dto.setEmiAmount(emiVal);
        dto.setTotalAmountPayable(totalPayable);
        dto.setTotalInterestPayable(totalInterest);

        return dto;
    }

    @Override
    public List<LoanType> getAllLoanTypes() throws BankException {
        try {
            return loanDAO.findAllLoanTypes();
        } catch (Exception e) {
            throw new BankException("Error fetching loan types", e);
        }
    }

    @Override
    public Loan applyLoan(Long customerId, Long loanTypeId, BigDecimal principal, int tenureMonths) throws BankException {
        if (!ValidationUtil.isValidAmount(principal)) {
            throw new BankException("Loan amount must be greater than zero");
        }
        try {
            LoanType lt = loanDAO.findLoanTypeById(loanTypeId);
            if (lt == null) throw new BankException("Invalid loan type selected");

            if (principal.compareTo(lt.getMaxAmount()) > 0) {
                throw new BankException("Amount exceeds maximum loan limit of ₹" + lt.getMaxAmount() + " for " + lt.getLoanName());
            }
            if (tenureMonths > lt.getMaxTenureMonths()) {
                throw new BankException("Tenure exceeds maximum allowable " + lt.getMaxTenureMonths() + " months for " + lt.getLoanName());
            }

            EmiCalculatorDTO emiDto = calculateEmi(principal, lt.getInterestRate(), tenureMonths);

            String loanNum = "SKLN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();

            Loan loan = new Loan();
            loan.setCustomerId(customerId);
            loan.setLoanTypeId(loanTypeId);
            loan.setLoanNumber(loanNum);
            loan.setPrincipalAmount(principal);
            loan.setInterestRate(lt.getInterestRate());
            loan.setTenureMonths(tenureMonths);
            loan.setEmiAmount(emiDto.getEmiAmount());
            loan.setOutstandingAmount(emiDto.getTotalAmountPayable());
            loan.setStatus(LoanStatus.PENDING);

            Long id = loanDAO.create(loan);
            loan.setLoanId(id);

            // Send notification
            Notification n = new Notification();
            n.setUserId(customerId);
            n.setTitle("Loan Application Submitted");
            n.setMessage("Your " + lt.getLoanName() + " application " + loanNum + " for ₹" + principal + " is under review.");
            n.setNotificationType("LOAN");
            notificationDAO.create(n);

            return loan;
        } catch (Exception e) {
            throw new BankException("Failed to submit loan application: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> getCustomerLoans(Long customerId) throws BankException {
        try {
            return loanDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching loans", e);
        }
    }

    @Override
    public Loan getLoanById(Long loanId) throws BankException {
        try {
            Loan l = loanDAO.findById(loanId);
            if (l == null) throw new BankException("Loan record not found");
            return l;
        } catch (Exception e) {
            throw new BankException("Error fetching loan details", e);
        }
    }

    @Override
    public LoanPayment payEmi(Long loanId, Long accountId, Long customerId) throws BankException {
        Connection conn = null;
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null || !loan.getCustomerId().equals(customerId)) {
                throw new BankException("Loan not found or unauthorized");
            }
            if (loan.getStatus() != LoanStatus.APPROVED) {
                throw new BankException("Loan is not active or approved for EMI payment");
            }
            if (loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BankException("Loan is already fully paid.");
            }

            BigDecimal emi = loan.getEmiAmount();
            if (emi.compareTo(loan.getOutstandingAmount()) > 0) {
                emi = loan.getOutstandingAmount();
            }

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findForUpdate(conn, accountId);
            if (account == null || !account.getCustomerId().equals(customerId)) {
                throw new BankException("Invalid payment account");
            }
            if (account.getAvailableBalance().compareTo(emi) < 0) {
                throw new InsufficientBalanceException("Insufficient account balance to pay EMI. EMI: ₹" + emi + ", Available: ₹" + account.getAvailableBalance());
            }

            // Debit EMI from account
            BigDecimal before = account.getBalance();
            BigDecimal after = before.subtract(emi);
            accountDAO.updateBalance(conn, accountId, after, after);

            // Update Loan Outstanding
            BigDecimal newOutstanding = loan.getOutstandingAmount().subtract(emi);
            loanDAO.updateOutstandingAmount(conn, loanId, newOutstanding);

            // Record Loan Payment
            BigDecimal monthlyInterest = loan.getInterestRate().divide(new BigDecimal("1200"), 4, RoundingMode.HALF_UP).multiply(loan.getOutstandingAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalComp = emi.subtract(monthlyInterest);
            if (principalComp.compareTo(BigDecimal.ZERO) < 0) principalComp = emi;

            String payRef = "SKEMI" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
            LoanPayment lp = new LoanPayment();
            lp.setLoanId(loanId);
            lp.setAmount(emi);
            lp.setPrincipalComponent(principalComp);
            lp.setInterestComponent(monthlyInterest);
            lp.setPaymentReference(payRef);
            Long paymentId = loanPaymentDAO.create(conn, lp);
            lp.setPaymentId(paymentId);

            // Transaction log
            Transaction txn = new Transaction();
            txn.setTransactionReference(payRef);
            txn.setAccountId(accountId);
            txn.setTransactionType(TransactionType.LOAN_EMI);
            txn.setAmount(emi);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription("EMI Payment for Loan " + loan.getLoanNumber());
            txn.setStatus(TransactionStatus.SUCCESS);
            transactionDAO.create(conn, txn);

            // Notification
            Notification n = new Notification();
            n.setUserId(customerId);
            n.setTitle("EMI Paid Successfully");
            n.setMessage("EMI payment of ₹" + emi + " processed for Loan " + loan.getLoanNumber() + ". Outstanding: ₹" + newOutstanding);
            n.setNotificationType("LOAN");
            notificationDAO.create(conn, n);

            conn.commit();
            return lp;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("EMI payment failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<LoanPayment> getLoanPayments(Long loanId) throws BankException {
        try {
            return loanPaymentDAO.findByLoanId(loanId);
        } catch (Exception e) {
            throw new BankException("Error fetching loan payment history", e);
        }
    }
}
