package com.skbankofbareilly.mobile.model;

import java.math.BigDecimal;

public class PaymentRequestDTO {
    private String providerId;
    private String consumerNumber;
    private BigDecimal amount;
    private String paymentMethod;

    public PaymentRequestDTO(String providerId, String consumerNumber, BigDecimal amount, String paymentMethod) {
        this.providerId = providerId;
        this.consumerNumber = consumerNumber;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getProviderId() { return providerId; }
    public String getConsumerNumber() { return consumerNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
}
