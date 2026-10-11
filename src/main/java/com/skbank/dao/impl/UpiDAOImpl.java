package com.skbank.dao.impl;

import com.skbank.dao.UpiDAO;
import com.skbank.model.UpiAccount;
import com.skbank.util.DatabaseConnection;

import java.sql.*;

public class UpiDAOImpl implements UpiDAO {

    private void ensureColumnsExist(Connection conn) {
        String[] sqls = new String[] {
            "ALTER TABLE upi_accounts ADD COLUMN upi_pin_hash VARCHAR(255) NULL",
            "ALTER TABLE upi_accounts ADD COLUMN pin_hash VARCHAR(255) NULL",
            "ALTER TABLE upi_accounts ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"
        };
        for (String s : sqls) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(s);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public UpiAccount findByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT * FROM upi_accounts WHERE customer_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUpi(rs);
            }
        }
        return null;
    }

    @Override
    public UpiAccount findByUpiAddress(String upiAddress) throws SQLException {
        String sql = "SELECT * FROM upi_accounts WHERE upi_address = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, upiAddress);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUpi(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(UpiAccount upi) throws SQLException {
        String sqlPrimary = "INSERT INTO upi_accounts (customer_id, account_id, upi_address, upi_pin_hash, status, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, NOW())";
        String sqlFallback = "INSERT INTO upi_accounts (customer_id, account_id, upi_address, pin_hash, status, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureColumnsExist(conn);
            try {
                return executeInsert(conn, sqlPrimary, upi);
            } catch (SQLException e) {
                return executeInsert(conn, sqlFallback, upi);
            }
        }
    }

    private Long executeInsert(Connection conn, String sql, UpiAccount upi) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, upi.getCustomerId());
            ps.setLong(2, upi.getAccountId());
            ps.setString(3, upi.getUpiAddress());
            ps.setString(4, upi.getPinHash());
            ps.setString(5, upi.getStatus() != null ? upi.getStatus() : "ACTIVE");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updatePin(Long upiId, String newPinHash) throws SQLException {
        String sqlPrimary = "UPDATE upi_accounts SET upi_pin_hash = ? WHERE upi_account_id = ? OR upi_id = ?";
        String sqlFallback = "UPDATE upi_accounts SET pin_hash = ? WHERE upi_account_id = ? OR upi_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureColumnsExist(conn);
            try (PreparedStatement ps = conn.prepareStatement(sqlPrimary)) {
                ps.setString(1, newPinHash);
                ps.setLong(2, upiId);
                ps.setLong(3, upiId);
                if (ps.executeUpdate() > 0) return true;
            } catch (Exception ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(sqlFallback)) {
                ps.setString(1, newPinHash);
                ps.setLong(2, upiId);
                ps.setLong(3, upiId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    @Override
    public boolean updateUpiAddress(Long upiId, String newUpiAddress) throws SQLException {
        String sql = "UPDATE upi_accounts SET upi_address = ? WHERE upi_account_id = ? OR upi_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newUpiAddress);
            ps.setLong(2, upiId);
            ps.setLong(3, upiId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long upiId, String status) throws SQLException {
        String sql = "UPDATE upi_accounts SET status = ? WHERE upi_account_id = ? OR upi_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, upiId);
            ps.setLong(3, upiId);
            return ps.executeUpdate() > 0;
        }
    }

    private UpiAccount mapUpi(ResultSet rs) throws SQLException {
        UpiAccount u = new UpiAccount();
        try { u.setUpiId(rs.getLong("upi_account_id")); } catch (Exception e) { try { u.setUpiId(rs.getLong("upi_id")); } catch (Exception ignored) {} }
        u.setCustomerId(rs.getLong("customer_id"));
        u.setAccountId(rs.getLong("account_id"));
        u.setUpiAddress(rs.getString("upi_address"));
        try { u.setPinHash(rs.getString("upi_pin_hash")); } catch (Exception e) { try { u.setPinHash(rs.getString("pin_hash")); } catch (Exception ignored) {} }
        try { u.setStatus(rs.getString("status")); } catch (Exception ignored) {}
        try { u.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        return u;
    }
}
