package com.bank.skminibank.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.api.ApiService;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.model.UpiResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpiActivity extends AppCompatActivity {

    private TextView tvUpiId, tvUpiStatus;
    private MaterialButton btnGenerate, btnSetPin, btnShowQr, btnTogglePin;
    private EditText etUpiPin, etOtp;
    private LinearLayout layoutSetPin;
    private TextInputLayout tilOtp, tilNewPin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("UPI Settings");
        }

        sessionManager = new SessionManager(this);
        tvUpiId = findViewById(R.id.tvUpiId);
        tvUpiStatus = findViewById(R.id.tvUpiStatus);
        btnGenerate = findViewById(R.id.btnGenerateUpi);
        btnSetPin = findViewById(R.id.btnSetPin);
        btnShowQr = findViewById(R.id.btnShowQr);
        btnTogglePin = findViewById(R.id.btnTogglePinSection);

        etUpiPin = findViewById(R.id.etUpiPin);
        etOtp = findViewById(R.id.etOtp);
        tilOtp = findViewById(R.id.tilOtp);
        tilNewPin = findViewById(R.id.tilNewPin);
        layoutSetPin = findViewById(R.id.layoutSetPin);

        btnGenerate.setOnClickListener(v -> generateUpi());
        btnSetPin.setOnClickListener(v -> handleVerifyAndSetPin());
        btnShowQr.setOnClickListener(v -> startActivity(new Intent(this, MyQrActivity.class)));

        btnTogglePin.setOnClickListener(v -> {
            if (layoutSetPin.getVisibility() == View.VISIBLE) {
                layoutSetPin.setVisibility(View.GONE);
            } else {
                layoutSetPin.setVisibility(View.VISIBLE);
                sendOtpForPin(); // Auto send OTP when user wants to change PIN
            }
        });

        startWaveAnimation();
        fetchUpiDetails();
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.upiRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    private void fetchUpiDetails() {
        String accountNumber = sessionManager.getAccountNumber();
        if (accountNumber == null) return;

        tvUpiId.setText("Fetching...");

        ApiClient.getService().getUpiDetails(accountNumber).enqueue(new Callback<UpiResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpiResponse> call, @NonNull Response<UpiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UpiResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        tvUpiId.setText(res.getUpiId());
                        tvUpiStatus.setVisibility(View.VISIBLE);
                        tvUpiStatus.setText(res.getUpiStatus().toUpperCase());
                        btnGenerate.setVisibility(View.GONE);
                        btnShowQr.setVisibility(View.VISIBLE);
                    } else {
                        tvUpiId.setText("Not Generated");
                        tvUpiStatus.setVisibility(View.GONE);
                        btnGenerate.setVisibility(View.VISIBLE);
                        btnShowQr.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpiResponse> call, @NonNull Throwable t) {
                tvUpiId.setText("Network Error");
                Toast.makeText(UpiActivity.this, "Failed to load UPI details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateUpi() {
        int customerId = sessionManager.getCustomerId();
        String accountNumber = sessionManager.getAccountNumber();

        btnGenerate.setEnabled(false);
        btnGenerate.setText("Generating...");

        ApiClient.getService().generateUpi(customerId, accountNumber).enqueue(new Callback<UpiResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpiResponse> call, @NonNull Response<UpiResponse> response) {
                btnGenerate.setEnabled(true);
                btnGenerate.setText("CREATE UPI ID");
                if (response.isSuccessful() && response.body() != null) {
                    UpiResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        Toast.makeText(UpiActivity.this, "UPI ID Created Successfully!", Toast.LENGTH_SHORT).show();
                        fetchUpiDetails();
                        layoutSetPin.setVisibility(View.VISIBLE);
                        sendOtpForPin();
                    } else {
                        Toast.makeText(UpiActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpiResponse> call, @NonNull Throwable t) {
                btnGenerate.setEnabled(true);
                btnGenerate.setText("CREATE UPI ID");
                Toast.makeText(UpiActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOtpForPin() {
        String mobile = sessionManager.getMobile();
        ApiClient.getService().sendOtp(mobile).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(UpiActivity.this, "OTP Sent to registered mobile", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {}
        });
    }

    private void handleVerifyAndSetPin() {
        String otp = etOtp.getText().toString().trim();
        String pin = etUpiPin.getText().toString().trim();
        String accountNumber = sessionManager.getAccountNumber();

        if (otp.length() != 4) {
            etOtp.setError("Enter 4-digit OTP");
            return;
        }

        if (pin.length() != 4) {
            etUpiPin.setError("Enter 4-digit PIN");
            return;
        }

        btnSetPin.setEnabled(false);
        btnSetPin.setText("Processing...");

        ApiClient.getService().setUpiPin(accountNumber, pin, pin, otp).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnSetPin.setEnabled(true);
                btnSetPin.setText("VERIFY AND SET PIN");
                
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(UpiActivity.this, "UPI PIN Set Successfully!", Toast.LENGTH_LONG).show();
                        layoutSetPin.setVisibility(View.GONE);
                        etOtp.setText("");
                        etUpiPin.setText("");
                    } else {
                        Toast.makeText(UpiActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnSetPin.setEnabled(true);
                btnSetPin.setText("VERIFY AND SET PIN");
                Toast.makeText(UpiActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
