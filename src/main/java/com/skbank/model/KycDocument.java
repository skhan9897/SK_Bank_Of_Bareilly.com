package com.skbank.model;

import java.sql.Timestamp;

public class KycDocument {
    private int kycId;
    private String customerId;
    private String documentType; // Aadhaar, PAN, Address Proof, Photo
    private String filePath;
    private String status; // PENDING, VERIFIED, REJECTED
    private String rejectionReason;
    private Timestamp uploadedAt;

    // Joined fields
    private String customerName;

    public KycDocument() {}

    public int getKycId() { return kycId; }
    public void setKycId(int kycId) { this.kycId = kycId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Timestamp getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Timestamp uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
