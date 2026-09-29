package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class AuditLogDAO {

    public void log(AuditLog auditLog) {
        String sql = "INSERT INTO audit_logs (user_id, action, ip_address, details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (auditLog.getUserId() != null) {
                ps.setInt(1, auditLog.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, auditLog.getAction());
            ps.setString(3, auditLog.getIpAddress());
            ps.setString(4, auditLog.getDetails());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
