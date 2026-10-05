package com.skbank.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class DigitalPassbookDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String customerId;
    private String customerNumber;
    private String customerName;
    private Date dateOfBirth;
    private String gender;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private Long accountId;
    private String accountNumber;
    private String accountType;
    private String branchName;
    private String branchCode;
    private String ifscCode;
    private String accountStatus;
    private Timestamp openingDate;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal availableBalance = BigDecimal.ZERO;

    private String kycStatus;
    private String aadhaarNumber;
    private String panNumber;

    public DigitalPassbookDTO() {}

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public Timestamp getOpeningDate() { return openingDate; }
    public void setOpeningDate(Timestamp openingDate) { this.openingDate = openingDate; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getMaskedAccountNumber() {
        if (accountNumber != null && accountNumber.length() >= 4) {
            return "XXXXXX" + accountNumber.substring(accountNumber.length() - 4);
        }
        return accountNumber;
    }

    public String getMaskedMobile() {
        if (mobile != null && mobile.length() == 10) {
            return mobile.substring(0, 2) + "******" + mobile.substring(8);
        }
        return mobile;
    }

    public String getMaskedAadhaar() {
        if (aadhaarNumber != null && aadhaarNumber.length() >= 4) {
            return "XXXX XXXX " + aadhaarNumber.substring(aadhaarNumber.length() - 4);
        }
        return "XXXX XXXX XXXX";
    }

    public String getMaskedPan() {
        if (panNumber != null && panNumber.length() == 10) {
            return panNumber.substring(0, 2) + "*****" + panNumber.substring(7);
        }
        return "*****";
    }
}
