package com.bank.skminibank.model;

public class ProfileResponse {
    private String status;
    private int customerId;
    private String customerCode;
    private String fullName;
    private String fatherName;
    private String motherName;
    private String mobile;
    private String email;
    private String accountNumber;
    private String ifscCode;
    private String branch;
    private String accountType;
    private double balance;
    private String kycStatus;
    private String photo;

    public String getStatus() { return status; }
    public int getCustomerId() { return customerId; }
    public String getCustomerCode() { return customerCode; }
    public String getFullName() { return fullName; }
    public String getFatherName() { return fatherName; }
    public String getMotherName() { return motherName; }
    public String getMobile() { return mobile; }
    public String getEmail() { return email; }
    public String getAccountNumber() { return accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public String getBranch() { return branch; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public String getKycStatus() { return kycStatus; }
    public String getPhoto() { return photo; }
}
