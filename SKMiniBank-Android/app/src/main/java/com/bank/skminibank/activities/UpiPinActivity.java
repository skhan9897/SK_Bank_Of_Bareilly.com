package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpiPinActivity extends AppCompatActivity {

    private EditText etPin;
    private MaterialButton btnSubmit;
    private View btnFingerprint;
    private TextView tvFingerprintLabel;
    private SessionManager sessionManager;
    private String purpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_pin);

        sessionManager = new SessionManager(this);
        etPin = findViewById(R.id.etPin);
        btnSubmit = findViewById(R.id.btnSubmitPin);
        btnFingerprint = findViewById(R.id.btnFingerprintPay);
        tvFingerprintLabel = findViewById(R.id.tvFingerprintLabel);
        purpose = getIntent().getStringExtra("purpose");

        updateBiometricUI();

        btnFingerprint.setOnClickListener(v -> handleBiometricFlow());

        TextView tvBankName = findViewById(R.id.tvBankNamePin);
        if (tvBankName != null) tvBankName.setText("SK Mini Payment Bank");

        btnSubmit.setOnClickListener(v -> {
            String pin = etPin.getText().toString();
            if (pin.length() < 4) {
                Toast.makeText(this, "Enter 4 digit PIN", Toast.LENGTH_SHORT).show();
            } else {
                handlePinSuccess(pin);
            }
        });

        if (sessionManager.isBiometricEnabled() && !"view_balance".equals(purpose)) {
            new Handler().postDelayed(this::showBiometricPrompt, 500);
        }
    }

    private void updateBiometricUI() {
        if (sessionManager.isBiometricEnabled()) {
            tvFingerprintLabel.setText("Pay with Fingerprint");
        } else {
            tvFingerprintLabel.setText("Enable Fingerprint Pay");
        }
    }

    private void handleBiometricFlow() {
        if (sessionManager.isBiometricEnabled()) {
            showBiometricPrompt();
        } else {
            // Explicit Setup Flow
            showBiometricSetupDialog();
        }
    }

    private void showBiometricSetupDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Enable Fingerprint")
                .setMessage("Would you like to use your fingerprint for faster and secure payments? You will need to enter your PIN once to set this up.")
                .setPositiveButton("Setup Now", (dialog, which) -> {
                    Toast.makeText(this, "Enter your 4-digit PIN and click CONFIRM to link fingerprint", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Maybe Later", null)
                .show();
    }

    private void showBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Fingerprint not available or not set up on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String storedPin = sessionManager.getUpiPin();
                if (storedPin != null && !storedPin.isEmpty()) {
                    handlePinSuccess(storedPin);
                } else {
                    Toast.makeText(UpiPinActivity.this, "Security error. Please use PIN to reset Fingerprint.", Toast.LENGTH_SHORT).show();
                    sessionManager.setBiometricEnabled(false);
                    updateBiometricUI();
                }
            }
            
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(UpiPinActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                }
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Use your fingerprint to authenticate")
                .setNegativeButtonText("Use PIN")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void handlePinSuccess(String pin) {
        // If it was the first time enabling, and the user just typed this PIN
        if (!sessionManager.isBiometricEnabled() && etPin.getText().toString().equals(pin)) {
            // We should ask to confirm with fingerprint to finish setup
            setupFingerprintWithPin(pin);
            return;
        }

        processFinalAction(pin);
    }

    private void setupFingerprintWithPin(String pin) {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            // Cannot use biometric, just proceed
            processFinalAction(pin);
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                sessionManager.setUpiPin(pin);
                sessionManager.setBiometricEnabled(true);
                Toast.makeText(UpiPinActivity.this, "Fingerprint Pay Enabled Successfully!", Toast.LENGTH_SHORT).show();
                processFinalAction(pin);
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Setup failed or cancelled, just proceed with PIN this time
                processFinalAction(pin);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Link Fingerprint")
                .setSubtitle("Confirm fingerprint to enable biometric payments")
                .setNegativeButtonText("Skip for now")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void processFinalAction(String pin) {
        if ("view_balance".equals(purpose)) {
            Intent data = new Intent();
            data.putExtra("auth_success", true);
            setResult(RESULT_OK, data);
            finish();
        } else if ("balance".equals(purpose)) {
            fetchAndShowBalance();
        } else {
            Intent data = new Intent();
            data.putExtra("verified", true);
            data.putExtra("pin", pin);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void fetchAndShowBalance() {
        btnSubmit.setEnabled(false);
        int customerId = sessionManager.getCustomerId();
        
        ApiClient.getService().getDashboardData(customerId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                btnSubmit.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    showBalanceBottomSheet(response.body().getBalance());
                } else {
                    Toast.makeText(UpiPinActivity.this, "Failed to fetch balance", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                btnSubmit.setEnabled(true);
                Toast.makeText(UpiPinActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBalanceBottomSheet(double balance) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_balance_result, null);
        
        TextView tvBalance = view.findViewById(R.id.tvBalanceResult);
        TextView tvBankDetail = view.findViewById(R.id.tvBankDetail);
        
        tvBalance.setText(String.format(Locale.getDefault(), "₹ %.2f", balance));
        
        String acc = sessionManager.getAccountNumber();
        if (acc != null && acc.length() > 4) {
            tvBankDetail.setText("SK Mini Payment Bank - " + acc.substring(acc.length() - 4));
        } else {
            tvBankDetail.setText("SK Mini Payment Bank");
        }

        setupBrand(view.findViewById(R.id.brand1), "Ask Me\nAnything", android.R.drawable.ic_menu_help);
        setupBrand(view.findViewById(R.id.brand2), "Create Any\nImage", android.R.drawable.ic_menu_gallery);
        setupBrand(view.findViewById(R.id.brand3), "Plan My\nBudget", android.R.drawable.ic_menu_agenda);
        setupBrand(view.findViewById(R.id.brand4), "Make My\nResume", android.R.drawable.ic_menu_edit);
        
        view.findViewById(R.id.btnDone).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            finish();
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }

    private void setupBrand(View v, String title, int icon) {
        if (v != null) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
            ((ImageView) v.findViewById(R.id.ivIcon)).setImageResource(icon);
        }
    }
}