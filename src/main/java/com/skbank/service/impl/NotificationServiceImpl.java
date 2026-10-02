package com.skbank.service.impl;

import com.skbank.dao.NotificationDAO;
import com.skbank.dao.impl.NotificationDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.Notification;
import com.skbank.service.NotificationService;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public List<Notification> getUserNotifications(Long userId, int limit) throws BankException {
        try {
            return notificationDAO.findByUserId(userId, limit);
        } catch (Exception e) {
            throw new BankException("Error fetching notifications", e);
        }
    }

    @Override
    public int getUnreadCount(Long userId) throws BankException {
        try {
            return notificationDAO.countUnreadByUserId(userId);
        } catch (Exception e) {
            throw new BankException("Error counting unread notifications", e);
        }
    }

    @Override
    public boolean markAsRead(Long notificationId, Long userId) throws BankException {
        try {
            return notificationDAO.markAsRead(notificationId, userId);
        } catch (Exception e) {
            throw new BankException("Error marking notification as read", e);
        }
    }

    @Override
    public boolean markAllAsRead(Long userId) throws BankException {
        try {
            return notificationDAO.markAllAsRead(userId);
        } catch (Exception e) {
            throw new BankException("Error marking all notifications as read", e);
        }
    }

    @Override
    public void sendNotification(Long userId, String title, String message, String type) throws BankException {
        try {
            Notification n = new Notification();
            n.setUserId(userId);
            n.setTitle(title);
            n.setMessage(message);
            n.setNotificationType(type);
            notificationDAO.create(n);
        } catch (Exception e) {
            throw new BankException("Error sending notification", e);
        }
    }
}
