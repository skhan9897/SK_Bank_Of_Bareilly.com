package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CardTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cardTransactionId;
    private Long cardId;
    private BigDecimal amount = BigDecimal.ZERO;
    private String merchantName;
    private String transactionReference;
    private String status;
    private Timestamp createdAt;

    public CardTransaction() {}

    public Long getCardTransactionId() { return cardTransactionId; }
    public void setCardTransactionId(Long cardTransactionId) { this.cardTransactionId = cardTransactionId; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
