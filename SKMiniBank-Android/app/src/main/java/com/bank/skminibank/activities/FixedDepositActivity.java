package com.bank.skminibank.activities;

import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class FixedDepositActivity extends AppCompatActivity {

    private EditText etAmount, etDuration;
    private MaterialButton btnSubmit;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_deposit);

        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing FD Creation...");
        progressDialog.setCancelable(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Fixed Deposit");
        }

        etAmount = findViewById(R.id.etFdAmount);
        etDuration = findViewById(R.id.etFdDuration);
        btnSubmit = findViewById(R.id.btnSubmitFd);

        btnSubmit.setOnClickListener(v -> validateAndSubmit());

        startWaveAnimation();
    }

    private void validateAndSubmit() {
        String amountStr = etAmount.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etAmount.setError("Amount required");
            return;
        }
        
        double amount = Double.parseDouble(amountStr);
        if (amount < 5000) {
            etAmount.setError("Minimum amount is ₹5000");
            return;
        }

        if (durationStr.isEmpty()) {
            etDuration.setError("Tenure required");
            return;
        }

        int duration = Integer.parseInt(durationStr);
        String accountNumber = sessionManager.getAccountNumber();
        int customerId = sessionManager.getCustomerId();

        if (accountNumber == null || accountNumber.isEmpty() || customerId == -1) {
            Toast.makeText(this, "Session Expired. Please Login Again.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        btnSubmit.setEnabled(false);

        ApiClient.getService().createFixedDeposit(customerId, accountNumber, amount, duration).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                progressDialog.dismiss();
                btnSubmit.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    if (res.isSuccess()) {
                        Toast.makeText(FixedDepositActivity.this, "FD Opened Successfully! Balance Deducted.", Toast.LENGTH_LONG).show();
                        finish(); // Go back to dashboard
                    } else {
                        Toast.makeText(FixedDepositActivity.this, "Failed: " + res.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(FixedDepositActivity.this, "Server responded with an error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                btnSubmit.setEnabled(true);
                Toast.makeText(FixedDepositActivity.this, "Connection Error. Check Internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.fdRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
