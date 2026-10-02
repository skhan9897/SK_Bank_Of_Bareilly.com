package com.skbank.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long sourceAccountId;
    private String paymentType;
    private String providerCode;
    private String recipientIdentifier;
    private BigDecimal amount = BigDecimal.ZERO;
    private String remarks;
    private String idempotencyKey;

    // Specific fields for recharge & bills
    private String mobileNumber;
    private String operator;
    private String circle;
    private String consumerNumber;
    private String vehicleNumber;
    private String policyNumber;

    public PaymentRequestDTO() {}

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

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getCircle() { return circle; }
    public void setCircle(String circle) { this.circle = circle; }

    public String getConsumerNumber() { return consumerNumber; }
    public void setConsumerNumber(String consumerNumber) { this.consumerNumber = consumerNumber; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
}
