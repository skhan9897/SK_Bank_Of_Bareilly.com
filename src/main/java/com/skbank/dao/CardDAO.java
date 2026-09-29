package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    public List<Card> findByCustomerId(String customerId) throws SQLException {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT c.*, a.account_number FROM cards c " +
                     "JOIN accounts a ON c.account_id = a.account_id WHERE c.customer_id = ? ORDER BY c.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCard(rs));
                }
            }
        }
        return list;
    }

    public Card findById(int cardId) throws SQLException {
        String sql = "SELECT c.*, a.account_number FROM cards c " +
                     "JOIN accounts a ON c.account_id = a.account_id WHERE c.card_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCard(rs);
                }
            }
        }
        return null;
    }

    public boolean createCard(Card card) throws SQLException {
        String sql = "INSERT INTO cards (customer_id, account_id, card_number, card_holder_name, card_type, expiry_date, cvv_hash, pin_hash, daily_limit, credit_limit, available_limit, outstanding, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, card.getCustomerId());
            ps.setInt(2, card.getAccountId());
            ps.setString(3, card.getCardNumber());
            ps.setString(4, card.getCardHolderName());
            ps.setString(5, card.getCardType());
            ps.setString(6, card.getExpiryDate());
            ps.setString(7, card.getCvvHash());
            ps.setString(8, card.getPinHash());
            ps.setDouble(9, card.getDailyLimit() > 0 ? card.getDailyLimit() : 50000.0);
            ps.setDouble(10, card.getCreditLimit());
            ps.setDouble(11, card.getAvailableLimit());
            ps.setDouble(12, card.getOutstanding());
            ps.setString(13, card.getStatus() != null ? card.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCardStatus(int cardId, String status) throws SQLException {
        String sql = "UPDATE cards SET status = ? WHERE card_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, cardId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePin(int cardId, String newPinHash) throws SQLException {
        String sql = "UPDATE cards SET pin_hash = ? WHERE card_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPinHash);
            ps.setInt(2, cardId);
            return ps.executeUpdate() > 0;
        }
    }

    private Card mapCard(ResultSet rs) throws SQLException {
        Card c = new Card();
        c.setCardId(rs.getInt("card_id"));
        c.setCustomerId(rs.getString("customer_id"));
        c.setAccountId(rs.getInt("account_id"));
        c.setCardNumber(rs.getString("card_number"));
        c.setCardHolderName(rs.getString("card_holder_name"));
        c.setCardType(rs.getString("card_type"));
        c.setExpiryDate(rs.getString("expiry_date"));
        c.setCvvHash(rs.getString("cvv_hash"));
        c.setPinHash(rs.getString("pin_hash"));
        c.setDailyLimit(rs.getDouble("daily_limit"));
        c.setCreditLimit(rs.getDouble("credit_limit"));
        c.setAvailableLimit(rs.getDouble("available_limit"));
        c.setOutstanding(rs.getDouble("outstanding"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));

        try { c.setAccountNumber(rs.getString("account_number")); } catch (Exception ignored) {}

        return c;
    }
}
