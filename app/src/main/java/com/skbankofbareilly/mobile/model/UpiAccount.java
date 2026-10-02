package com.skbankofbareilly.mobile.model;

public class UpiAccount {
    private Long upiAccountId;
    private Long customerId;
    private Long accountId;
    private String upiAddress;
    private String status;
    private String accountNumber;

    public UpiAccount() {}

    public Long getUpiAccountId() { return upiAccountId; }
    public void setUpiAccountId(Long upiAccountId) { this.upiAccountId = upiAccountId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getUpiAddress() { return upiAddress; }
    public void setUpiAddress(String upiAddress) { this.upiAddress = upiAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}
