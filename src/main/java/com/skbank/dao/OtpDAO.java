package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class OtpDAO {

    public boolean saveOtp(Long userId, String mobile, String email, String otpHash, String purpose, int expiryMinutes) throws SQLException {
        String sql = "INSERT INTO otp_verifications (user_id, mobile, email, otp_hash, purpose, expires_at) VALUES (?, ?, ?, ?, ?, NOW() + INTERVAL ? MINUTE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) ps.setLong(1, userId); else ps.setNull(1, Types.BIGINT);
            ps.setString(2, mobile);
            ps.setString(3, email);
            ps.setString(4, otpHash);
            ps.setString(5, purpose);
            ps.setInt(6, expiryMinutes);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean verifyOtp(String otpHash, String purpose) throws SQLException {
        String sql = "UPDATE otp_verifications SET verified = 1 WHERE otp_hash = ? AND purpose = ? AND expires_at >= NOW() AND verified = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, otpHash);
            ps.setString(2, purpose);
            return ps.executeUpdate() > 0;
        }
    }
}
