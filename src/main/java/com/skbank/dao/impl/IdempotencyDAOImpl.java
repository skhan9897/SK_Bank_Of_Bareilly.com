package com.skbank.dao.impl;

import com.skbank.dao.IdempotencyDAO;
import com.skbank.util.DatabaseConnection;

import java.sql.*;

public class IdempotencyDAOImpl implements IdempotencyDAO {

    @Override
    public String getExistingResponse(String key) throws SQLException {
        if (key == null || key.trim().isEmpty()) return null;
        String sql = "SELECT response_payload FROM payment_idempotency WHERE idempotency_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("response_payload");
            }
        }
        return null;
    }

    @Override
    public void saveIdempotency(Connection conn, String key, String refNumber, String jsonResponse) throws SQLException {
        if (key == null || key.trim().isEmpty()) return;
        String sql = "INSERT INTO payment_idempotency (idempotency_key, reference_number, response_payload, created_at) " +
                     "VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key.trim());
            ps.setString(2, refNumber);
            ps.setString(3, jsonResponse);
            ps.executeUpdate();
        }
    }
}
