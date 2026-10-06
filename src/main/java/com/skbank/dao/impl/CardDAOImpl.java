package com.skbank.dao.impl;

import com.skbank.dao.CardDAO;
import com.skbank.model.Card;
import com.skbank.model.CardStatus;
import com.skbank.model.CardType;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAOImpl implements CardDAO {

    @Override
    public Card findById(Long cardId) throws SQLException {
        String sql = "SELECT * FROM cards WHERE card_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCard(rs);
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public List<Card> findByCustomerId(Long customerId) throws SQLException {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE customer_id = ? ORDER BY card_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCard(rs));
            }
        } catch (Exception ignored) {}
        return list;
    }

    @Override
    public Long create(Card card) throws SQLException {
        String sql = "INSERT INTO cards (customer_id, account_id, card_number, card_type, expiry_date, card_status) " +
                     "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, card.getCustomerId());
            ps.setLong(2, card.getAccountId());
            ps.setString(3, card.getCardNumber());
            ps.setString(4, card.getCardType() != null ? card.getCardType().name() : CardType.DEBIT.name());
            ps.setDate(5, card.getExpiryDate());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateStatus(Long cardId, String status) throws SQLException {
        String sql = "UPDATE cards SET card_status = ? WHERE card_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, cardId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Card> findAllAdmin(int offset, int limit) throws SQLException {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT * FROM cards ORDER BY card_id DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCard(rs));
            }
        } catch (Exception ignored) {}
        return list;
    }

    @Override
    public long countAllAdmin() throws SQLException {
        String sql = "SELECT COUNT(*) FROM cards";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (Exception ignored) {}
        return 0;
    }

    private Card mapCard(ResultSet rs) throws SQLException {
        Card cd = new Card();
        cd.setCardId(rs.getLong("card_id"));
        cd.setCustomerId(rs.getLong("customer_id"));
        cd.setAccountId(rs.getLong("account_id"));
        cd.setCardNumber(rs.getString("card_number"));
        try { cd.setCardType(CardType.valueOf(rs.getString("card_type"))); } catch (Exception ignored) { cd.setCardType(CardType.DEBIT); }
        try { cd.setExpiryDate(rs.getDate("expiry_date")); } catch (Exception ignored) {}
        try { cd.setCardStatus(CardStatus.valueOf(rs.getString("card_status"))); } catch (Exception ignored) { cd.setCardStatus(CardStatus.ACTIVE); }
        try { cd.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        try { cd.setAccountNumber(rs.getString("account_number")); } catch (Exception ignored) {}
        try { cd.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}
        return cd;
    }
}
