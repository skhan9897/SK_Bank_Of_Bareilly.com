package com.bank.skminibank.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.AccountResponse;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpiPaymentActivity extends AppCompatActivity {

    private TextInputEditText etToUpiId, etAmount, etRemarks, etPin;
    private TextInputLayout tilToUpi;
    private TextView tvFromAcc, tvBalance;
    private MaterialButton btnPay;
    private SessionManager sessionManager;
    private double currentBalance = 0;
    private String mode = "upi";
    private boolean isVerified = false;
    private String verifiedReceiverUpi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_payment);

        mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "upi";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mode.equalsIgnoreCase("mobile") ? "Mobile Transfer" : "UPI Payment");
        }

        sessionManager = new SessionManager(this);
        etToUpiId = findViewById(R.id.etToUpiId);
        tilToUpi = (TextInputLayout) etToUpiId.getParent().getParent();
        etAmount = findViewById(R.id.etAmount);
        etRemarks = findViewById(R.id.etRemarks);
        etPin = findViewById(R.id.etUpiPin);
        tvFromAcc = findViewById(R.id.tvFromAccNo);
        tvBalance = findViewById(R.id.tvFromBalance);
        btnPay = findViewById(R.id.btnPay);

        if (mode.equalsIgnoreCase("mobile")) {
            tilToUpi.setHint("Receiver Mobile Number");
            etToUpiId.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
            btnPay.setText("SECURE PAY");
            
            etToUpiId.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 10) {
                        autoVerifyMobile(s.toString());
                    } else {
                        isVerified = false;
                        tilToUpi.setHelperText(null);
                        tilToUpi.setError(null);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        String scanned = getIntent().getStringExtra("scannedUpi");
        if (scanned != null) parseUpiUri(scanned);

        loadAccountDetails();

        btnPay.setOnClickListener(v -> {
            if (mode.equalsIgnoreCase("mobile") && !isVerified) {
                Toast.makeText(this, "Please enter a valid registered mobile number", Toast.LENGTH_SHORT).show();
            } else {
                performPayment();
            }
        });
    }

    private void autoVerifyMobile(String mobile) {
        tilToUpi.setHelperText("Checking...");
        tilToUpi.setError(null);

        ApiClient.getService().searchByMobile(mobile).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        isVerified = true;
                        verifiedReceiverUpi = res.getUpiId();
                        tilToUpi.setHelperText("Verified: " + res.getCustomerName());
                        tilToUpi.setError(null);
                        
                        // AUTO FILL REMARKS
                        etRemarks.setText("Payment to " + res.getCustomerName());
                    } else {
                        isVerified = false;
                        verifiedReceiverUpi = "";
                        tilToUpi.setError(res.getMessage() != null ? res.getMessage() : "Account not found");
                    }
                } else {
                    isVerified = false;
                    tilToUpi.setError("Server Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                isVerified = false;
                tilToUpi.setError("Verification Error");
            }
        });
    }

    private void parseUpiUri(String uri) {
        if (uri == null) return;
        if (uri.startsWith("upi://pay")) {
            Uri parsed = Uri.parse(uri);
            String pa = parsed.getQueryParameter("pa");
            if (pa != null) etToUpiId.setText(pa);
            String am = parsed.getQueryParameter("am");
            if (am != null) etAmount.setText(am);
        } else {
            etToUpiId.setText(uri);
        }
    }

    private void loadAccountDetails() {
        ApiClient.getService().getDashboardData(sessionManager.getCustomerId()).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponse data = response.body();
                    tvFromAcc.setText("A/C: " + data.getAccountNumber());
                    currentBalance = data.getBalance();
                    tvBalance.setText(String.format(Locale.getDefault(), "Available: ₹ %.2f", currentBalance));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {}
        });
    }

    private void performPayment() {
        String kyc = sessionManager.getKycStatus();
        if (!"VERIFIED".equalsIgnoreCase(kyc) && !"APPROVED".equalsIgnoreCase(kyc)) {
            Toast.makeText(this, "PLEASE UPDATE AND VERIFY YOUR KYC FIRST", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, KYCUpdateActivity.class));
            return;
        }

        String toUpi = etToUpiId.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (toUpi.isEmpty() || amountStr.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount > currentBalance) {
            Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromAcc = sessionManager.getAccountNumber();
        if (fromAcc == null || fromAcc.equalsIgnoreCase("null") || fromAcc.isEmpty()) {
            Toast.makeText(this, "Account Number missing. Please refresh dashboard.", Toast.LENGTH_LONG).show();
            return;
        }

        String receiverToPass = toUpi;
        if (mode.equalsIgnoreCase("mobile") && isVerified && verifiedReceiverUpi != null 
                && !verifiedReceiverUpi.isEmpty() && !verifiedReceiverUpi.equalsIgnoreCase("null")) {
            receiverToPass = verifiedReceiverUpi;
        } 
        
        final String finalReceiver = receiverToPass;
        String displayName = tilToUpi.getHelperText() != null ? tilToUpi.getHelperText().toString().replace("Verified: ", "") : toUpi;

        Intent intent = new Intent(this, PaymentProcessingActivity.class);
        intent.putExtra("type", "upi");
        intent.putExtra("fromAcc", fromAcc);
        intent.putExtra("toIdentifier", finalReceiver);
        intent.putExtra("pin", pin);
        intent.putExtra("amountStr", amountStr);
        intent.putExtra("remarks", etRemarks.getText().toString());
        intent.putExtra("name", displayName);
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