package com.skbank.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SystemSettingsUtil {

    private static final Map<String, String> SETTINGS = new HashMap<>();

    static {
        // Default System Limits
        SETTINGS.put("MAX_TRANSFER_AMOUNT", "500000.00");
        SETTINGS.put("DAILY_TRANSFER_LIMIT", "1000000.00");
        SETTINGS.put("MAX_WITHDRAWAL_AMOUNT", "50000.00");
        SETTINGS.put("DAILY_WITHDRAWAL_LIMIT", "100000.00");
        SETTINGS.put("MAX_UPI_TRANSACTION_AMOUNT", "100000.00");
        SETTINGS.put("OTP_EXPIRY_MINUTES", "5");
        SETTINGS.put("SESSION_TIMEOUT_MINUTES", "30");
    }

    public static String getSetting(String key) {
        return SETTINGS.getOrDefault(key, "");
    }

    public static BigDecimal getSettingAsBigDecimal(String key, BigDecimal defaultValue) {
        String val = SETTINGS.get(key);
        if (val != null) {
            try {
                return new BigDecimal(val.trim());
            } catch (Exception ignored) {}
        }
        return defaultValue;
    }

    public static int getSettingAsInt(String key, int defaultValue) {
        String val = SETTINGS.get(key);
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (Exception ignored) {}
        }
        return defaultValue;
    }

    public static void updateSettings(Map<String, String> newSettings) {
        if (newSettings != null) {
            SETTINGS.putAll(newSettings);
        }
    }
}
