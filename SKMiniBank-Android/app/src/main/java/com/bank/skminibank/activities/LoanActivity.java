package com.bank.skminibank.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.bank.skminibank.model.LoanResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerLoanType;
    private EditText etAmount, etTenure, etIncome, etPurpose;
    private MaterialButton btnApply;
    private CardView cardApplyForm, cardLoanStatus;
    private TextView tvStatusType, tvStatusAmount, tvStatusValue, tvStatusDate, tvStatusRemarks;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Loan Services");
        }

        sessionManager = new SessionManager(this);

        // Form Views
        cardApplyForm = findViewById(R.id.cardApplyForm);
        spinnerLoanType = findViewById(R.id.spinnerLoanType);
        etAmount = findViewById(R.id.etLoanAmount);
        etTenure = findViewById(R.id.etTenure);
        etIncome = findViewById(R.id.etIncome);
        etPurpose = findViewById(R.id.etPurpose);
        btnApply = findViewById(R.id.btnApplyLoan);

        // Status Views
        cardLoanStatus = findViewById(R.id.cardLoanStatus);
        tvStatusType = findViewById(R.id.tvStatusType);
        tvStatusAmount = findViewById(R.id.tvStatusAmount);
        tvStatusValue = findViewById(R.id.tvStatusValue);
        tvStatusDate = findViewById(R.id.tvStatusDate);
        tvStatusRemarks = findViewById(R.id.tvStatusRemarks);

        // Setup Loan Types
        String[] loans = {"Personal Loan", "Home Loan", "Car Loan", "Education Loan", "Business Loan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, loans);
        spinnerLoanType.setAdapter(adapter);

        btnApply.setOnClickListener(v -> submitLoanApplication());

        checkLoanStatus();
    }

    private void checkLoanStatus() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getLoanStatus(customerId).enqueue(new Callback<LoanResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoanResponse> call, @NonNull Response<LoanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoanResponse res = response.body();
                    if (res.isSuccess()) {
                        showStatus(res);
                    } else {
                        showForm();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoanResponse> call, @NonNull Throwable t) {
                showForm();
            }
        });
    }

    private void showStatus(LoanResponse res) {
        cardApplyForm.setVisibility(View.GONE);
        cardLoanStatus.setVisibility(View.VISIBLE);

        tvStatusType.setText(res.getLoanType());
        tvStatusAmount.setText(String.format(Locale.getDefault(), "₹ %.2f", res.getLoanAmount()));

        String status = res.getStatus();
        tvStatusValue.setText(status.toUpperCase());
        tvStatusDate.setText("Applied on: " + res.getRequestDate());
        tvStatusRemarks.setText("Remarks: " + (res.getRemarks() != null ? res.getRemarks() : "Processing..."));

        // Status Styling
        if ("APPROVED".equalsIgnoreCase(status)) {
            tvStatusValue.setBackgroundResource(R.drawable.badge_approved);
            tvStatusValue.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("DISBURSED".equalsIgnoreCase(status)) {
            tvStatusValue.setText("AMOUNT DISBURSED ✅");
            tvStatusValue.setBackgroundResource(R.drawable.badge_approved);
            tvStatusValue.setTextColor(Color.parseColor("#1565C0"));
        } else if ("DOC_VERIFICATION".equalsIgnoreCase(status)) {
            tvStatusValue.setText("DOC VERIFICATION IN PROCESS 📄");
            tvStatusValue.setBackgroundResource(R.drawable.badge_pending);
            tvStatusValue.setTextColor(Color.parseColor("#0288D1"));
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            tvStatusValue.setBackgroundResource(R.drawable.badge_rejected);
            tvStatusValue.setTextColor(Color.parseColor("#C62828"));
        } else {
            tvStatusValue.setBackgroundResource(R.drawable.badge_pending);
            tvStatusValue.setTextColor(Color.parseColor("#E65100"));
        }
    }

    private void showForm() {
        cardApplyForm.setVisibility(View.VISIBLE);
        cardLoanStatus.setVisibility(View.GONE);
    }

    private void submitLoanApplication() {
        String type = spinnerLoanType.getText().toString();
        String amountStr = etAmount.getText().toString().trim();
        String tenureStr = etTenure.getText().toString().trim();
        String incomeStr = etIncome.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();

        if (type.isEmpty() || amountStr.isEmpty() || tenureStr.isEmpty() || incomeStr.isEmpty() || purpose.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int tenure = Integer.parseInt(tenureStr);
        double income = Double.parseDouble(incomeStr);
        int customerId = sessionManager.getCustomerId();
        String accountNumber = sessionManager.getAccountNumber();

        btnApply.setEnabled(false);
        btnApply.setText("Submitting...");

        ApiClient.getService().applyLoan(customerId, accountNumber, type, amount, tenure, income, purpose).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnApply.setEnabled(true);
                btnApply.setText("SUBMIT APPLICATION");

                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        Toast.makeText(LoanActivity.this, "Loan Application Submitted Successfully!", Toast.LENGTH_LONG).show();
                        checkLoanStatus(); // Refresh to show status
                    } else {
                        Toast.makeText(LoanActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnApply.setEnabled(true);
                btnApply.setText("SUBMIT APPLICATION");
                Toast.makeText(LoanActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
