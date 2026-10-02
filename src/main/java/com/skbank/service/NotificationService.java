package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getUserNotifications(Long userId, int limit) throws BankException;
    int getUnreadCount(Long userId) throws BankException;
    boolean markAsRead(Long notificationId, Long userId) throws BankException;
    boolean markAllAsRead(Long userId) throws BankException;
    void sendNotification(Long userId, String title, String message, String type) throws BankException;
}
