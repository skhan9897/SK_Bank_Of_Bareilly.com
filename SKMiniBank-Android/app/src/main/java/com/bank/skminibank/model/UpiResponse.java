package com.bank.skminibank.model;

public class UpiResponse {
    private String status;
    private String message;
    private String customerId;
    private String accountNumber;
    private String upiId;
    private String upiPin;
    private String upiStatus;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public String getUpiId() { return upiId; }
    public String getUpiPin() { return upiPin; }
    public String getUpiStatus() { return upiStatus; }
}
