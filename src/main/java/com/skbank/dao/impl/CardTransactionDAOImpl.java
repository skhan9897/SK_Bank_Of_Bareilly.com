package com.skbank.dao.impl;

import com.skbank.dao.CardTransactionDAO;
import com.skbank.model.CardTransaction;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardTransactionDAOImpl implements CardTransactionDAO {

    @Override
    public List<CardTransaction> findByCardId(Long cardId) throws SQLException {
        List<CardTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM card_transactions WHERE card_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CardTransaction ct = new CardTransaction();
                    ct.setCardTransactionId(rs.getLong("card_transaction_id"));
                    ct.setCardId(rs.getLong("card_id"));
                    ct.setAmount(rs.getBigDecimal("amount"));
                    ct.setMerchantName(rs.getString("merchant_name"));
                    ct.setTransactionReference(rs.getString("transaction_reference"));
                    ct.setStatus(rs.getString("status"));
                    ct.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(ct);
                }
            }
        }
        return list;
    }

    @Override
    public Long create(CardTransaction ct) throws SQLException {
        String sql = "INSERT INTO card_transactions (card_id, amount, merchant_name, transaction_reference, status, created_at) " +
                     "VALUES (?, ?, ?, ?, 'SUCCESS', NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ct.getCardId());
            ps.setBigDecimal(2, ct.getAmount());
            ps.setString(3, ct.getMerchantName());
            ps.setString(4, ct.getTransactionReference());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }
}
