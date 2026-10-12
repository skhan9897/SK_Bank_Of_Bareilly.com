package com.bank.skminibank.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.database.AppDatabase;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RazorpayActivity extends AppCompatActivity {

    private EditText etAmount;
    private TextView tvWalletBalance;
    private MaterialButton btnProceed, btn100, btn500, btn1000;
    private SessionManager sessionManager;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razorpay);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        String kyc = sessionManager.getKycStatus();
        if (!"VERIFIED".equalsIgnoreCase(kyc) && !"APPROVED".equalsIgnoreCase(kyc)) {
            Toast.makeText(this, "PLEASE UPDATE AND VERIFY YOUR KYC FIRST", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, KYCUpdateActivity.class));
            finish();
            return;
        }

        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        etAmount = findViewById(R.id.etWalletAmount);
        btnProceed = findViewById(R.id.btnProceedPayment);
        
        btn100 = findViewById(R.id.btn100);
        btn500 = findViewById(R.id.btn500);
        btn1000 = findViewById(R.id.btn1000);

        if (btn100 != null) btn100.setOnClickListener(v -> etAmount.setText("100"));
        if (btn500 != null) btn500.setOnClickListener(v -> etAmount.setText("500"));
        if (btn1000 != null) btn1000.setOnClickListener(v -> etAmount.setText("1000"));
        
        btnProceed.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (!amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount >= 10) {
                        processDeposit(amount);
                    } else {
                        Toast.makeText(this, "Minimum amount is ₹10", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            }
        });

        startWaveAnimation();
        fetchCurrentBalance();
    }

    private void fetchCurrentBalance() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getDashboardData(customerId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double walletBalance = response.body().getWalletBalance();
                    if (tvWalletBalance != null) {
                        tvWalletBalance.setText(String.format(Locale.getDefault(), "₹ %.2f", walletBalance));
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.razorpayRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    private void processDeposit(double amount) {
        btnProceed.setEnabled(false);
        btnProceed.setText("Connecting to Secure Server...");
        
        String accNo = sessionManager.getAccountNumber();
        if (accNo == null) {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ApiClient.getService().performDeposit(accNo, amount).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginRes = response.body();
                    String status = loginRes.getStatus();
                    
                    if ("success".equalsIgnoreCase(status) || "ok".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status)) {
                        btnProceed.setText("Payment Successful!");
                        
                        // Save transaction locally for instant history update
                        String tid = "SKMB" + System.currentTimeMillis();
                        String date = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date());
                        String cleanAcc = accNo.replaceAll("\\s+", "");
                        new Thread(() -> {
                            db.transactionDao().insertTransaction(new com.bank.skminibank.database.TransactionEntity(
                                    cleanAcc, tid, "CREDIT", amount, "Wallet Deposit", date, loginRes.getBalance()
                            ));
                        }).start();

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(RazorpayActivity.this, PaymentSuccessActivity.class);
                            intent.putExtra("amount", String.valueOf(amount));
                            intent.putExtra("name", sessionManager.getCustomerName() != null ? sessionManager.getCustomerName() : "Self Deposit");
                            intent.putExtra("acc", sessionManager.getAccountNumber() != null ? sessionManager.getAccountNumber() : "XXXX");
                            intent.putExtra("balance", String.valueOf(loginRes.getBalance())); // This is already the updated wallet balance from API
                            intent.putExtra("transactionId", "SKMB" + System.currentTimeMillis());
                            intent.putExtra("status", "SUCCESS");
                            startActivity(intent);
                            finish();
                        }, 1000);
                    } else {
                        btnProceed.setEnabled(true);
                        btnProceed.setText("Try Again");
                        String msg = loginRes.getMessage() != null ? loginRes.getMessage() : "Payment Rejected by Server";
                        Toast.makeText(RazorpayActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    btnProceed.setEnabled(true);
                    btnProceed.setText("Try Again");
                    String errorMsg = "Server Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    Toast.makeText(RazorpayActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnProceed.setEnabled(true);
                btnProceed.setText("Try Again");
                Toast.makeText(RazorpayActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
