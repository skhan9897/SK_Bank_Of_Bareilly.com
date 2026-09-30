package com.skbank.service;

import com.skbank.util.ValidationUtil;

public class DefaultKycVerificationService implements KycVerificationService {

    @Override
    public boolean verifyAadhaar(String aadhaarNumber) {
        return ValidationUtil.isValidAadhaar(aadhaarNumber);
    }

    @Override
    public boolean verifyPan(String panNumber) {
        return ValidationUtil.isValidPan(panNumber);
    }

    @Override
    public boolean verifyCustomerKyc(String aadhaarNumber, String panNumber) {
        return verifyAadhaar(aadhaarNumber) && verifyPan(panNumber);
    }
}
