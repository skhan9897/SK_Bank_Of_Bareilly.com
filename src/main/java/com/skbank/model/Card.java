package com.skbank.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cardId;
    private Long customerId;
    private Long accountId;
    private String cardNumber; // stored or shown as XXXX XXXX XXXX 1234
    private CardType cardType;
    private Date expiryDate;
    private CardStatus cardStatus = CardStatus.ACTIVE;
    private Timestamp createdAt;

    // Joined fields
    private String accountNumber;
    private String customerName;

    public Card() {}

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public CardStatus getCardStatus() { return cardStatus; }
    public void setCardStatus(CardStatus cardStatus) { this.cardStatus = cardStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMaskedCardNumber() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            String digits = cardNumber.replaceAll("\\s+", "");
            if (digits.length() >= 4) {
                return "XXXX XXXX XXXX " + digits.substring(digits.length() - 4);
            }
        }
        return "XXXX XXXX XXXX 1234";
    }
}
