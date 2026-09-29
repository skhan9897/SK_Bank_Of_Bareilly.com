package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CardTransactionDAO {

    public boolean recordCardTransaction(long cardId, String merchant, double amount, String txnType) throws SQLException {
        String sql = "INSERT INTO card_transactions (card_id, merchant_name, amount, transaction_type, status) VALUES (?, ?, ?, ?, 'SUCCESS')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setString(2, merchant);
            ps.setDouble(3, amount);
            ps.setString(4, txnType);
            return ps.executeUpdate() > 0;
        }
    }
}
