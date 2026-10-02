package com.skbank.dao.impl;

import com.skbank.dao.UpiDAO;
import com.skbank.model.UpiAccount;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpiDAOImpl implements UpiDAO {

    private static final String SELECT_JOIN_SQL = 
        "SELECT u.*, a.account_number FROM upi_accounts u JOIN accounts a ON u.account_id = a.account_id ";

    @Override
    public UpiAccount findByUpiAddress(String upiAddress) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE u.upi_address = ?";
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
    public UpiAccount findByCustomerId(Long customerId) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE u.customer_id = ? AND u.status = 'ACTIVE' LIMIT 1";
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
    public Long create(UpiAccount u) throws SQLException {
        String sql = "INSERT INTO upi_accounts (customer_id, account_id, upi_address, upi_pin_hash, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, 'ACTIVE', NOW(), NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, u.getCustomerId());
            ps.setLong(2, u.getAccountId());
            ps.setString(3, u.getUpiAddress());
            ps.setString(4, u.getUpiPinHash());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateUpiAddress(Long upiAccountId, String newAddress) throws SQLException {
        String sql = "UPDATE upi_accounts SET upi_address = ?, updated_at = NOW() WHERE upi_account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newAddress);
            ps.setLong(2, upiAccountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUpiPin(Long upiAccountId, String newPinHash) throws SQLException {
        String sql = "UPDATE upi_accounts SET upi_pin_hash = ?, updated_at = NOW() WHERE upi_account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPinHash);
            ps.setLong(2, upiAccountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long upiAccountId, String status) throws SQLException {
        String sql = "UPDATE upi_accounts SET status = ?, updated_at = NOW() WHERE upi_account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, upiAccountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<UpiAccount> findAllByCustomerId(Long customerId) throws SQLException {
        List<UpiAccount> list = new ArrayList<>();
        String sql = SELECT_JOIN_SQL + "WHERE u.customer_id = ? ORDER BY u.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUpi(rs));
                }
            }
        }
        return list;
    }

    private UpiAccount mapUpi(ResultSet rs) throws SQLException {
        UpiAccount u = new UpiAccount();
        u.setUpiAccountId(rs.getLong("upi_account_id"));
        u.setCustomerId(rs.getLong("customer_id"));
        u.setAccountId(rs.getLong("account_id"));
        u.setUpiAddress(rs.getString("upi_address"));
        u.setUpiPinHash(rs.getString("upi_pin_hash"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        try { u.setAccountNumber(rs.getString("account_number")); } catch (SQLException ignored) {}
        return u;
    }
}
