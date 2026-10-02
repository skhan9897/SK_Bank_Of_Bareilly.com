package com.skbank.dao.impl;

import com.skbank.dao.TransactionDAO;
import com.skbank.model.Transaction;
import com.skbank.model.TransactionStatus;
import com.skbank.model.TransactionType;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    private static final String SELECT_JOIN_SQL = 
        "SELECT t.*, a.account_number, ra.account_number AS related_account_number, c.full_name AS customer_name " +
        "FROM transactions t " +
        "JOIN accounts a ON t.account_id = a.account_id " +
        "JOIN customers c ON a.customer_id = c.customer_id " +
        "LEFT JOIN accounts ra ON t.related_account_id = ra.account_id ";

    @Override
    public Transaction findById(Long transactionId) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE t.transaction_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapTransaction(rs);
            }
        }
        return null;
    }

    @Override
    public Transaction findByReference(String reference) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE t.transaction_reference = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reference);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapTransaction(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Transaction transaction) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(conn, transaction);
        }
    }

    @Override
    public Long create(Connection conn, Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_reference, account_id, transaction_type, amount, balance_before, balance_after, related_account_id, description, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transaction.getTransactionReference());
            ps.setLong(2, transaction.getAccountId());
            ps.setString(3, transaction.getTransactionType().name());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setBigDecimal(5, transaction.getBalanceBefore());
            ps.setBigDecimal(6, transaction.getBalanceAfter());
            if (transaction.getRelatedAccountId() != null) {
                ps.setLong(7, transaction.getRelatedAccountId());
            } else {
                ps.setNull(7, Types.BIGINT);
            }
            ps.setString(8, transaction.getDescription());
            ps.setString(9, transaction.getStatus() != null ? transaction.getStatus().name() : TransactionStatus.SUCCESS.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId, int offset, int limit) throws SQLException {
        return findFiltered(accountId, null, null, null, offset, limit);
    }

    @Override
    public List<Transaction> findFiltered(Long accountId, Date startDate, Date endDate, String type, int offset, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOIN_SQL).append("WHERE t.account_id = ? ");

        if (startDate != null) sql.append("AND t.created_at >= ? ");
        if (endDate != null) sql.append("AND t.created_at <= ? ");
        if (type != null && !type.trim().isEmpty()) sql.append("AND t.transaction_type = ? ");

        sql.append("ORDER BY t.created_at DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            ps.setLong(p++, accountId);
            if (startDate != null) ps.setDate(p++, startDate);
            if (endDate != null) ps.setTimestamp(p++, new Timestamp(endDate.getTime() + (24 * 3600 * 1000L - 1)));
            if (type != null && !type.trim().isEmpty()) ps.setString(p++, type.trim());
            ps.setInt(p++, limit);
            ps.setInt(p, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        }
        return list;
    }

    @Override
    public long countFiltered(Long accountId, Date startDate, Date endDate, String type) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM transactions t WHERE t.account_id = ? ");

        if (startDate != null) sql.append("AND t.created_at >= ? ");
        if (endDate != null) sql.append("AND t.created_at <= ? ");
        if (type != null && !type.trim().isEmpty()) sql.append("AND t.transaction_type = ? ");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            ps.setLong(p++, accountId);
            if (startDate != null) ps.setDate(p++, startDate);
            if (endDate != null) ps.setTimestamp(p++, new Timestamp(endDate.getTime() + (24 * 3600 * 1000L - 1)));
            if (type != null && !type.trim().isEmpty()) ps.setString(p++, type.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public List<Transaction> findAllAdmin(int offset, int limit, String searchQuery, String typeFilter, Date startDate, Date endDate) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOIN_SQL).append("WHERE 1=1 ");

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (t.transaction_reference LIKE ? OR a.account_number LIKE ? OR c.full_name LIKE ?) ");
        }
        if (typeFilter != null && !typeFilter.trim().isEmpty()) {
            sql.append("AND t.transaction_type = ? ");
        }
        if (startDate != null) sql.append("AND t.created_at >= ? ");
        if (endDate != null) sql.append("AND t.created_at <= ? ");

        sql.append("ORDER BY t.created_at DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(p++, q);
                ps.setString(p++, q);
                ps.setString(p++, q);
            }
            if (typeFilter != null && !typeFilter.trim().isEmpty()) {
                ps.setString(p++, typeFilter.trim());
            }
            if (startDate != null) ps.setDate(p++, startDate);
            if (endDate != null) ps.setTimestamp(p++, new Timestamp(endDate.getTime() + (24 * 3600 * 1000L - 1)));
            ps.setInt(p++, limit);
            ps.setInt(p, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        }
        return list;
    }

    @Override
    public long countAllAdmin(String searchQuery, String typeFilter, Date startDate, Date endDate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM transactions t JOIN accounts a ON t.account_id = a.account_id JOIN customers c ON a.customer_id = c.customer_id WHERE 1=1 ");

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (t.transaction_reference LIKE ? OR a.account_number LIKE ? OR c.full_name LIKE ?) ");
        }
        if (typeFilter != null && !typeFilter.trim().isEmpty()) {
            sql.append("AND t.transaction_type = ? ");
        }
        if (startDate != null) sql.append("AND t.created_at >= ? ");
        if (endDate != null) sql.append("AND t.created_at <= ? ");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(p++, q);
                ps.setString(p++, q);
                ps.setString(p++, q);
            }
            if (typeFilter != null && !typeFilter.trim().isEmpty()) {
                ps.setString(p++, typeFilter.trim());
            }
            if (startDate != null) ps.setDate(p++, startDate);
            if (endDate != null) ps.setTimestamp(p++, new Timestamp(endDate.getTime() + (24 * 3600 * 1000L - 1)));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public long getTodaysTransactionCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE DATE(created_at) = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getLong("transaction_id"));
        t.setTransactionReference(rs.getString("transaction_reference"));
        t.setAccountId(rs.getLong("account_id"));
        t.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setBalanceBefore(rs.getBigDecimal("balance_before"));
        t.setBalanceAfter(rs.getBigDecimal("balance_after"));
        long relId = rs.getLong("related_account_id");
        if (!rs.wasNull()) {
            t.setRelatedAccountId(relId);
        }
        t.setDescription(rs.getString("description"));
        t.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        t.setCreatedAt(rs.getTimestamp("created_at"));

        try { t.setAccountNumber(rs.getString("account_number")); } catch (SQLException ignored) {}
        try { t.setRelatedAccountNumber(rs.getString("related_account_number")); } catch (SQLException ignored) {}
        try { t.setCustomerName(rs.getString("customer_name")); } catch (SQLException ignored) {}

        return t;
    }
}
