package com.bank.skminibank.model;

public class LoginResponse {

    private String status;
    private String message;

    private int customerId;

    private String customerCode;
    private String customerName;
    private String accountNumber;
    private String mobile;
    private String email;
    private String photo;

    private double balance;
    private String kycStatus;

    public LoginResponse() {
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    public String getMessage() {
        return message;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public double getBalance() {
        return balance;
    }
}