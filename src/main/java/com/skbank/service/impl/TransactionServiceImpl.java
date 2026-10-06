package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.TransactionService;
import com.skbank.util.DatabaseConnection;
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
        return processWithdrawal(accountId, amount, description, null);
    }

    @Override
    public Transaction processWithdrawal(Long accountId, BigDecimal amount, String description, Long customerId) throws BankException {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankException("Withdrawal amount must be greater than zero");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findForUpdate(conn, accountId);
            if (account == null) throw new BankException("Account not found");
            if (customerId != null && !account.getCustomerId().equals(customerId)) {
                throw new BankException("Unauthorized account withdrawal");
            }
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Account is not active");
            }
            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient available balance for withdrawal");
            }

            BigDecimal before = account.getBalance();
            BigDecimal after = before.subtract(amount);

            accountDAO.updateBalance(conn, accountId, after, after);

            String ref = "SKWD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
            Transaction txn = new Transaction();
            txn.setTransactionReference(ref);
            txn.setAccountId(accountId);
            txn.setTransactionType(TransactionType.WITHDRAWAL);
            txn.setAmount(amount);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription(description != null && !description.trim().isEmpty() ? description : "Cash Withdrawal");
            txn.setStatus(TransactionStatus.SUCCESS);

            Long txnId = transactionDAO.create(conn, txn);
            txn.setTransactionId(txnId);

            Customer cust = customerDAO.findById(account.getCustomerId());
            if (cust != null) {
                Notification n = new Notification();
                n.setUserId(cust.getUserId());
                n.setTitle("Cash Withdrawal Debited");
                n.setMessage("₹" + amount + " debited from account " + account.getMaskedAccountNumber() + ". Ref: " + ref);
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
        try {
            int offset = (page - 1) * pageSize;
            return transactionDAO.findByAccountId(accountId, offset, pageSize);
        } catch (Exception e) {
            throw new BankException("Error fetching transactions", e);
        }
    }

    @Override
    public long countAccountTransactions(Long accountId) throws BankException {
        try {
            return transactionDAO.countByAccountId(accountId);
        } catch (Exception e) {
            throw new BankException("Error counting transactions", e);
        }
    }

    @Override
    public List<Transaction> getFilteredTransactions(Long accountId, Date startDate, Date endDate, String type, int page, int pageSize) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return transactionDAO.findFiltered(accountId, startDate, endDate, type, offset, pageSize);
        } catch (Exception e) {
            throw new BankException("Error fetching filtered transactions", e);
        }
    }

    @Override
    public long countFilteredTransactions(Long accountId, Date startDate, Date endDate, String type) throws BankException {
        try {
            return transactionDAO.countFiltered(accountId, startDate, endDate, type);
        } catch (Exception e) {
            throw new BankException("Error counting filtered transactions", e);
        }
    }

    @Override
    public Transaction getTransactionByReference(String reference) throws BankException {
        try {
            return transactionDAO.findByReference(reference);
        } catch (Exception e) {
            throw new BankException("Error fetching transaction details", e);
        }
    }
}
