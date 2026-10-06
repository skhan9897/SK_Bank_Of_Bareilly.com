package com.skbank.dao.impl;

import com.skbank.dao.TransactionDAO;
import com.skbank.model.Transaction;
import com.skbank.model.TransactionStatus;
import com.skbank.model.TransactionType;
import com.skbank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public Transaction findById(Long transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
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
        String sql = "SELECT * FROM transactions WHERE transaction_reference = ?";
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
    public Long create(Transaction t) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(conn, t);
        }
    }

    @Override
    public Long create(Connection conn, Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_reference, account_id, related_account_id, transaction_type, amount, balance_before, balance_after, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTransactionReference());
            ps.setLong(2, t.getAccountId());
            if (t.getRelatedAccountId() != null) ps.setLong(3, t.getRelatedAccountId());
            else ps.setNull(3, Types.BIGINT);
            ps.setString(4, t.getTransactionType() != null ? t.getTransactionType().name() : TransactionType.TRANSFER.name());
            ps.setBigDecimal(5, t.getAmount());
            ps.setBigDecimal(6, t.getBalanceBefore() != null ? t.getBalanceBefore() : BigDecimal.ZERO);
            ps.setBigDecimal(7, t.getBalanceAfter() != null ? t.getBalanceAfter() : BigDecimal.ZERO);
            ps.setString(8, t.getDescription());
            ps.setString(9, t.getStatus() != null ? t.getStatus().name() : TransactionStatus.SUCCESS.name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            // Fallback SQL if DB schema lacks balance_before or balance_after columns
            String fallbackSql = "INSERT INTO transactions (transaction_reference, account_id, related_account_id, transaction_type, amount, description, status) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(fallbackSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, t.getTransactionReference());
                ps.setLong(2, t.getAccountId());
                if (t.getRelatedAccountId() != null) ps.setLong(3, t.getRelatedAccountId());
                else ps.setNull(3, Types.BIGINT);
                ps.setString(4, t.getTransactionType() != null ? t.getTransactionType().name() : TransactionType.TRANSFER.name());
                ps.setBigDecimal(5, t.getAmount());
                ps.setString(6, t.getDescription());
                ps.setString(7, t.getStatus() != null ? t.getStatus().name() : TransactionStatus.SUCCESS.name());

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        }
        return null;
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId, int offset, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_id DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        }
        return list;
    }

    @Override
    public long countByAccountId(Long accountId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public List<Transaction> findFiltered(Long accountId, Date startDate, Date endDate, String type, int offset, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE account_id = ? ");
        if (startDate != null) sql.append("AND created_at >= ? ");
        if (endDate != null) sql.append("AND created_at <= ? ");
        if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type.trim())) sql.append("AND transaction_type = ? ");
        sql.append("ORDER BY transaction_id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, accountId);
            if (startDate != null) ps.setTimestamp(idx++, new Timestamp(startDate.getTime()));
            if (endDate != null) ps.setTimestamp(idx++, new Timestamp(endDate.getTime() + 86399000L));
            if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type.trim())) ps.setString(idx++, type.trim());
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        }
        return list;
    }

    @Override
    public long countFiltered(Long accountId, Date startDate, Date endDate, String type) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM transactions WHERE account_id = ? ");
        if (startDate != null) sql.append("AND created_at >= ? ");
        if (endDate != null) sql.append("AND created_at <= ? ");
        if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type.trim())) sql.append("AND transaction_type = ? ");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, accountId);
            if (startDate != null) ps.setTimestamp(idx++, new Timestamp(startDate.getTime()));
            if (endDate != null) ps.setTimestamp(idx++, new Timestamp(endDate.getTime() + 86399000L));
            if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type.trim())) ps.setString(idx++, type.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public List<Transaction> findAllAdmin(int offset, int limit, String searchQuery, String typeFilter, Date startDate, Date endDate) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE 1=1 ");
        if (searchQuery != null && !searchQuery.trim().isEmpty()) sql.append("AND (transaction_reference LIKE ? OR description LIKE ?) ");
        if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter.trim())) sql.append("AND transaction_type = ? ");
        if (startDate != null) sql.append("AND created_at >= ? ");
        if (endDate != null) sql.append("AND created_at <= ? ");
        sql.append("ORDER BY transaction_id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(idx++, q);
                ps.setString(idx++, q);
            }
            if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter.trim())) ps.setString(idx++, typeFilter.trim());
            if (startDate != null) ps.setTimestamp(idx++, new Timestamp(startDate.getTime()));
            if (endDate != null) ps.setTimestamp(idx++, new Timestamp(endDate.getTime() + 86399000L));
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        }
        return list;
    }

    @Override
    public long countAllAdmin(String searchQuery, String typeFilter, Date startDate, Date endDate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM transactions WHERE 1=1 ");
        if (searchQuery != null && !searchQuery.trim().isEmpty()) sql.append("AND (transaction_reference LIKE ? OR description LIKE ?) ");
        if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter.trim())) sql.append("AND transaction_type = ? ");
        if (startDate != null) sql.append("AND created_at >= ? ");
        if (endDate != null) sql.append("AND created_at <= ? ");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(idx++, q);
                ps.setString(idx++, q);
            }
            if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter.trim())) ps.setString(idx++, typeFilter.trim());
            if (startDate != null) ps.setTimestamp(idx++, new Timestamp(startDate.getTime()));
            if (endDate != null) ps.setTimestamp(idx++, new Timestamp(endDate.getTime() + 86399000L));

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
        } catch (Exception e) {
            String fallbackSql = "SELECT COUNT(*) FROM transactions";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(fallbackSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getLong("transaction_id"));
        t.setTransactionReference(rs.getString("transaction_reference"));
        t.setAccountId(rs.getLong("account_id"));
        long relAcc = rs.getLong("related_account_id");
        if (!rs.wasNull()) t.setRelatedAccountId(relAcc);
        try { t.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type"))); } catch (Exception ignored) {}
        t.setAmount(rs.getBigDecimal("amount"));
        try { t.setBalanceBefore(rs.getBigDecimal("balance_before")); } catch (Exception ignored) {}
        try { t.setBalanceAfter(rs.getBigDecimal("balance_after")); } catch (Exception ignored) {}
        t.setDescription(rs.getString("description"));
        try { t.setStatus(TransactionStatus.valueOf(rs.getString("status"))); } catch (Exception ignored) {}
        try { t.setTransactionTime(rs.getTimestamp("transaction_time")); } catch (Exception ignored) {}
        try { t.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        return t;
    }
}
