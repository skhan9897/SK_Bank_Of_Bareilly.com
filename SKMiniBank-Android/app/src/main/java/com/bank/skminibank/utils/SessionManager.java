package com.bank.skminibank.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SKMiniBankPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_HAS_LOGGED_IN_ONCE = "hasLoggedInOnce";
    private static final String KEY_CUSTOMER_ID = "customerId";
    private static final String KEY_NAME = "customerName";
    private static final String KEY_ACC_NO = "accountNumber";
    private static final String KEY_KYC_STATUS = "kycStatus";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BIOMETRIC_ENABLED = "biometricEnabled";
    private static final String KEY_UPI_PIN = "upiPin";
    private static final String KEY_LAST_BALANCE = "lastBalance";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setBiometricEnabled(boolean enabled) {
        editor.putBoolean(KEY_BIOMETRIC_ENABLED, enabled);
        editor.apply();
    }

    public boolean isBiometricEnabled() {
        return pref.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    public void setUpiPin(String pin) {
        editor.putString(KEY_UPI_PIN, pin);
        editor.apply();
    }

    public String getUpiPin() {
        return pref.getString(KEY_UPI_PIN, null);
    }

    public void setLastBalance(float balance) {
        editor.putFloat(KEY_LAST_BALANCE, balance);
        editor.apply();
    }

    public float getLastBalance() {
        return pref.getFloat(KEY_LAST_BALANCE, -1);
    }

    public void createLoginSession(int customerId, String name, String accNo, String password, String mobile, String email, String kycStatus, String photo) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_HAS_LOGGED_IN_ONCE, true);
        editor.putInt(KEY_CUSTOMER_ID, customerId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ACC_NO, accNo);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_KYC_STATUS, kycStatus);
        editor.putString(KEY_PHOTO, photo);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isLoggedInOnce() {
        return pref.getBoolean(KEY_HAS_LOGGED_IN_ONCE, false);
    }

    public int getCustomerId() {
        return pref.getInt(KEY_CUSTOMER_ID, -1);
    }

    public String getCustomerName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getAccountNumber() {
        return pref.getString(KEY_ACC_NO, null);
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public String getMobile() {
        return pref.getString(KEY_MOBILE, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "support@skbank.com");
    }

    public void setAccountNumber(String accNo) {
        editor.putString(KEY_ACC_NO, accNo);
        editor.apply();
    }

    public void setKycStatus(String status) {
        editor.putString(KEY_KYC_STATUS, status);
        editor.apply();
    }

    public String getKycStatus() {
        return pref.getString(KEY_KYC_STATUS, "PENDING");
    }

    public void setPhoto(String photo) {
        editor.putString(KEY_PHOTO, photo);
        editor.apply();
    }

    public String getPhoto() {
        return pref.getString(KEY_PHOTO, null);
    }

    public void logoutUser() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
}