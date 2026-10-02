package com.skbankofbareilly.mobile.model;

public class Kyc {
    private Long kycId;
    private Long customerId;
    private String aadhaarNumber;
    private String panNumber;
    private String verificationStatus = "VERIFIED";

    public Kyc() {}

    public Long getKycId() { return kycId; }
    public void setKycId(Long kycId) { this.kycId = kycId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

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
