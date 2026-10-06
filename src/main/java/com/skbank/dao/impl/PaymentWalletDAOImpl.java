package com.skbank.dao.impl;

import com.skbank.dao.PaymentWalletDAO;
import com.skbank.model.PaymentWallet;
import com.skbank.util.DatabaseConnection;

import java.sql.*;

public class PaymentWalletDAOImpl implements PaymentWalletDAO {

    @Override
    public PaymentWallet findByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT * FROM payment_wallets WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapWallet(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Connection conn, PaymentWallet wallet) throws SQLException {
        String sql = "INSERT INTO payment_wallets (customer_id, wallet_number, balance, status, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, wallet.getCustomerId());
            ps.setString(2, wallet.getWalletNumber());
            ps.setBigDecimal(3, wallet.getBalance());
            ps.setString(4, wallet.getStatus() != null ? wallet.getStatus() : "ACTIVE");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    private PaymentWallet mapWallet(ResultSet rs) throws SQLException {
        PaymentWallet w = new PaymentWallet();
        w.setWalletId(rs.getLong("wallet_id"));
        w.setCustomerId(rs.getLong("customer_id"));
        w.setWalletNumber(rs.getString("wallet_number"));
        w.setBalance(rs.getBigDecimal("balance"));
        try { w.setStatus(rs.getString("status")); } catch (Exception ignored) {}
        try { w.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        return w;
    }
}
