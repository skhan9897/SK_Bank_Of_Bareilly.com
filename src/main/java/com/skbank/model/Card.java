package com.skbank.model;

import java.sql.Timestamp;

public class Card {
    private int cardId;
    private String customerId;
    private int accountId;
    private String cardNumber;
    private String cardHolderName;
    private String cardType; // DEBIT, CREDIT
    private String expiryDate; // MM/YYYY
    private String cvvHash;
    private String pinHash;
    private double dailyLimit;
    private double creditLimit;
    private double availableLimit;
    private double outstanding;
    private String status; // ACTIVE, BLOCKED
    private Timestamp createdAt;

    // Joined fields
    private String accountNumber;

    public Card() {}

    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getMaskedCardNumber() {
        if (cardNumber != null && cardNumber.length() >= 16) {
            return "**** **** **** " + cardNumber.substring(12);
        }
        return cardNumber;
    }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvvHash() { return cvvHash; }
    public void setCvvHash(String cvvHash) { this.cvvHash = cvvHash; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public double getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(double dailyLimit) { this.dailyLimit = dailyLimit; }

    public double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }

    public double getAvailableLimit() { return availableLimit; }
    public void setAvailableLimit(double availableLimit) { this.availableLimit = availableLimit; }

    public double getOutstanding() { return outstanding; }
    public void setOutstanding(double outstanding) { this.outstanding = outstanding; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}
