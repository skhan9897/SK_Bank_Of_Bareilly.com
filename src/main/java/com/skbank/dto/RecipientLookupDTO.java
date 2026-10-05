package com.skbank.dto;

import java.io.Serializable;

public class RecipientLookupDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String recipientName;
    private String maskedMobile;
    private String maskedAccount;
    private String upiAddress;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String status;
    private Long accountId;
    private String customerId;
    private boolean ownAccount;

    public RecipientLookupDTO() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getMaskedMobile() { return maskedMobile; }
    public void setMaskedMobile(String maskedMobile) { this.maskedMobile = maskedMobile; }

    public String getMaskedAccount() { return maskedAccount; }
    public void setMaskedAccount(String maskedAccount) { this.maskedAccount = maskedAccount; }

    public String getUpiAddress() { return upiAddress; }
    public void setUpiAddress(String upiAddress) { this.upiAddress = upiAddress; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public boolean isOwnAccount() { return ownAccount; }
    public void setOwnAccount(boolean ownAccount) { this.ownAccount = ownAccount; }
}
