package com.skbank.model;

import java.sql.Date;
import java.sql.Timestamp;

public class FixedDeposit {
    private int fdId;
    private String customerId;
    private int accountId;
    private String receiptNumber;
    private double depositAmount;
    private double interestRate;
    private int tenureMonths;
    private double maturityAmount;
    private Date maturityDate;
    private String status; // ACTIVE, CLOSED, MATURED
    private Timestamp createdAt;

    // Joined fields
    private String accountNumber;
    private String customerName;

    public FixedDeposit() {}

    public int getFdId() { return fdId; }
    public void setFdId(int fdId) { this.fdId = fdId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(double depositAmount) { this.depositAmount = depositAmount; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public double getMaturityAmount() { return maturityAmount; }
    public void setMaturityAmount(double maturityAmount) { this.maturityAmount = maturityAmount; }

    public Date getMaturityDate() { return maturityDate; }
    public void setMaturityDate(Date maturityDate) { this.maturityDate = maturityDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
