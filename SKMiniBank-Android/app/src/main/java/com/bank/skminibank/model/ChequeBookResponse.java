package com.bank.skminibank.model;

import com.google.gson.annotations.SerializedName;

public class ChequeBookResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("chequeType")
    private String chequeType;
    
    @SerializedName("requestDate")
    private String requestDate;
    
    @SerializedName("approvalDate")
    private String approvalDate;
    
    @SerializedName("expectedDeliveryDate")
    private String expectedDeliveryDate;
    
    @SerializedName("remarks")
    private String remarks;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getChequeType() { return chequeType; }
    public String getRequestDate() { return requestDate; }
    public String getApprovalDate() { return approvalDate; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public String getRemarks() { return remarks; }
}
