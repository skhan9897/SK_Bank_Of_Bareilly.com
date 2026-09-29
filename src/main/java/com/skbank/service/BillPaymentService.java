package com.skbank.service;

import com.skbank.config.DatabaseConfig;
import com.skbank.dao.AccountDAO;
import com.skbank.dao.BillPaymentDAO;
import com.skbank.dao.TransactionDAO;
import com.skbank.model.Account;
import com.skbank.model.BillPayment;
import com.skbank.model.Transaction;
import com.skbank.util.TransactionIdGenerator;

import java.sql.SQLException;
import java.util.List;

public class BillPaymentService {

    private final BillPaymentDAO billPaymentDAO = new BillPaymentDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public BillPayment processBillPayment(String customerId, int accountId, String category, String provider, String consumerNo, double amount) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Bill payment amount must be greater than zero.");
        }

        Account account = accountDAO.findByAccountId(accountId);
        if (account == null || !"ACTIVE".equals(account.getStatus())) {
            throw new IllegalArgumentException("Selected payment account is not active.");
        }
        if (account.getBalance() < amount) {
            throw new IllegalStateException("Insufficient balance to complete bill payment.");
        }

        // Deduct balance
        double newBalance = account.getBalance() - amount;
        String updateAccSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(updateAccSql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }

        String refNo = TransactionIdGenerator.generateReferenceNumber();

        // Create Bill Payment Record
        BillPayment bp = new BillPayment();
        bp.setCustomerId(customerId);
        bp.setAccountId(accountId);
        bp.setBillCategory(category);
        bp.setProvider(provider);
        bp.setConsumerNumber(consumerNo);
        bp.setAmount(amount);
        bp.setReferenceNumber(refNo);
        bp.setStatus("SUCCESS");

        boolean created = billPaymentDAO.createBillPayment(bp);

        if (created) {
            // Record Transaction
            Transaction txn = new Transaction();
            txn.setTransactionId(TransactionIdGenerator.generateTransactionId());
            txn.setAccountId(accountId);
            txn.setType("BILL_PAYMENT");
            txn.setAmount(amount);
            txn.setBalanceAfter(newBalance);
            txn.setReferenceNumber(refNo);
            txn.setDescription(category + " Payment - " + provider + " (" + consumerNo + ")");
            txn.setStatus("SUCCESS");
            transactionDAO.createTransaction(txn);
        }

        return bp;
    }

    public List<BillPayment> getCustomerBillPayments(String customerId) throws SQLException {
        return billPaymentDAO.findByCustomerId(customerId);
    }
}
