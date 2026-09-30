package com.skbank.model;

public class RecipientDTO {

    private String customerId;
    private String customerName;
    private String maskedMobile;
    private String accountNumber;
    private String maskedAccountNumber;
    private String upiId;
    private String bankName;
    private String branchName;
    private String ifsc;
    private String status;

    public RecipientDTO() {
        this.bankName = "SK Bank of Bareilly";
        this.status = "ACTIVE";
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMaskedMobile() { return maskedMobile; }
    public void setMaskedMobile(String maskedMobile) { this.maskedMobile = maskedMobile; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getMaskedAccountNumber() { return maskedAccountNumber; }
    public void setMaskedAccountNumber(String maskedAccountNumber) { this.maskedAccountNumber = maskedAccountNumber; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getIfsc() { return ifsc; }
    public void setIfsc(String ifsc) { this.ifsc = ifsc; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
