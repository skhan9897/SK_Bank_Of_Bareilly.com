package com.bank.skminibank.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KYCUpdateActivity extends AppCompatActivity {

    private EditText etAadhaar, etPan;
    private MaterialButton btnSubmit;
    private CardView cardForm, cardStatus;
    private TextView tvStatus, tvRemarks;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_update);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        etAadhaar = findViewById(R.id.etAadhaar);
        etPan = findViewById(R.id.etPan);
        btnSubmit = findViewById(R.id.btnSubmitKyc);
        cardForm = findViewById(R.id.cardKycForm);
        cardStatus = findViewById(R.id.cardKycStatus);
        tvStatus = findViewById(R.id.tvKycStatusText);
        tvRemarks = findViewById(R.id.tvKycRemarks);

        btnSubmit.setOnClickListener(v -> submitKyc());

        checkStatus();
    }

    private void checkStatus() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getKYCStatus(customerId).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    String status = res.getStatus();
                    
                    // If status is "FAILED" or "No KYC Request Found", we should show the form to let user try again
                    if (status != null && !status.isEmpty() && 
                        !status.equalsIgnoreCase("error") && 
                        !status.equalsIgnoreCase("failed") &&
                        !res.getMessage().toLowerCase().contains("no kyc request found")) {
                        
                        // Standardize status: If request is approved, user is verified
                        if ("APPROVED".equalsIgnoreCase(status) || "VERIFIED".equalsIgnoreCase(status)) {
                            sessionManager.setKycStatus("VERIFIED");
                        } else {
                            sessionManager.setKycStatus(status);
                        }

                        showStatus(status, res.getMessage());
                    } else {
                        showForm();
                    }
                } else {
                    showForm();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                showForm();
            }
        });
    }

    private void showStatus(String status, String remarks) {
        cardForm.setVisibility(View.GONE);
        cardStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(status.toUpperCase());
        if (remarks != null && !remarks.isEmpty()) tvRemarks.setText(remarks);

        if ("VERIFIED".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
            tvStatus.setBackgroundResource(R.drawable.badge_approved);
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            tvStatus.setBackgroundResource(R.drawable.badge_rejected);
            tvStatus.setTextColor(Color.parseColor("#C62828"));
        } else {
            tvStatus.setBackgroundResource(R.drawable.badge_pending);
            tvStatus.setTextColor(Color.parseColor("#E65100"));
        }
    }

    private void showForm() {
        cardForm.setVisibility(View.VISIBLE);
        cardStatus.setVisibility(View.GONE);
        btnSubmit.setEnabled(true);
        btnSubmit.setText("SUBMIT KYC");
    }

    private void submitKyc() {
        String aadhaar = etAadhaar.getText().toString().trim();
        String pan = etPan.getText().toString().trim();

        etAadhaar.setError(null);
        etPan.setError(null);

        if (aadhaar.length() != 12) {
            etAadhaar.setError("Invalid Aadhaar (12 digits required)");
            return;
        }
        if (pan.length() != 10) {
            etPan.setError("Invalid PAN (10 characters required)");
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        // Format for API: aadhaar:123456789012|pan:ABCDE1234F
        String details = "aadhaar:" + aadhaar + "|pan:" + pan;

        ApiClient.getService().submitKYC(
                sessionManager.getCustomerId(),
                sessionManager.getAccountNumber(),
                aadhaar,
                pan,
                "", "", "", "", "" // Images not used for this logic
        ).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("SUBMIT KYC");
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(KYCUpdateActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    checkStatus();
                } else {
                    Toast.makeText(KYCUpdateActivity.this, "Submission Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("SUBMIT KYC");
                Toast.makeText(KYCUpdateActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
