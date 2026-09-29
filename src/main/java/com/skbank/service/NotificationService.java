package com.skbank.service;

import com.skbank.dao.NotificationDAO;
import com.skbank.model.Notification;

import java.sql.SQLException;
import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    public List<Notification> getUserNotifications(int userId) throws SQLException {
        return notificationDAO.findByUserId(userId);
    }

    public boolean createNotification(int userId, String title, String message, String type) throws SQLException {
        return notificationDAO.createNotification(userId, title, message, type);
    }

    public boolean markAsRead(int notificationId, int userId) throws SQLException {
        return notificationDAO.markAsRead(notificationId, userId);
    }

    public int getUnreadNotificationCount(int userId) throws SQLException {
        return notificationDAO.getUnreadCount(userId);
    }
}
