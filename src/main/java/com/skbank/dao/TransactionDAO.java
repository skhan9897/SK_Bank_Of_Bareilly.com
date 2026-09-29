package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Transaction;
import com.skbank.util.TransactionIdGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean createTransaction(Transaction txn) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_reference, account_id, type, direction, amount, balance_before, balance_after, sender_account, receiver_account, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String ref = txn.getTransactionId() != null ? txn.getTransactionId() : TransactionIdGenerator.generateTransactionId();
            String dir = txn.getDirection() != null ? txn.getDirection() : 
                         (("DEPOSIT".equalsIgnoreCase(txn.getType()) || "INTEREST_CREDIT".equalsIgnoreCase(txn.getType()) || "REFUND".equalsIgnoreCase(txn.getType())) ? "CREDIT" : "DEBIT");

            double before = txn.getBalanceBefore() > 0 ? txn.getBalanceBefore() : 
                            ("CREDIT".equalsIgnoreCase(dir) ? Math.max(0.0, txn.getBalanceAfter() - txn.getAmount()) : txn.getBalanceAfter() + txn.getAmount());

            ps.setString(1, ref);
            ps.setInt(2, txn.getAccountId());
            ps.setString(3, txn.getType() != null ? txn.getType() : "DEPOSIT");
            ps.setString(4, dir);
            ps.setDouble(5, txn.getAmount());
            ps.setDouble(6, before);
            ps.setDouble(7, txn.getBalanceAfter());
            ps.setString(8, txn.getSenderAccount());
            ps.setString(9, txn.getReceiverAccount());
            ps.setString(10, txn.getDescription());
            ps.setString(11, txn.getStatus() != null ? txn.getStatus() : "SUCCESS");
            return ps.executeUpdate() > 0;
        }
    }

    public List<Transaction> findByAccountId(int accountId, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, a.account_number " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "WHERE t.account_id = ? ORDER BY t.transaction_date DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, limit > 0 ? limit : 100);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        }
        return list;
    }

    public List<Transaction> findByCustomerId(String customerId, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, a.account_number " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "WHERE a.customer_id = ? ORDER BY t.transaction_date DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setInt(2, limit > 0 ? limit : 50);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        }
        return list;
    }

    public List<Transaction> filterTransactions(int accountId, String filterPeriod) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String intervalCondition = "";
        switch (filterPeriod != null ? filterPeriod : "30 Days") {
            case "Today": intervalCondition = "AND t.transaction_date >= CURDATE()"; break;
            case "7 Days": intervalCondition = "AND t.transaction_date >= NOW() - INTERVAL 7 DAY"; break;
            case "30 Days": intervalCondition = "AND t.transaction_date >= NOW() - INTERVAL 30 DAY"; break;
            case "3 Months": intervalCondition = "AND t.transaction_date >= NOW() - INTERVAL 3 MONTH"; break;
            case "6 Months": intervalCondition = "AND t.transaction_date >= NOW() - INTERVAL 6 MONTH"; break;
            default: intervalCondition = "AND t.transaction_date >= NOW() - INTERVAL 30 DAY"; break;
        }

        String sql = "SELECT t.*, a.account_number FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "WHERE t.account_id = ? " + intervalCondition + " ORDER BY t.transaction_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        }
        return list;
    }

    public List<Transaction> findAll(int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, a.account_number, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "ORDER BY t.transaction_date DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 200);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        }
        return list;
    }

    public int countTodayTransactions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE DATE(transaction_date) = CURDATE()";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getTodayTransactionVolume() throws SQLException {
        String sql = "SELECT SUM(amount) FROM transactions WHERE DATE(transaction_date) = CURDATE()";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getString("transaction_reference"));
        t.setAccountId(rs.getInt("account_id"));
        t.setType(rs.getString("type"));
        t.setDirection(rs.getString("direction"));
        t.setAmount(rs.getDouble("amount"));
        t.setBalanceBefore(rs.getDouble("balance_before"));
        t.setBalanceAfter(rs.getDouble("balance_after"));
        t.setSenderAccount(rs.getString("sender_account"));
        t.setReceiverAccount(rs.getString("receiver_account"));
        t.setReferenceNumber(rs.getString("transaction_reference"));
        t.setDescription(rs.getString("description"));
        t.setStatus(rs.getString("status"));
        t.setCreatedAt(rs.getTimestamp("transaction_date"));

        try { t.setAccountNumber(rs.getString("account_number")); } catch (Exception ignored) {}
        try { t.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}

        return t;
    }
}
