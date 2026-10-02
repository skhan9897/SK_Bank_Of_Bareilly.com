package com.skbankofbareilly.mobile.model;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private String errorCode;
    private T data;

    public ApiResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
