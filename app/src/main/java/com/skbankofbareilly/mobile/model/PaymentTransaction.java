package com.skbankofbareilly.mobile.model;

import java.math.BigDecimal;

public class PaymentTransaction {
    private Long paymentTransactionId;
    private Long customerId;
    private Long sourceAccountId;
    private String paymentType;
    private String providerCode;
    private String recipientIdentifier;
    private BigDecimal amount = BigDecimal.ZERO;
    private String referenceNumber;
    private String idempotencyKey;
    private String status = "SUCCESS";
    private String remarks;
    private String createdAt;

    public PaymentTransaction() {}

    public Long getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(Long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(Long sourceAccountId) { this.sourceAccountId = sourceAccountId; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getProviderCode() { return providerCode; }
    public void setProviderCode(String providerCode) { this.providerCode = providerCode; }

    public String getRecipientIdentifier() { return recipientIdentifier; }
    public void setRecipientIdentifier(String recipientIdentifier) { this.recipientIdentifier = recipientIdentifier; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
