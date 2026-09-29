package com.skbank.model;

import java.sql.Timestamp;

public class Transaction {
    private String transactionId;
    private int accountId;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER, NEFT, RTGS, IMPS, UPI, BILL_PAYMENT, CARD_PAYMENT, LOAN_EMI, FD_INVESTMENT, INTEREST_CREDIT
    private double amount;
    private double balanceAfter;
    private String referenceNumber;
    private String description;
    private String status; // SUCCESS, PENDING, FAILED, REVERSED
    private Timestamp createdAt;

    // Joined fields
    private String accountNumber;
    private String customerName;

    public Transaction() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
