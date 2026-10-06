package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class UpiAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long upiId;
    private Long customerId;
    private Long accountId;
    private String upiAddress;
    private String pinHash;
    private String status = "ACTIVE";
    private Timestamp createdAt;

    public UpiAccount() {}

    public Long getUpiId() { return upiId; }
    public void setUpiId(Long upiId) { this.upiId = upiId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getUpiAddress() { return upiAddress; }
    public void setUpiAddress(String upiAddress) { this.upiAddress = upiAddress; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
