package com.bank.skminibank.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MyRequestsResponse {
    @SerializedName("status")
    private String statusStr;
    
    @SerializedName("success")
    private Boolean successBool;

    @SerializedName(value = "requests", alternate = {"data", "list", "serviceRequests"})
    private List<ServiceRequest> requests;

    public String getStatus() {
        if (statusStr != null) return statusStr;
        if (successBool != null && successBool) return "success";
        return "failed";
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(getStatus());
    }

    public List<ServiceRequest> getRequests() { return requests; }
}
