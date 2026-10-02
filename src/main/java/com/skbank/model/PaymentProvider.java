package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class PaymentProvider implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long providerId;
    private ProviderType providerType;
    private String providerName;
    private String code;
    private String status = "ACTIVE";
    private Timestamp createdAt;

    public PaymentProvider() {}

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public ProviderType getProviderType() { return providerType; }
    public void setProviderType(ProviderType providerType) { this.providerType = providerType; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
