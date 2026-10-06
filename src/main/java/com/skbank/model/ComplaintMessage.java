package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ComplaintMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Long complaintId;
    private Long senderUserId;
    private String senderUsername;
    private String message;
    private Timestamp sentAt;

    public ComplaintMessage() {}

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }
}
