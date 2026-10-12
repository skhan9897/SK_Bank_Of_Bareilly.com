package com.bank.skminibank.model;

import com.google.gson.annotations.SerializedName;

public class AtmStatusResponse {
    
    @SerializedName("success")
    private Boolean success;

    @SerializedName(value = "requestId", alternate = {"id", "request_id"})
    private int requestId;

    @SerializedName(value = "accountNumber", alternate = {"account_number", "acc_no"})
    private String accountNumber;

    @SerializedName(value = "cardType", alternate = {"card_type", "type"})
    private String cardType;

    @SerializedName(value = "status", alternate = {"request_status"})
    private String status;

    @SerializedName(value = "requestDate", alternate = {"request_date", "date"})
    private String requestDate;

    @SerializedName(value = "approvalDate", alternate = {"approval_date"})
    private String approvalDate;

    @SerializedName(value = "expectedDeliveryDate", alternate = {"expected_delivery_date", "delivery_by"})
    private String expectedDeliveryDate;

    @SerializedName(value = "dispatchedDate", alternate = {"dispatched_date", "dispatch_date"})
    private String dispatchedDate;

    @SerializedName(value = "deliveredDate", alternate = {"delivered_date"})
    private String deliveredDate;

    @SerializedName(value = "approvedBy", alternate = {"approved_by"})
    private String approvedBy;

    @SerializedName("remarks")
    private String remarks;

    public boolean isSuccess() {
        if (success != null) return success;
        if (status != null) return !"failed".equalsIgnoreCase(status) && !"error".equalsIgnoreCase(status);
        return requestId != 0;
    }

    public String getMessage() { return status; }
    public int getRequestId() { return requestId; }
    public String getAccountNumber() { return accountNumber; }
    public String getCardType() { return cardType; }
    public String getStatus() { return status != null ? status : "PENDING"; }
    public String getRequestDate() { return requestDate; }
    public String getApprovalDate() { return approvalDate; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public String getDispatchedDate() { return dispatchedDate; }
    public String getDeliveredDate() { return deliveredDate; }
    public String getApprovedBy() { return approvedBy; }
    public String getRemarks() { return remarks; }
}