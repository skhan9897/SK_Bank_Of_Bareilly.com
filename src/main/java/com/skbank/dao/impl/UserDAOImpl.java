package com.skbank.dao.impl;

import com.skbank.dao.UserDAO;
import com.skbank.model.User;
import com.skbank.model.UserRole;
import com.skbank.model.UserStatus;
import com.skbank.util.DatabaseConnection;

import java.sql.*;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Long create(User user) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(conn, user);
        }
    }

    @Override
    public Long create(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, status, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getStatus() != null ? user.getStatus().name() : UserStatus.ACTIVE.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updatePassword(Long userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, updated_at = NOW() WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ?, updated_at = NOW() WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateLastLogin(Long userId) throws SQLException {
        String sql = "UPDATE users SET last_login_at = NOW(), failed_login_attempts = 0 WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateFailedLoginAttempts(Long userId, int attempts) throws SQLException {
        String sql = "UPDATE users SET failed_login_attempts = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attempts);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        user.setAccountLockedUntil(rs.getTimestamp("account_locked_until"));
        user.setLastLogin(rs.getTimestamp("last_login_at"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
