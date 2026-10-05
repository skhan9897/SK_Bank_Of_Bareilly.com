package com.skbank.dao.impl;

import com.skbank.dao.PaymentWalletDAO;
import com.skbank.model.PaymentWallet;
import com.skbank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;

public class PaymentWalletDAOImpl implements PaymentWalletDAO {

    @Override
    public PaymentWallet findByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT * FROM payment_wallets WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapWallet(rs);
            }
        }
        return null;
    }

    @Override
    public PaymentWallet findForUpdate(Connection conn, String customerId) throws SQLException {
        String sql = "SELECT * FROM payment_wallets WHERE customer_id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapWallet(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Connection conn, PaymentWallet w) throws SQLException {
        String sql = "INSERT INTO payment_wallets (customer_id, wallet_number, balance, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, 'ACTIVE', NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, w.getCustomerId());
            ps.setString(2, w.getWalletNumber());
            ps.setBigDecimal(3, w.getBalance() != null ? w.getBalance() : BigDecimal.ZERO);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateBalance(Connection conn, Long walletId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE payment_wallets SET balance = ?, updated_at = NOW() WHERE wallet_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setLong(2, walletId);
            return ps.executeUpdate() > 0;
        }
    }

    private PaymentWallet mapWallet(ResultSet rs) throws SQLException {
        PaymentWallet w = new PaymentWallet();
        w.setWalletId(rs.getLong("wallet_id"));
        w.setCustomerId(rs.getString("customer_id"));
        w.setWalletNumber(rs.getString("wallet_number"));
        w.setBalance(rs.getBigDecimal("balance"));
        w.setStatus(rs.getString("status"));
        w.setCreatedAt(rs.getTimestamp("created_at"));
        w.setUpdatedAt(rs.getTimestamp("updated_at"));
        return w;
    }
}
