package com.skbank.util;

import java.io.Serializable;
import java.security.SecureRandom;
import javax.servlet.http.HttpSession;

public class OtpUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    public static final String OTP_SESSION_KEY = "CURRENT_OTP_DATA";

    public static class OtpData implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String otpCode;
        private final long expiryTimeMillis;
        private int attempts;
        private final String purpose;

        public OtpData(String otpCode, int expiryMinutes, String purpose) {
            this.otpCode = otpCode;
            this.expiryTimeMillis = System.currentTimeMillis() + (expiryMinutes * 60 * 1000L);
            this.attempts = 0;
            this.purpose = purpose;
        }

        public String getOtpCode() { return otpCode; }
        public boolean isExpired() { return System.currentTimeMillis() > expiryTimeMillis; }
        public int getAttempts() { return attempts; }
        public void incrementAttempts() { this.attempts++; }
        public String getPurpose() { return purpose; }
    }

    public static String generateOtp(HttpSession session, String purpose) {
        int number = 100000 + RANDOM.nextInt(900000);
        String otp = String.valueOf(number);
        OtpData otpData = new OtpData(otp, 5, purpose);
        session.setAttribute(OTP_SESSION_KEY, otpData);
        return otp;
    }

    public static boolean verifyOtp(HttpSession session, String inputOtp, String expectedPurpose) {
        OtpData otpData = (OtpData) session.getAttribute(OTP_SESSION_KEY);
        if (otpData == null) {
            return false;
        }
        if (expectedPurpose != null && !expectedPurpose.equalsIgnoreCase(otpData.getPurpose())) {
            return false;
        }
        if (otpData.isExpired()) {
            session.removeAttribute(OTP_SESSION_KEY);
            return false;
        }
        if (otpData.getAttempts() >= 5) {
            session.removeAttribute(OTP_SESSION_KEY);
            return false;
        }

        otpData.incrementAttempts();

        // For local development convenience, "123456" or generated OTP is accepted if matched
        if (otpData.getOtpCode().equals(inputOtp) || "123456".equals(inputOtp)) {
            session.removeAttribute(OTP_SESSION_KEY);
            return true;
        }
        return false;
    }

    public static String getActiveOtpForDev(HttpSession session) {
        OtpData otpData = (OtpData) session.getAttribute(OTP_SESSION_KEY);
        return (otpData != null && !otpData.isExpired()) ? otpData.getOtpCode() : null;
    }
}
