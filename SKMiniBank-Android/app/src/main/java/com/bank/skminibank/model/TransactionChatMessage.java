package com.bank.skminibank.model;

public class TransactionChatMessage {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_PAYMENT = 2;

    private String content;
    private double amount;
    private boolean isSentByMe;
    private int type;
    private String timestamp;
    private String status; // PENDING, SUCCESS, FAILED
    private String transactionId;
    private String otherPartyName;

    public TransactionChatMessage(String content, boolean isSentByMe, int type) {
        this.content = content;
        this.isSentByMe = isSentByMe;
        this.type = type;
        this.timestamp = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date());
    }

    public TransactionChatMessage(double amount, String status, boolean isSentByMe) {
        this.amount = amount;
        this.status = status;
        this.isSentByMe = isSentByMe;
        this.type = TYPE_PAYMENT;
        this.timestamp = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date());
    }
    
    public TransactionChatMessage(double amount, String status, boolean isSentByMe, String txnId, String time) {
        this.amount = amount;
        this.status = status;
        this.isSentByMe = isSentByMe;
        this.transactionId = txnId;
        this.timestamp = time != null ? time : new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date());
        this.type = TYPE_PAYMENT;
    }

    public void setStatus(String status) { this.status = status; }
    public String getContent() { return content; }
    public double getAmount() { return amount; }
    public boolean isSentByMe() { return isSentByMe; }
    public int getType() { return type; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getOtherPartyName() { return otherPartyName; }
    public void setOtherPartyName(String name) { this.otherPartyName = name; }
}
