package com.bank.skminibank.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileBankingActivity extends AppCompatActivity {

    private TextView tvStatus, tvRemarks;
    private MaterialButton btnEnable;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_banking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mobile Banking");
        }

        sessionManager = new SessionManager(this);
        tvStatus = findViewById(R.id.tvMobileStatus);
        tvRemarks = findViewById(R.id.tvMobileRemarks);
        btnEnable = findViewById(R.id.btnEnableMobile);

        btnEnable.setOnClickListener(v -> submitRequest());

        checkStatus();
    }

    private void checkStatus() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getMobileBankingStatus(customerId).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    String status = res.getStatus();
                    
                    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("error")) {
                        tvStatus.setText(status.toUpperCase());
                        
                        String message = res.getMessage();
                        if (message != null && !message.isEmpty()) {
                            tvRemarks.setText(message);
                            tvRemarks.setVisibility(View.VISIBLE);
                        }

                        if ("ACTIVATED".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
                            tvStatus.setBackgroundResource(R.drawable.badge_approved);
                            btnEnable.setVisibility(View.GONE);
                        } else if ("PENDING".equalsIgnoreCase(status) || "DOC_VERIFICATION".equalsIgnoreCase(status)) {
                            tvStatus.setBackgroundResource(R.drawable.badge_pending);
                            btnEnable.setEnabled(false);
                            btnEnable.setText("REQUEST IN PROCESS");
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void submitRequest() {
        int customerId = sessionManager.getCustomerId();
        String accountNumber = sessionManager.getAccountNumber();

        btnEnable.setEnabled(false);
        btnEnable.setText("Submitting...");

        ApiClient.getService().applyMobileBanking(customerId, accountNumber).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnEnable.setEnabled(true);
                btnEnable.setText("ACTIVATE MOBILE BANKING");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(MobileBankingActivity.this, "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
                        checkStatus();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnEnable.setEnabled(true);
                btnEnable.setText("ACTIVATE MOBILE BANKING");
                Toast.makeText(MobileBankingActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
