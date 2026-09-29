package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean createTransaction(Transaction txn) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, account_id, type, amount, balance_after, reference_number, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txn.getTransactionId());
            ps.setInt(2, txn.getAccountId());
            ps.setString(3, txn.getType());
            ps.setDouble(4, txn.getAmount());
            ps.setDouble(5, txn.getBalanceAfter());
            ps.setString(6, txn.getReferenceNumber());
            ps.setString(7, txn.getDescription());
            ps.setString(8, txn.getStatus() != null ? txn.getStatus() : "SUCCESS");
            return ps.executeUpdate() > 0;
        }
    }

    public List<Transaction> findByAccountId(int accountId, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, a.account_number " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "WHERE t.account_id = ? ORDER BY t.created_at DESC LIMIT ?";
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
                     "WHERE a.customer_id = ? ORDER BY t.created_at DESC LIMIT ?";
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
            case "Today": intervalCondition = "AND t.created_at >= CURDATE()"; break;
            case "7 Days": intervalCondition = "AND t.created_at >= NOW() - INTERVAL 7 DAY"; break;
            case "30 Days": intervalCondition = "AND t.created_at >= NOW() - INTERVAL 30 DAY"; break;
            case "3 Months": intervalCondition = "AND t.created_at >= NOW() - INTERVAL 3 MONTH"; break;
            case "6 Months": intervalCondition = "AND t.created_at >= NOW() - INTERVAL 6 MONTH"; break;
            default: intervalCondition = "AND t.created_at >= NOW() - INTERVAL 30 DAY"; break;
        }

        String sql = "SELECT t.*, a.account_number FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "WHERE t.account_id = ? " + intervalCondition + " ORDER BY t.created_at DESC";

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
                     "ORDER BY t.created_at DESC LIMIT ?";
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
        String sql = "SELECT COUNT(*) FROM transactions WHERE DATE(created_at) = CURDATE()";
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
        String sql = "SELECT SUM(amount) FROM transactions WHERE DATE(created_at) = CURDATE()";
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
        t.setTransactionId(rs.getString("transaction_id"));
        t.setAccountId(rs.getInt("account_id"));
        t.setType(rs.getString("type"));
        t.setAmount(rs.getDouble("amount"));
        t.setBalanceAfter(rs.getDouble("balance_after"));
        t.setReferenceNumber(rs.getString("reference_number"));
        t.setDescription(rs.getString("description"));
        t.setStatus(rs.getString("status"));
        t.setCreatedAt(rs.getTimestamp("created_at"));

        try { t.setAccountNumber(rs.getString("account_number")); } catch (Exception ignored) {}
        try { t.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}

        return t;
    }
}
