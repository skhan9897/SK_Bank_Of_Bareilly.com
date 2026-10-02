package com.skbankofbareilly.mobile.model;

public class Card {
    private Long cardId;
    private Long customerId;
    private Long accountId;
    private String cardNumber;
    private String cardType;
    private String expiryDate;
    private String cardStatus = "ACTIVE";

    public Card() {}

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCardStatus() { return cardStatus; }
    public void setCardStatus(String cardStatus) { this.cardStatus = cardStatus; }

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
