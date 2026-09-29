package com.skbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SKBankPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition using Android SplashScreen API
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Display splash for 2 seconds to showcase premium logo animation
        new Handler(Looper.getMainLooper()).postDelayed(this::checkSessionAndNavigate, 2000);
    }

    private void checkSessionAndNavigate() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(SplashActivity.this, DashboardActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
