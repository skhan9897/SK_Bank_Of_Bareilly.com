package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ComplaintMessageDAO {

    public boolean addMessage(long complaintId, long senderId, String senderRole, String message) throws SQLException {
        String sql = "INSERT INTO complaint_messages (complaint_id, sender_id, sender_role, message) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, complaintId);
            ps.setLong(2, senderId);
            ps.setString(3, senderRole);
            ps.setString(4, message);
            return ps.executeUpdate() > 0;
        }
    }
}
