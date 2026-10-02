package com.skbank.dao;

import com.skbank.model.Notification;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface NotificationDAO {
    Long create(Notification notification) throws SQLException;
    Long create(Connection conn, Notification notification) throws SQLException;
    List<Notification> findByUserId(Long userId, int limit) throws SQLException;
    int countUnreadByUserId(Long userId) throws SQLException;
    boolean markAsRead(Long notificationId, Long userId) throws SQLException;
    boolean markAllAsRead(Long userId) throws SQLException;
}
