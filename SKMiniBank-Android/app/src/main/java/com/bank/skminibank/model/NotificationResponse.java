package com.bank.skminibank.model;

import java.util.List;

public class NotificationResponse {
    private String status;
    private List<NotificationItem> notifications;

    public String getStatus() { return status; }
    public List<NotificationItem> getNotifications() { return notifications; }

    public static class NotificationItem {
        private int id;
        private String title;
        private String message;
        private String date;
        private int isRead;

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getDate() { return date; }
        public int getIsRead() { return isRead; }
    }
}
