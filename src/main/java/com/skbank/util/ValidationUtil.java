package com.skbank.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^\\d{12}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile.trim()).matches();
    }

    public static String cleanAadhaar(String aadhaar) {
        return aadhaar != null ? aadhaar.replaceAll("\\s+", "").trim() : "";
    }

    public static boolean isValidAadhaar(String aadhaar) {
        String cleaned = cleanAadhaar(aadhaar);
        return AADHAAR_PATTERN.matcher(cleaned).matches();
    }

    public static boolean isValidPan(String pan) {
        if (pan == null) return false;
        String cleanPan = pan.trim().toUpperCase();
        return PAN_PATTERN.matcher(cleanPan).matches();
    }

    public static String maskAadhaar(String aadhaar) {
        String cleaned = cleanAadhaar(aadhaar);
        if (cleaned.length() == 12) {
            return "XXXX XXXX " + cleaned.substring(8);
        }
        return "XXXX XXXX XXXX";
    }

    public static String maskPan(String pan) {
        if (pan != null && pan.trim().length() == 10) {
            String clean = pan.trim().toUpperCase();
            return clean.substring(0, 5) + "****" + clean.substring(9);
        }
        return "ABCDE****F";
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasLetter = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasLetter && hasDigit && hasSpecial;
    }
}
