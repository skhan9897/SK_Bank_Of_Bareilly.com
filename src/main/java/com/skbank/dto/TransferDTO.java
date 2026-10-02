package com.skbank.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long senderAccountId;
    private Long receiverAccountId;
    private BigDecimal amount = BigDecimal.ZERO;
    private String transferType; // ACCOUNT, MOBILE, UPI, SELF
    private String remarks;
    private String referenceNumber;
    private String recipientName;
    private String recipientAccountMasked;
    private String senderAccountMasked;

    public TransferDTO() {}

    public Long getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(Long senderAccountId) { this.senderAccountId = senderAccountId; }

    public Long getReceiverAccountId() { return receiverAccountId; }
    public void setReceiverAccountId(Long receiverAccountId) { this.receiverAccountId = receiverAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientAccountMasked() { return recipientAccountMasked; }
    public void setRecipientAccountMasked(String recipientAccountMasked) { this.recipientAccountMasked = recipientAccountMasked; }

    public String getSenderAccountMasked() { return senderAccountMasked; }
    public void setSenderAccountMasked(String senderAccountMasked) { this.senderAccountMasked = senderAccountMasked; }
}
