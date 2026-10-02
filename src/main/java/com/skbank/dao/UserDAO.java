package com.skbank.dao;

import com.skbank.model.User;
import java.sql.Connection;
import java.sql.SQLException;

public interface UserDAO {
    User findByUsername(String username) throws SQLException;
    User findById(Long id) throws SQLException;
    Long create(User user) throws SQLException;
    Long create(Connection conn, User user) throws SQLException;
    boolean updatePassword(Long userId, String newPasswordHash) throws SQLException;
    boolean updateStatus(Long userId, String status) throws SQLException;
    boolean updateLastLogin(Long userId) throws SQLException;
    boolean updateFailedLoginAttempts(Long userId, int attempts) throws SQLException;
}
