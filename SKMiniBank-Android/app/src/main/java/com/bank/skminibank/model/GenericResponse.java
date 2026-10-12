package com.bank.skminibank.model;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    @SerializedName("status")
    private String statusStr;
    
    @SerializedName("success")
    private Boolean successBool;
    
    private String message;

    public String getStatus() {
        if (statusStr != null) return statusStr;
        if (successBool != null) return successBool ? "success" : "failed";
        return "error";
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(getStatus());
    }

    public String getMessage() {
        return message;
    }
}
