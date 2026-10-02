package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ComplaintMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Long complaintId;
    private Long senderUserId;
    private String message;
    private Timestamp createdAt;

    // Joined field
    private String senderName;
    private UserRole senderRole;

    public ComplaintMessage() {}

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public UserRole getSenderRole() { return senderRole; }
    public void setSenderRole(UserRole senderRole) { this.senderRole = senderRole; }
}
