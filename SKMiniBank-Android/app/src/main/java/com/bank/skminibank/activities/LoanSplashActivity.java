package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.bank.skminibank.R;
import com.bank.skminibank.utils.SessionManager;

public class LoanSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Reusing the same splash layout

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);
            if (sessionManager.isLoggedIn()) {
                // If logged in, go to Loan Dashboard
                startActivity(new Intent(this, LoanDashboardActivity.class));
            } else {
                // Otherwise, go to Loan Login
                startActivity(new Intent(this, LoanLoginActivity.class));
            }
            finish();
        }, 1500); // 1.5-second delay
    }
}