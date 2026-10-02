package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.TransactionService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.SystemSettingsUtil;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TransactionServiceImpl implements TransactionService {

    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public Transaction processWithdrawal(Long accountId, BigDecimal amount, String description) throws BankException {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankException("Withdrawal amount must be greater than zero");
        }

        BigDecimal maxSingle = SystemSettingsUtil.getSettingAsBigDecimal("MAX_WITHDRAWAL_AMOUNT", new BigDecimal("50000.00"));
        if (amount.compareTo(maxSingle) > 0) {
            throw new BankException("Amount exceeds maximum single withdrawal limit of ₹" + maxSingle);
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Lock account for update
            Account account = accountDAO.findForUpdate(conn, accountId);
            if (account == null) {
                throw new BankException("Account not found");
            }
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Account is " + account.getStatus().name().toLowerCase() + " and cannot transact.");
            }
            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient available balance. Available: ₹" + account.getAvailableBalance());
            }

            BigDecimal before = account.getBalance();
            BigDecimal after = before.subtract(amount);

            // Update account balance
            accountDAO.updateBalance(conn, accountId, after, after);

            // Record transaction
            String ref = "SKWD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Transaction txn = new Transaction();
            txn.setTransactionReference(ref);
            txn.setAccountId(accountId);
            txn.setTransactionType(TransactionType.WITHDRAWAL);
            txn.setAmount(amount);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription(description != null && !description.trim().isEmpty() ? description : "Self withdrawal (Demo internal operation)");
            txn.setStatus(TransactionStatus.SUCCESS);

            Long txnId = transactionDAO.create(conn, txn);
            txn.setTransactionId(txnId);

            // Send notification
            Customer customer = customerDAO.findById(account.getCustomerId());
            if (customer != null) {
                Notification n = new Notification();
                n.setUserId(customer.getUserId());
                n.setTitle("Withdrawal Successful");
                n.setMessage("₹" + amount + " withdrawn from account " + account.getMaskedAccountNumber() + ". Ref: " + ref);
                n.setNotificationType("WITHDRAWAL");
                notificationDAO.create(conn, n);
            }

            conn.commit();
            return txn;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Withdrawal failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<Transaction> getAccountTransactions(Long accountId, int page, int pageSize) throws BankException {
        return getFilteredTransactions(accountId, null, null, null, page, pageSize);
    }

    @Override
    public List<Transaction> getFilteredTransactions(Long accountId, Date startDate, Date endDate, String type, int page, int pageSize) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return transactionDAO.findFiltered(accountId, startDate, endDate, type, offset, pageSize);
        } catch (Exception e) {
            throw new BankException("Error fetching transactions", e);
        }
    }

    @Override
    public long countFilteredTransactions(Long accountId, Date startDate, Date endDate, String type) throws BankException {
        try {
            return transactionDAO.countFiltered(accountId, startDate, endDate, type);
        } catch (Exception e) {
            throw new BankException("Error counting transactions", e);
        }
    }

    @Override
    public Transaction getTransactionByReference(String reference) throws BankException {
        try {
            Transaction txn = transactionDAO.findByReference(reference);
            if (txn == null) throw new BankException("Transaction with reference " + reference + " not found");
            return txn;
        } catch (Exception e) {
            throw new BankException("Error fetching transaction details", e);
        }
    }
}
