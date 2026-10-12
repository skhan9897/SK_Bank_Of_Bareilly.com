package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.utils.SessionManager;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckBalanceActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvAccNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        tvAccNumber = findViewById(R.id.tvAccNumber);

        String acc = sessionManager.getAccountNumber();
        if (acc != null && acc.length() > 4) {
            tvAccNumber.setText("XXXX " + acc.substring(acc.length() - 4));
        } else {
            tvAccNumber.setText(acc);
        }

        findViewById(R.id.cardAccount).setOnClickListener(v -> {
            showBiometricOrPin();
        });

        findViewById(R.id.btnAddAccount).setOnClickListener(v -> {
            startActivity(new Intent(this, AddBankActivity.class));
        });
    }

    private void showBiometricOrPin() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Fallback to PIN
                startPinActivity();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Success - fetch balance directly
                fetchAndShowBalance();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(CheckBalanceActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for SK Mini Bank")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use PIN")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void startPinActivity() {
        Intent intent = new Intent(this, UpiPinActivity.class);
        intent.putExtra("purpose", "balance");
        startActivity(intent);
    }

    private void fetchAndShowBalance() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getDashboardData(customerId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // We need to show the same bottom sheet as UpiPinActivity
                    // For simplicity, let's start UpiPinActivity with a "verified" flag
                    Intent intent = new Intent(CheckBalanceActivity.this, UpiPinActivity.class);
                    intent.putExtra("purpose", "balance");
                    intent.putExtra("verified", true);
                    intent.putExtra("balance", response.body().getBalance());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Toast.makeText(CheckBalanceActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
