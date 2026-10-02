package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class RechargeTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long customerId;
    private String mobileNumber;
    private String operator;
    private String circle;
    private String rechargeType; // PREPAID, POSTPAID, DTH
    private BigDecimal amount = BigDecimal.ZERO;
    private String referenceNumber;
    private String status = "SUCCESS";
    private Timestamp createdAt;

    public RechargeTransaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getCircle() { return circle; }
    public void setCircle(String circle) { this.circle = circle; }

    public String getRechargeType() { return rechargeType; }
    public void setRechargeType(String rechargeType) { this.rechargeType = rechargeType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
