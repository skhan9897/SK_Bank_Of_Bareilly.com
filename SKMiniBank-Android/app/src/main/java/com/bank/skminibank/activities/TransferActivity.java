package com.bank.skminibank.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.api.ApiService;
import com.bank.skminibank.model.AccountResponse;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferActivity extends AppCompatActivity {

    private EditText etToAccount, etAmount, etDescription, etPin;
    private TextView tvPayeeName, tvFromAccNo, tvFromBalance;
    private MaterialButton btnTransfer;
    private Button btnVerify;
    private SessionManager sessionManager;
    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Fund Transfer");
        }

        sessionManager = new SessionManager(this);
        etToAccount = findViewById(R.id.etToAccount);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etPin = findViewById(R.id.etPin);
        tvPayeeName = findViewById(R.id.tvPayeeName);
        tvFromAccNo = findViewById(R.id.tvFromAccNo);
        tvFromBalance = findViewById(R.id.tvFromBalance);
        btnTransfer = findViewById(R.id.btnTransfer);
        btnVerify = findViewById(R.id.btnVerify);

        // Populate From Account Details
        tvFromAccNo.setText(String.format("A/C: %s", sessionManager.getAccountNumber()));
        
        btnTransfer.setOnClickListener(v -> performTransfer());
        btnVerify.setOnClickListener(v -> verifyAccount());
        
        fetchCurrentBalance();
        startWaveAnimation();
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.transferRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    private void fetchCurrentBalance() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getDashboardData(customerId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBalance = response.body().getBalance();
                    tvFromBalance.setText(String.format(Locale.getDefault(), "Available: ₹ %.2f", currentBalance));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Log.e("TransferActivity", "Failed to fetch balance", t);
            }
        });
    }

    private void verifyAccount() {
        String accountNumber = etToAccount.getText().toString().trim();
        if (accountNumber.isEmpty()) {
            etToAccount.setError("Enter Account Number");
            return;
        }

        btnVerify.setEnabled(false);
        btnVerify.setText("Verifying...");

        ApiClient.getService().getAccountByNumber(accountNumber).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Verify Payee");

                if (response.isSuccessful() && response.body() != null) {
                    AccountResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        tvPayeeName.setVisibility(View.VISIBLE);
                        tvPayeeName.setText(String.format("Account Holder: %s", res.getCustomerName()));
                    } else {
                        tvPayeeName.setVisibility(View.GONE);
                        Toast.makeText(TransferActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                btnVerify.setEnabled(true);
                btnVerify.setText("Verify Payee");
                Toast.makeText(TransferActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performTransfer() {
        String kyc = sessionManager.getKycStatus();
        if (!"VERIFIED".equalsIgnoreCase(kyc) && !"APPROVED".equalsIgnoreCase(kyc)) {
            Toast.makeText(this, "PLEASE UPDATE AND VERIFY YOUR KYC FIRST", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, KYCUpdateActivity.class));
            return;
        }

        String fromAccount = sessionManager.getAccountNumber();
        String toAccount = etToAccount.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (toAccount.isEmpty() || amountStr.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, "Account, Amount and PIN are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pin.length() != 4) {
            Toast.makeText(this, "Enter 4-digit PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Invalid Amount", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PaymentProcessingActivity.class);
        intent.putExtra("type", "transfer");
        intent.putExtra("fromAcc", fromAccount);
        intent.putExtra("toIdentifier", toAccount);
        intent.putExtra("pin", pin);
        intent.putExtra("amountStr", amountStr);
        intent.putExtra("remarks", description);
        intent.putExtra("name", tvPayeeName.getText().toString().replace("Account Holder: ", ""));
        intent.putExtra("balance", currentBalance);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
