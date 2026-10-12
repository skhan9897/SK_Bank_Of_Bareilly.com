package com.bank.skminibank.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.AtmStatusResponse;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtmApplyActivity extends AppCompatActivity {

    private RadioGroup rgCardType;
    private MaterialButton btnApply;
    private View cardApplyForm, cardAtmStatus;
    private TextView tvExistingCardType, tvAtmStatus, tvRequestDate;
    private TextView tvApprovalDate, tvExpectedDate, tvDispatchDate, tvDeliverDate, tvRemarks;
    private View rowApproval, rowExpected, rowDispatched, rowDelivered, labelRemarks;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_apply);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("ATM Card");
            }
        }

        sessionManager = new SessionManager(this);
        
        // Form views
        cardApplyForm = findViewById(R.id.cardApplyForm);
        rgCardType = findViewById(R.id.rgCardType);
        btnApply = findViewById(R.id.btnApplyATM);

        // Status views
        cardAtmStatus = findViewById(R.id.cardAtmStatus);
        tvExistingCardType = findViewById(R.id.tvExistingCardType);
        tvAtmStatus = findViewById(R.id.tvAtmStatus);
        tvRequestDate = findViewById(R.id.tvRequestDate);
        
        tvApprovalDate = findViewById(R.id.tvApprovalDate);
        tvExpectedDate = findViewById(R.id.tvExpectedDate);
        tvDispatchDate = findViewById(R.id.tvDispatchDate);
        tvDeliverDate = findViewById(R.id.tvDeliverDate);
        tvRemarks = findViewById(R.id.tvAtmRemarks);
        
        rowApproval = findViewById(R.id.rowApproval);
        rowExpected = findViewById(R.id.rowExpected);
        rowDispatched = findViewById(R.id.rowDispatched);
        rowDelivered = findViewById(R.id.rowDelivered);
        labelRemarks = findViewById(R.id.tvAtmRemarksLabel);

        if (btnApply != null) {
            btnApply.setOnClickListener(v -> submitApplication());
        }

        checkATMStatus();
    }

    private void checkATMStatus() {
        int customerId = sessionManager.getCustomerId();
        if (customerId == -1) {
            showForm();
            return;
        }
        
        ApiClient.getService().getATMStatus(customerId).enqueue(new Callback<AtmStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<AtmStatusResponse> call, @NonNull Response<AtmStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AtmStatusResponse res = response.body();
                    if (res.isSuccess()) {
                        showStatus(res);
                    } else {
                        showForm();
                    }
                } else {
                    showForm();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AtmStatusResponse> call, @NonNull Throwable t) {
                showForm();
            }
        });
    }

    private void showStatus(AtmStatusResponse res) {
        if (res == null) return;
        
        if (cardApplyForm != null) cardApplyForm.setVisibility(View.GONE);
        if (cardAtmStatus != null) cardAtmStatus.setVisibility(View.VISIBLE);
        
        if (tvExistingCardType != null) tvExistingCardType.setText(res.getCardType() != null ? res.getCardType() : "N/A");
        
        String status = res.getStatus();
        if (status == null) status = "PENDING";
        
        if (tvAtmStatus != null) {
            tvAtmStatus.setText(status.toUpperCase());
            
            if ("APPROVED".equalsIgnoreCase(status)) {
                tvAtmStatus.setBackgroundResource(R.drawable.badge_approved);
                tvAtmStatus.setTextColor(Color.parseColor("#2E7D32"));
            } else if ("DISPATCHED".equalsIgnoreCase(status)) {
                tvAtmStatus.setBackgroundResource(R.drawable.badge_approved); 
                tvAtmStatus.setTextColor(Color.parseColor("#1565C0"));
                tvAtmStatus.setText("DISPATCHED");
            } else if ("DELIVERED".equalsIgnoreCase(status)) {
                tvAtmStatus.setBackgroundResource(R.drawable.badge_approved);
                tvAtmStatus.setTextColor(Color.parseColor("#2E7D32"));
                tvAtmStatus.setText("DELIVERED ✅");
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                tvAtmStatus.setBackgroundResource(R.drawable.badge_rejected);
                tvAtmStatus.setTextColor(Color.parseColor("#C62828"));
            } else {
                tvAtmStatus.setBackgroundResource(R.drawable.badge_pending);
                tvAtmStatus.setTextColor(Color.parseColor("#E65100"));
            }
        }
        
        if (tvRequestDate != null) tvRequestDate.setText(res.getRequestDate() != null ? res.getRequestDate() : "---");

        // Detailed Tracking
        updateRow(rowApproval, tvApprovalDate, res.getApprovalDate());
        updateRow(rowExpected, tvExpectedDate, res.getExpectedDeliveryDate());
        updateRow(rowDispatched, tvDispatchDate, res.getDispatchedDate());
        updateRow(rowDelivered, tvDeliverDate, res.getDeliveredDate());

        if (res.getRemarks() != null && !res.getRemarks().isEmpty() && !res.getRemarks().equals("null")) {
            if (labelRemarks != null) labelRemarks.setVisibility(View.VISIBLE);
            if (tvRemarks != null) {
                tvRemarks.setVisibility(View.VISIBLE);
                tvRemarks.setText(res.getRemarks());
            }
        } else {
            if (labelRemarks != null) labelRemarks.setVisibility(View.GONE);
            if (tvRemarks != null) tvRemarks.setVisibility(View.GONE);
        }
    }

    private void updateRow(View row, TextView textView, String value) {
        if (row == null || textView == null) return;
        if (value != null && !value.isEmpty() && !value.equals("null")) {
            row.setVisibility(View.VISIBLE);
            textView.setText(value);
        } else {
            row.setVisibility(View.GONE);
        }
    }

    private void showForm() {
        if (cardApplyForm != null) cardApplyForm.setVisibility(View.VISIBLE);
        if (cardAtmStatus != null) cardAtmStatus.setVisibility(View.GONE);
    }

    private void submitApplication() {
        if (rgCardType == null) return;
        int selectedId = rgCardType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a card type", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = findViewById(selectedId);
        if (rb == null) return;
        String cardType = rb.getText().toString();
        int customerId = sessionManager.getCustomerId();
        String accountNumber = sessionManager.getAccountNumber();

        if (btnApply != null) {
            btnApply.setEnabled(false);
            btnApply.setText("Submitting Application...");
        }

        ApiClient.getService().applyATM(customerId, accountNumber, cardType).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (btnApply != null) {
                    btnApply.setEnabled(true);
                    btnApply.setText("SUBMIT APPLICATION");
                }

                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    if (res.isSuccess()) {
                        Toast.makeText(AtmApplyActivity.this, "Request Submitted Successfully", Toast.LENGTH_LONG).show();
                        checkATMStatus(); // Refresh to show status card
                    } else {
                        Toast.makeText(AtmApplyActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                if (btnApply != null) {
                    btnApply.setEnabled(true);
                    btnApply.setText("SUBMIT APPLICATION");
                }
                Toast.makeText(AtmApplyActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}