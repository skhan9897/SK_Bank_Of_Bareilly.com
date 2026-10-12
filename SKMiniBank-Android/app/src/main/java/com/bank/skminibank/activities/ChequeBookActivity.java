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
import androidx.cardview.widget.CardView;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.ChequeBookResponse;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChequeBookActivity extends AppCompatActivity {

    private RadioGroup rgChequeType;
    private MaterialButton btnApply;
    private CardView cardForm, cardStatus;
    private TextView tvStatus, tvType, tvReqDate, tvAppDate, tvExpDate, tvRemarks;
    private View rowApp, rowExp;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheque_book);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cheque Book");
        }

        sessionManager = new SessionManager(this);
        rgChequeType = findViewById(R.id.rgChequeType);
        btnApply = findViewById(R.id.btnApplyCheque);
        
        cardForm = findViewById(R.id.cardChequeForm);
        cardStatus = findViewById(R.id.cardChequeStatus);
        
        tvStatus = findViewById(R.id.tvChequeStatus);
        tvType = findViewById(R.id.tvChequeTypeLabel);
        tvReqDate = findViewById(R.id.tvReqDate);
        tvAppDate = findViewById(R.id.tvAppDate);
        tvExpDate = findViewById(R.id.tvExpDate);
        tvRemarks = findViewById(R.id.tvChequeRemarks);
        
        rowApp = findViewById(R.id.rowAppDate);
        rowExp = findViewById(R.id.rowExpDate);

        btnApply.setOnClickListener(v -> submitRequest());
        
        checkStatus();
    }

    private void checkStatus() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getChequeBookStatus(customerId).enqueue(new Callback<ChequeBookResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChequeBookResponse> call, @NonNull Response<ChequeBookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChequeBookResponse res = response.body();
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
            public void onFailure(@NonNull Call<ChequeBookResponse> call, @NonNull Throwable t) {
                showForm();
            }
        });
    }

    private void showStatus(ChequeBookResponse res) {
        cardForm.setVisibility(View.GONE);
        cardStatus.setVisibility(View.VISIBLE);
        
        String status = res.getStatus();
        tvStatus.setText(status.toUpperCase());
        tvType.setText(res.getChequeType() + " Cheque Book");
        tvReqDate.setText(res.getRequestDate());
        
        if (res.getApprovalDate() != null && !res.getApprovalDate().isEmpty()) {
            rowApp.setVisibility(View.VISIBLE);
            tvAppDate.setText(res.getApprovalDate());
        } else {
            rowApp.setVisibility(View.GONE);
        }

        if (res.getExpectedDeliveryDate() != null && !res.getExpectedDeliveryDate().isEmpty()) {
            rowExp.setVisibility(View.VISIBLE);
            tvExpDate.setText(res.getExpectedDeliveryDate());
        } else {
            rowExp.setVisibility(View.GONE);
        }

        if (res.getRemarks() != null && !res.getRemarks().isEmpty()) {
            tvRemarks.setVisibility(View.VISIBLE);
            tvRemarks.setText(res.getRemarks());
        } else {
            tvRemarks.setVisibility(View.GONE);
        }

        // Color coding
        if ("APPROVED".equalsIgnoreCase(status)) {
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            tvStatus.setTextColor(Color.parseColor("#C62828"));
        } else {
            tvStatus.setTextColor(Color.parseColor("#E65100"));
        }
    }

    private void showForm() {
        cardForm.setVisibility(View.VISIBLE);
        cardStatus.setVisibility(View.GONE);
    }

    private void submitRequest() {
        int selectedId = rgChequeType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select cheque book size", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = findViewById(selectedId);
        String chequeType = rb.getText().toString();
        int customerId = sessionManager.getCustomerId();
        String accountNumber = sessionManager.getAccountNumber();

        btnApply.setEnabled(false);
        btnApply.setText("Submitting...");

        ApiClient.getService().applyChequeBook(customerId, accountNumber, chequeType).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnApply.setEnabled(true);
                btnApply.setText("SUBMIT REQUEST");

                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    if (res.isSuccess()) {
                        Toast.makeText(ChequeBookActivity.this, "Request Submitted Successfully", Toast.LENGTH_LONG).show();
                        checkStatus();
                    } else {
                        Toast.makeText(ChequeBookActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnApply.setEnabled(true);
                btnApply.setText("SUBMIT REQUEST");
                Toast.makeText(ChequeBookActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
