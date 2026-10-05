package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Kyc implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long kycId;
    private String customerId;
    private String aadhaarNumber;
    private String panNumber;
    private KycStatus verificationStatus = KycStatus.VERIFIED;
    private Timestamp verifiedAt;
    private Timestamp createdAt;

    public Kyc() {}

    public Long getKycId() { return kycId; }
    public void setKycId(Long kycId) { this.kycId = kycId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public KycStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(KycStatus verificationStatus) { this.verificationStatus = verificationStatus; }

    public Timestamp getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Timestamp verifiedAt) { this.verifiedAt = verifiedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getMaskedAadhaar() {
        if (aadhaarNumber != null && aadhaarNumber.length() >= 4) {
            return "XXXX XXXX " + aadhaarNumber.substring(aadhaarNumber.length() - 4);
        }
        return "XXXX XXXX XXXX";
    }

    public String getMaskedPan() {
        if (panNumber != null && panNumber.length() == 10) {
            return panNumber.substring(0, 2) + "*****" + panNumber.substring(7);
        }
        return "*****";
    }
}
