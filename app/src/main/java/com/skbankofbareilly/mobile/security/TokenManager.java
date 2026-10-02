package com.skbankofbareilly.mobile.security;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "sk_secure_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_CUSTOMER_NAME = "customer_name";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";

    private static TokenManager instance;
    private SharedPreferences prefs;

    private TokenManager(Context context) {
        try {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveSession(String token, Long userId, Long customerId, String customerName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        if (userId != null) editor.putLong(KEY_USER_ID, userId);
        if (customerId != null) editor.putLong(KEY_CUSTOMER_ID, customerId);
        if (customerName != null) editor.putString(KEY_CUSTOMER_NAME, customerName);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public Long getUserId() {
        long id = prefs.getLong(KEY_USER_ID, -1);
        return id != -1 ? id : null;
    }

    public Long getCustomerId() {
        long id = prefs.getLong(KEY_CUSTOMER_ID, -1);
        return id != -1 ? id : null;
    }

    public String getCustomerName() {
        return prefs.getString(KEY_CUSTOMER_NAME, "Customer");
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().trim().isEmpty();
    }

    public void setBiometricEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }

    public boolean isBiometricEnabled() {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
