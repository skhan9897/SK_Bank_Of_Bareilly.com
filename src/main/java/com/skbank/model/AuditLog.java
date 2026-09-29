package com.skbank.model;

import java.sql.Timestamp;

public class AuditLog {
    private int logId;
    private Integer userId;
    private String action;
    private String ipAddress;
    private String details;
    private Timestamp createdAt;

    public AuditLog() {}

    public AuditLog(Integer userId, String action, String ipAddress, String details) {
        this.userId = userId;
        this.action = action;
        this.ipAddress = ipAddress;
        this.details = details;
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
