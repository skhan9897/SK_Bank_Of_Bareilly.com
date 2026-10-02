package com.skbank.dao.impl;

import com.skbank.dao.AuditLogDAO;
import com.skbank.model.AuditLog;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl implements AuditLogDAO {

    @Override
    public List<AuditLog> findAll(int offset, int limit, String moduleFilter) throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT al.*, u.username FROM audit_logs al LEFT JOIN users u ON al.user_id = u.id ");
        if (moduleFilter != null && !moduleFilter.trim().isEmpty()) {
            sql.append("WHERE al.module = ? ");
        }
        sql.append("ORDER BY al.created_at DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (moduleFilter != null && !moduleFilter.trim().isEmpty()) {
                ps.setString(p++, moduleFilter.trim());
            }
            ps.setInt(p++, limit);
            ps.setInt(p, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog al = new AuditLog();
                    al.setAuditId(rs.getLong("audit_id"));
                    long uid = rs.getLong("user_id");
                    if (!rs.wasNull()) al.setUserId(uid);
                    al.setAction(rs.getString("action"));
                    al.setModule(rs.getString("module"));
                    al.setDescription(rs.getString("description"));
                    al.setIpAddress(rs.getString("ip_address"));
                    al.setUserAgent(rs.getString("user_agent"));
                    al.setCreatedAt(rs.getTimestamp("created_at"));
                    try { al.setUsername(rs.getString("username")); } catch (SQLException ignored) {}
                    list.add(al);
                }
            }
        }
        return list;
    }

    @Override
    public long countAll(String moduleFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM audit_logs ");
        if (moduleFilter != null && !moduleFilter.trim().isEmpty()) {
            sql.append("WHERE module = ? ");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (moduleFilter != null && !moduleFilter.trim().isEmpty()) {
                ps.setString(1, moduleFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }
}
