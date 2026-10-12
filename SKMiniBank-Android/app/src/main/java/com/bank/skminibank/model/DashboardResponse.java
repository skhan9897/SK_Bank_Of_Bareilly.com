package com.bank.skminibank.model;

public class DashboardResponse {
    private String status;
    private String customerName;
    private String customerCode;
    private String accountNumber;
    private String accountType;
    private String branch;
    private double balance;
    private double walletBalance;
    private String kycStatus;
    private String photo;
    private String upiId;
    private String upiStatus;
    private String accountStatus; // Added field
    private int customerId;

    public String getStatus() { return status; }
    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerCode() { return customerCode; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public String getBranch() { return branch; }
    public double getBalance() { return balance; }
    public double getWalletBalance() { return walletBalance; }
    public String getKycStatus() { return kycStatus; }
    public String getPhoto() { return photo; }
    public String getUpiId() { return upiId; }
    public String getUpiStatus() { return upiStatus; }
    public String getAccountStatus() { return accountStatus; }
}
