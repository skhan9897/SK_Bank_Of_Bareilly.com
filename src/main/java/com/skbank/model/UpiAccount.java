package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class UpiAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long upiAccountId;
    private String customerId;
    private Long accountId;
    private String upiAddress;
    private String upiPinHash;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined fields
    private String accountNumber;

    public UpiAccount() {}

    public Long getUpiAccountId() { return upiAccountId; }
    public void setUpiAccountId(Long upiAccountId) { this.upiAccountId = upiAccountId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getUpiAddress() { return upiAddress; }
    public void setUpiAddress(String upiAddress) { this.upiAddress = upiAddress; }

    public String getUpiPinHash() { return upiPinHash; }
    public void setUpiPinHash(String upiPinHash) { this.upiPinHash = upiPinHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}
