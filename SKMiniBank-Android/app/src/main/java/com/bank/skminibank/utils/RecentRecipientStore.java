package com.bank.skminibank.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RecentRecipientStore {
    private static final String PREF_NAME = "recent_recipients";
    private static final String ORDERED_KEY = "recent_recipients_ordered";
    private static final String LEGACY_KEY = "recent_recipients";
    private static final String SEPARATOR = "\u001F";

    private RecentRecipientStore() {
    }

    public static void save(Context context, String name, String mobile) {
        if (name == null || name.trim().isEmpty() || mobile == null || mobile.trim().isEmpty()) {
            return;
        }

        String normalizedMobile = mobile.replaceAll("[^0-9]", "");
        if (normalizedMobile.length() > 10) {
            normalizedMobile = normalizedMobile.substring(normalizedMobile.length() - 10);
        }
        if (normalizedMobile.isEmpty()) {
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        List<String> ordered = read(prefs);
        String entry = name.trim() + "::" + normalizedMobile;
        ordered.remove(entry);
        ordered.add(0, entry);
        while (ordered.size() > 8) {
            ordered.remove(ordered.size() - 1);
        }
        prefs.edit().putString(ORDERED_KEY, join(ordered)).apply();
    }

    public static List<String> read(SharedPreferences prefs) {
        String stored = prefs.getString(ORDERED_KEY, "");
        List<String> ordered = new ArrayList<>();
        if (stored != null && !stored.isEmpty()) {
            for (String entry : stored.split(SEPARATOR, -1)) {
                if (!entry.isEmpty() && !ordered.contains(entry)) {
                    ordered.add(entry);
                }
            }
            return ordered;
        }

        Set<String> legacy = prefs.getStringSet(LEGACY_KEY, new LinkedHashSet<>());
        if (legacy != null) {
            ordered.addAll(legacy);
        }
        return ordered;
    }

    private static String join(List<String> entries) {
        StringBuilder result = new StringBuilder();
        for (String entry : entries) {
            if (result.length() > 0) {
                result.append(SEPARATOR);
            }
            result.append(entry);
        }
        return result.toString();
    }
}
