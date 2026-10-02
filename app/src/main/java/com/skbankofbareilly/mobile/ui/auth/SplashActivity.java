package com.skbankofbareilly.mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.skbankofbareilly.mobile.R;
import com.skbankofbareilly.mobile.security.BiometricAuthManager;
import com.skbankofbareilly.mobile.security.TokenManager;
import com.skbankofbareilly.mobile.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthAndNavigate, 1800);
    }

    private void checkAuthAndNavigate() {
        TokenManager tokenManager = TokenManager.getInstance(this);

        if (tokenManager.isLoggedIn()) {
            if (tokenManager.isBiometricEnabled() && BiometricAuthManager.isBiometricAvailable(this)) {
                BiometricAuthManager.authenticate(this, "SK Bank Biometric Login", "Verify identity to access your banking account", new BiometricAuthManager.BiometricCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}
