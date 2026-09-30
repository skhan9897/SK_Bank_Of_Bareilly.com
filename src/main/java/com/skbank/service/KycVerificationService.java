package com.skbank.service;

public interface KycVerificationService {

    boolean verifyAadhaar(String aadhaarNumber);

    boolean verifyPan(String panNumber);

    boolean verifyCustomerKyc(String aadhaarNumber, String panNumber);
}
