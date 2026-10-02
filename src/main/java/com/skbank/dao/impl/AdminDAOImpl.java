package com.skbank.dao.impl;

import com.skbank.dao.AdminDAO;
import com.skbank.model.Admin;
import com.skbank.model.AdminRole;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements AdminDAO {

    private static final String SELECT_JOIN_SQL = 
        "SELECT a.*, u.username, e.full_name, e.email " +
        "FROM admins a " +
        "JOIN users u ON a.user_id = u.id " +
        "LEFT JOIN employees e ON a.employee_id = e.employee_id ";

    @Override
    public Admin findByUserId(Long userId) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE a.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAdmin(rs);
            }
        }
        return null;
    }

    @Override
    public Admin findById(Long adminId) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE a.admin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAdmin(rs);
            }
        }
        return null;
    }

    @Override
    public List<Admin> findAll() throws SQLException {
        List<Admin> list = new ArrayList<>();
        String sql = SELECT_JOIN_SQL + "ORDER BY a.admin_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapAdmin(rs));
        }
        return list;
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin adm = new Admin();
        adm.setAdminId(rs.getLong("admin_id"));
        adm.setUserId(rs.getLong("user_id"));
        long empId = rs.getLong("employee_id");
        if (!rs.wasNull()) adm.setEmployeeId(empId);
        adm.setAdminRole(AdminRole.valueOf(rs.getString("admin_role")));
        adm.setStatus(rs.getString("status"));
        adm.setCreatedAt(rs.getTimestamp("created_at"));
        try { adm.setUsername(rs.getString("username")); } catch (SQLException ignored) {}
        try { adm.setFullName(rs.getString("full_name")); } catch (SQLException ignored) {}
        try { adm.setEmail(rs.getString("email")); } catch (SQLException ignored) {}
        return adm;
    }
}
