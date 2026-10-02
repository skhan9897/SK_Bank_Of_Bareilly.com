package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class FastagAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long fastagId;
    private Long customerId;
    private String vehicleNumber;
    private String tagId;
    private String issuerBank = "SK BANK OF BAREILLY";
    private BigDecimal balance = BigDecimal.ZERO;
    private String status = "ACTIVE";
    private Timestamp createdAt;

    public FastagAccount() {}

    public Long getFastagId() { return fastagId; }
    public void setFastagId(Long fastagId) { this.fastagId = fastagId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }

    public String getIssuerBank() { return issuerBank; }
    public void setIssuerBank(String issuerBank) { this.issuerBank = issuerBank; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
