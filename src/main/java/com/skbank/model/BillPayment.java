package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class BillPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long billPaymentId;
    private Long customerId;
    private Long accountId;
    private String billerType;
    private String billerName;
    private String consumerNumber;
    private BigDecimal amount = BigDecimal.ZERO;
    private String paymentReference;
    private String status = "SUCCESS";
    private Timestamp createdAt;

    public BillPayment() {}

    public Long getBillPaymentId() { return billPaymentId; }
    public void setBillPaymentId(Long billPaymentId) { this.billPaymentId = billPaymentId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getBillerType() { return billerType; }
    public void setBillerType(String billerType) { this.billerType = billerType; }

    public String getBillerName() { return billerName; }
    public void setBillerName(String billerName) { this.billerName = billerName; }

    public String getConsumerNumber() { return consumerNumber; }
    public void setConsumerNumber(String consumerNumber) { this.consumerNumber = consumerNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
