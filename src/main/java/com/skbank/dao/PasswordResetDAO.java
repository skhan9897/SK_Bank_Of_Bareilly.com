package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PasswordResetDAO {

    public boolean createResetToken(long userId, String tokenHash, int expiryHours) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens (user_id, token_hash, expires_at) VALUES (?, ?, NOW() + INTERVAL ? HOUR)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, tokenHash);
            ps.setInt(3, expiryHours);
            return ps.executeUpdate() > 0;
        }
    }
}
