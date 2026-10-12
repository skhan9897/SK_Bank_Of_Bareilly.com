package com.bank.skminibank.activities;

import android.os.Bundle;
import android.util.Log;
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
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.model.UpiResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetPinActivity extends AppCompatActivity {

    private EditText etNewPin, etConfirmPin, etOtp;
    private LinearLayout otpSection;
    private TextView tvOtpSentTo;
    private MaterialButton btnSetPin;
    private SessionManager sessionManager;
    private boolean isOtpSent = false;

    private static final String TAG = "SetPinActivity_Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        etNewPin = findViewById(R.id.etNewPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        etOtp = findViewById(R.id.etOtp);
        otpSection = findViewById(R.id.otpSection);
        tvOtpSentTo = findViewById(R.id.tvOtpSentTo);
        btnSetPin = findViewById(R.id.btnSetPin);

        btnSetPin.setOnClickListener(v -> {
            if (!isOtpSent) {
                handleGetOtp();
            } else {
                handleVerifyAndSetPin();
            }
        });
    }

    private void handleGetOtp() {
        String pin1 = etNewPin.getText().toString();
        String pin2 = etConfirmPin.getText().toString();

        if (pin1.length() != 4) {
            Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pin1.equals(pin2)) {
            Toast.makeText(this, "PINs do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSetPin.setEnabled(false);
        btnSetPin.setText("Sending OTP...");

        String mobile = sessionManager.getMobile();
        if (mobile == null || mobile.isEmpty()) {
            Toast.makeText(this, "Mobile number not found. Please login again.", Toast.LENGTH_LONG).show();
            btnSetPin.setEnabled(true);
            btnSetPin.setText("GET OTP");
            return;
        }
        
        final String finalMobile = mobile;
        ApiClient.getService().sendOtp(mobile).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnSetPin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        isOtpSent = true;
                        otpSection.setVisibility(View.VISIBLE);
                        btnSetPin.setText("VERIFY & SET PIN");
                        
                        String maskedMobile = finalMobile.length() >= 10 ? 
                                finalMobile.substring(0, 2) + "******" + finalMobile.substring(8) : 
                                finalMobile;

                        tvOtpSentTo.setText("OTP sent to " + maskedMobile + "\n(If not received, use 9897)");
                        etNewPin.setEnabled(false);
                        etConfirmPin.setEnabled(false);
                    } else {
                        btnSetPin.setText("GET OTP");
                        Toast.makeText(SetPinActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnSetPin.setText("GET OTP");
                    Toast.makeText(SetPinActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnSetPin.setEnabled(true);
                btnSetPin.setText("GET OTP");
                Toast.makeText(SetPinActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleVerifyAndSetPin() {
        String pin = etNewPin.getText().toString();
        String otp = etOtp.getText().toString();
        String accNo = sessionManager.getAccountNumber();

        if (otp.length() < 4) {
            Toast.makeText(this, "Enter 4-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSetPin.setEnabled(false);
        btnSetPin.setText("Verifying...");

        // Fetch current PIN first for verification if needed by backend
        ApiClient.getService().getUpiDetails(accNo).enqueue(new Callback<UpiResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpiResponse> call, @NonNull Response<UpiResponse> response) {
                String existingPin = pin; 
                if (response.isSuccessful() && response.body() != null) {
                    UpiResponse upiRes = response.body();
                    if ("success".equalsIgnoreCase(upiRes.getStatus()) && upiRes.getUpiPin() != null) {
                        existingPin = upiRes.getUpiPin();
                    }
                }
                
                ApiClient.getService().setUpiPin(accNo, existingPin, pin, otp).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                        btnSetPin.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse res = response.body();
                            if ("success".equalsIgnoreCase(res.getStatus())) {
                                Toast.makeText(SetPinActivity.this, "Transaction PIN Set Successfully", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                btnSetPin.setText("VERIFY & SET PIN");
                                Toast.makeText(SetPinActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            btnSetPin.setText("VERIFY & SET PIN");
                            Toast.makeText(SetPinActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        btnSetPin.setEnabled(true);
                        btnSetPin.setText("VERIFY & SET PIN");
                        Toast.makeText(SetPinActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<UpiResponse> call, @NonNull Throwable t) {
                // Fallback to direct call
                ApiClient.getService().setUpiPin(accNo, pin, pin, otp).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                        btnSetPin.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && "success".equalsIgnoreCase(response.body().getStatus())) {
                            Toast.makeText(SetPinActivity.this, "Transaction PIN Set Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            btnSetPin.setText("VERIFY & SET PIN");
                            Toast.makeText(SetPinActivity.this, "Failed to set PIN", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        btnSetPin.setEnabled(true);
                        btnSetPin.setText("VERIFY & SET PIN");
                        Toast.makeText(SetPinActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
