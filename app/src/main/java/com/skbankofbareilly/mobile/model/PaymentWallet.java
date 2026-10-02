package com.skbankofbareilly.mobile.model;

import java.math.BigDecimal;

public class PaymentWallet {
    private Long walletId;
    private Long customerId;
    private String walletNumber;
    private BigDecimal balance = BigDecimal.ZERO;
    private String status = "ACTIVE";

    public PaymentWallet() {}

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getWalletNumber() { return walletNumber; }
    public void setWalletNumber(String walletNumber) { this.walletNumber = walletNumber; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
