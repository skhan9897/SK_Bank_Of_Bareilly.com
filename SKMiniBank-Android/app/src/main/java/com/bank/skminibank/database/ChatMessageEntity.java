package com.bank.skminibank.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessageEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String ownerAccountNumber; // To keep history separate for different logged-in users
    private String transactionId; // Unique ID from server to prevent duplicates
    private String contactMobile;
    private String content;
    private double amount;
    private boolean isSentByMe;
    private int type; // 1 for text, 2 for payment
    private String timestamp;
    private String status;

    public ChatMessageEntity(String ownerAccountNumber, String transactionId, String contactMobile, String content, double amount, boolean isSentByMe, int type, String timestamp, String status) {
        this.ownerAccountNumber = ownerAccountNumber;
        this.transactionId = transactionId;
        this.contactMobile = contactMobile;
        this.content = content;
        this.amount = amount;
        this.isSentByMe = isSentByMe;
        this.type = type;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOwnerAccountNumber() { return ownerAccountNumber; }
    public void setOwnerAccountNumber(String ownerAccountNumber) { this.ownerAccountNumber = ownerAccountNumber; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getContactMobile() { return contactMobile; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public boolean isSentByMe() { return isSentByMe; }
    public void setSentByMe(boolean sentByMe) { isSentByMe = sentByMe; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}