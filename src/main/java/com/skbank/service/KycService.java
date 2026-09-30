package com.skbank.service;

import com.skbank.dao.CustomerDAO;
import com.skbank.dao.KycDAO;
import com.skbank.model.CustomerKyc;
import com.skbank.util.PasswordUtil;
import com.skbank.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class KycService {

    private final KycDAO kycDAO = new KycDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final KycVerificationService verificationService = new DefaultKycVerificationService();

    public CustomerKyc getKycStatus(String customerId) throws SQLException {
        CustomerKyc kyc = kycDAO.findByCustomerId(customerId);
        if (kyc == null) {
            // Create default pending record if not present
            kyc = new CustomerKyc();
            kyc.setCustomerId(customerId);
            kyc.setKycStatus("PENDING");
            kyc.setAadhaarMasked("XXXX XXXX XXXX");
            kyc.setPanMasked("XXXXX****X");
            kyc.setVerificationReference("SKKYC" + System.currentTimeMillis());
        }
        return kyc;
    }

    public List<CustomerKyc> getAllPendingKyc() throws SQLException {
        return kycDAO.findAllPendingKyc();
    }

    public List<CustomerKyc> getAllKyc() throws SQLException {
        return kycDAO.findAllKyc();
    }

    public CustomerKyc submitKyc(String customerId, String rawAadhaar, String rawPan) throws SQLException {
        String cleanAadhaar = ValidationUtil.cleanAadhaar(rawAadhaar);
        String cleanPan = rawPan != null ? rawPan.trim().toUpperCase() : "";

        // 1. Validate Formats
        if (!ValidationUtil.isValidAadhaar(cleanAadhaar)) {
            throw new IllegalArgumentException("Aadhaar Number must contain exactly 12 numeric digits.");
        }

        if (!ValidationUtil.isValidPan(cleanPan)) {
            throw new IllegalArgumentException("Invalid PAN Number format. Expected format: ABCDE1234F.");
        }

        // 2. Hash sensitive data for secure storage
        String aadhaarHash = PasswordUtil.hashPassword(cleanAadhaar);
        String panHash = PasswordUtil.hashPassword(cleanPan);

        // 3. Masked versions for display
        String aadhaarMasked = ValidationUtil.maskAadhaar(cleanAadhaar);
        String panMasked = ValidationUtil.maskPan(cleanPan);

        // 4. Duplicate Check
        CustomerKyc existingAadhaar = kycDAO.findByAadhaar(aadhaarHash);
        if (existingAadhaar != null && !existingAadhaar.getCustomerId().equalsIgnoreCase(customerId)) {
            throw new IllegalArgumentException("This Aadhaar Number is already associated with another customer account.");
        }

        CustomerKyc existingPan = kycDAO.findByPan(panHash);
        if (existingPan != null && !existingPan.getCustomerId().equalsIgnoreCase(customerId)) {
            throw new IllegalArgumentException("This PAN Number is already associated with another customer account.");
        }

        // 5. Verification Service Check
        boolean verified = verificationService.verifyCustomerKyc(cleanAadhaar, cleanPan);

        Random random = new Random();
        int randSeq = 10000 + random.nextInt(90000);
        String verRef = "SKKYC2026" + randSeq;

        CustomerKyc kyc = new CustomerKyc();
        kyc.setCustomerId(customerId);
        kyc.setAadhaarNumber(aadhaarHash);
        kyc.setAadhaarMasked(aadhaarMasked);
        kyc.setPanNumber(panHash);
        kyc.setPanMasked(panMasked);
        kyc.setVerificationReference(verRef);

        if (verified) {
            kyc.setKycStatus("VERIFIED");
            kyc.setRejectionReason(null);
        } else {
            kyc.setKycStatus("REJECTED");
            kyc.setRejectionReason("Government verification provider failed to match Aadhaar and PAN records.");
        }

        // 6. Save KYC Record and Update Customer Table
        kycDAO.saveKyc(kyc);
        customerDAO.updateKycStatus(customerId, kyc.getKycStatus());

        return kycDAO.findByCustomerId(customerId);
    }

    public boolean updateKycStatus(String customerId, String status, String rejectionReason) throws SQLException {
        Random random = new Random();
        String verRef = "SKKYC2026" + (10000 + random.nextInt(90000));
        
        boolean updated = kycDAO.updateKycStatus(customerId, status, verRef, rejectionReason);
        if (updated) {
            customerDAO.updateKycStatus(customerId, status);
        }
        return updated;
    }
}
