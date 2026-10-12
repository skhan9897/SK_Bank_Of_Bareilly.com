package com.bank.skminibank.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.bank.skminibank.R;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchBioLogin, switchBioTxn;
    private View cardVerify;
    private TextView tvVerifyTitle;
    private TextInputLayout tilVerify;
    private EditText etVerifyValue;
    private SessionManager sessionManager;
    private boolean isEnablingLogin = false;
    private boolean isEnablingTxn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switchBioLogin = findViewById(R.id.switchBioLogin);
        switchBioTxn = findViewById(R.id.switchBioTxn);
        cardVerify = findViewById(R.id.cardVerify);
        tvVerifyTitle = findViewById(R.id.tvVerifyTitle);
        tilVerify = findViewById(R.id.tilVerify);
        etVerifyValue = findViewById(R.id.etVerifyValue);

        switchBioLogin.setChecked(sessionManager.isBiometricEnabled());

        switchBioLogin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !sessionManager.isBiometricEnabled()) {
                showVerification(true, false);
            } else if (!isChecked) {
                sessionManager.setBiometricEnabled(false);
                Toast.makeText(this, "Biometric Login Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        switchBioTxn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showVerification(false, true);
            } else {
                Toast.makeText(this, "Biometric Transaction Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnVerify).setOnClickListener(v -> handleVerification());
    }

    private void showVerification(boolean login, boolean txn) {
        isEnablingLogin = login;
        isEnablingTxn = txn;
        cardVerify.setVisibility(View.VISIBLE);
        etVerifyValue.setText("");
        if (login) {
            tvVerifyTitle.setText("Verify Login Password");
            tilVerify.setHint("Enter Login Password");
            etVerifyValue.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            tvVerifyTitle.setText("Verify Transaction PIN");
            tilVerify.setHint("Enter 4-Digit UPI PIN");
            etVerifyValue.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }
    }

    private void handleVerification() {
        String input = etVerifyValue.getText().toString().trim();
        if (isEnablingLogin) {
            if (input.equals(sessionManager.getPassword())) {
                authenticateBiometric(() -> {
                    sessionManager.setBiometricEnabled(true);
                    cardVerify.setVisibility(View.GONE);
                    Toast.makeText(this, "Biometric Login Enabled", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            }
        } else if (isEnablingTxn) {
            if (input.equals(sessionManager.getUpiPin())) {
                authenticateBiometric(() -> {
                    cardVerify.setVisibility(View.GONE);
                    Toast.makeText(this, "Biometric Transaction Enabled", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void authenticateBiometric(Runnable onSuccess) {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Biometric not available on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                runOnUiThread(onSuccess);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm Fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}