package com.bank.skminibank.model;

import com.google.gson.annotations.SerializedName;

public class ServiceRequest {
    @SerializedName(value = "requestId", alternate = {"id", "request_id"})
    private int requestId;

    @SerializedName(value = "customerId", alternate = {"customer_id", "user_id"})
    private int customerId;

    @SerializedName(value = "accountNumber", alternate = {"account_number", "acc_no"})
    private String accountNumber;

    @SerializedName(value = "requestType", alternate = {"request_type", "type"})
    private String requestType;

    @SerializedName(value = "requestDetails", alternate = {"details", "request_details", "description"})
    private String requestDetails;

    @SerializedName("status")
    private String status;

    @SerializedName("remarks")
    private String remarks;

    @SerializedName(value = "approvedBy", alternate = {"approved_by"})
    private String approvedBy;

    @SerializedName(value = "requestDate", alternate = {"request_date", "created_at", "date"})
    private String requestDate;

    @SerializedName(value = "approvalDate", alternate = {"approval_date", "approved_at"})
    private String approvalDate;

    @SerializedName(value = "expectedDeliveryDate", alternate = {"expected_delivery_date", "delivery_date", "exp_date"})
    private String expectedDeliveryDate;

    @SerializedName(value = "dispatchedDate", alternate = {"dispatched_date", "dispatch_date"})
    private String dispatchedDate;

    @SerializedName(value = "deliveredDate", alternate = {"delivered_date", "delivery_completed_at"})
    private String deliveredDate;

    public int getRequestId() { return requestId; }
    public int getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public String getRequestType() { return requestType != null ? requestType : "Service Request"; }
    public String getRequestDetails() { return requestDetails; }
    public String getStatus() { return status != null ? status : "PENDING"; }
    public String getRemarks() { return remarks; }
    public String getApprovedBy() { return approvedBy; }
    public String getRequestDate() { return requestDate != null ? requestDate : "---"; }
    public String getApprovalDate() { return approvalDate; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public String getDispatchedDate() { return dispatchedDate; }
    public String getDeliveredDate() { return deliveredDate; }
}
