package com.skbank.model;

import java.sql.Timestamp;

public class BillPayment {
    private int billId;
    private String customerId;
    private int accountId;
    private String billCategory; // Electricity, Water, Gas, Mobile Recharge, DTH, Internet, Insurance, Credit Card Bill
    private String provider;
    private String consumerNumber;
    private double amount;
    private String referenceNumber;
    private String status; // SUCCESS, FAILED
    private Timestamp createdAt;

    // Joined fields
    private String accountNumber;

    public BillPayment() {}

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getBillCategory() { return billCategory; }
    public void setBillCategory(String billCategory) { this.billCategory = billCategory; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getConsumerNumber() { return consumerNumber; }
    public void setConsumerNumber(String consumerNumber) { this.consumerNumber = consumerNumber; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}
