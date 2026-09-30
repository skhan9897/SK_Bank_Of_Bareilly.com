package com.skbank.util;

import com.skbank.model.CustomerKyc;
import com.skbank.service.KycService;

public class TestKycSubmission {

    public static void main(String[] args) {
        System.out.println("Testing Number-Based KYC Submission (Aadhaar Number + PAN Number)...");
        KycService service = new KycService();

        try {
            CustomerKyc kyc = service.submitKyc("SKC10001", "1234 5678 9012", "ABCDE1234F");
            System.out.println("KYC TEST PASSED! Status: " + kyc.getKycStatus() + " | Aadhaar Masked: " + kyc.getAadhaarMasked() + " | PAN Masked: " + kyc.getPanMasked() + " | Ref: " + kyc.getVerificationReference());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
