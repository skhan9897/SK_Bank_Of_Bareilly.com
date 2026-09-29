package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public boolean createAdmin(long userId, String fullName, String email, String phone, String department) throws SQLException {
        String sql = "INSERT INTO admins (user_id, full_name, email, phone, department) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, department != null ? department : "GENERAL");
            return ps.executeUpdate() > 0;
        }
    }
}
