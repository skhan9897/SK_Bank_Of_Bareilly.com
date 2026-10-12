package com.bank.skminibank.model;

public class AccountResponse {
    private String status;
    private String message;
    private String customerId;
    private String customerName;
    private String accountNumber;
    private String accountType;
    private String balance;
    private String statusValue;
    private String upiId;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public String getBalance() { return balance; }
    public String getStatusValue() { return statusValue; }
    public String getUpiId() { return upiId; }
}
