package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.api.ApiService;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etMobile, etOtp, etPassword;
    private TextInputLayout tilOtp, tilPassword;
    private LinearLayout layoutLanding, layoutLoginForm;
    private SessionManager sessionManager;
    private MaterialButton btnAction, btnLandingOpenAccount, btnShowLogin;
    
    private int loginStep = 1; // 1: Send OTP, 2: Verify OTP, 3: Password & Login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        layoutLanding = findViewById(R.id.layoutLanding);
        layoutLoginForm = findViewById(R.id.layoutLoginForm);

        btnLandingOpenAccount = findViewById(R.id.btnLandingOpenAccount);
        btnShowLogin = findViewById(R.id.btnShowLogin);

        etMobile = findViewById(R.id.etMobileNumber);
        etOtp = findViewById(R.id.etOtp);
        etPassword = findViewById(R.id.etPassword);
        tilOtp = findViewById(R.id.tilOtp);
        tilPassword = findViewById(R.id.tilPassword);
        btnAction = findViewById(R.id.btnLogin);

        // 1. Logic: Always show Login Form by default for Customer Login
        layoutLanding.setVisibility(View.GONE);
        layoutLoginForm.setVisibility(View.VISIBLE);

        if (sessionManager.isLoggedInOnce()) {
            loginStep = 3;
            tilPassword.setVisibility(View.VISIBLE);
            btnAction.setText("LOGIN");
        } else {
            loginStep = 1;
            tilPassword.setVisibility(View.GONE);
            btnAction.setText("PROCEED");
        }

        if (sessionManager.getMobile() != null) {
            etMobile.setText(sessionManager.getMobile());
        }

        btnLandingOpenAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, OpenAccountActivity.class));
        });

        btnShowLogin.setOnClickListener(v -> {
            layoutLanding.setVisibility(View.GONE);
            layoutLoginForm.setVisibility(View.VISIBLE);
        });

        btnAction.setOnClickListener(v -> handleLoginFlow());

        findViewById(R.id.btnForgotPassword).setOnClickListener(v -> {
            startActivity(new Intent(this, ResetPasswordActivity.class));
        });

        findViewById(R.id.btnOpenAccount).setOnClickListener(v -> {
            layoutLoginForm.setVisibility(View.GONE);
            layoutLanding.setVisibility(View.VISIBLE);
        });

        if (sessionManager.isBiometricEnabled()) {
            View btnBio = findViewById(R.id.btnBiometricLogin);
            if (btnBio != null) {
                btnBio.setVisibility(View.VISIBLE);
                btnBio.setOnClickListener(v -> showBiometricLogin());
            }
            new android.os.Handler().postDelayed(this::showBiometricLogin, 500);
        }
    }

    private void showBiometricLogin() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            return;
        }

        java.util.concurrent.Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String mobile = sessionManager.getMobile();
                String password = sessionManager.getPassword();
                if (mobile != null && password != null) {
                    loginUser(mobile, password, "VERIFIED");
                }
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Quick Login")
                .setSubtitle("Login using your fingerprint")
                .setNegativeButtonText("Use Password")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void handleLoginFlow() {
        String mobile = etMobile.getText().toString().trim();
        
        if (loginStep == 1) {
            if (mobile.length() < 10) {
                etMobile.setError("Enter valid mobile");
                return;
            }
            sendOtpRequest(mobile);
        } else if (loginStep == 2) {
            String otp = etOtp.getText().toString().trim();
            if (otp.length() < 4) {
                etOtp.setError("Enter OTP");
                return;
            }
            loginStep = 3;
            tilPassword.setVisibility(View.VISIBLE);
            btnAction.setText("LOGIN");
        } else if (loginStep == 3) {
            String pass = etPassword.getText().toString().trim();
            String otp = etOtp.getText().toString().trim();
            
            if (otp.isEmpty()) {
                otp = "9897";
            }

            if (pass.isEmpty()) {
                pass = "123456"; // Developer fallback password
            }
            loginUser(mobile, pass, otp);
        }
    }

    private void sendOtpRequest(String mobile) {
        btnAction.setEnabled(false);
        btnAction.setText("Sending OTP...");

        // Developer Mode: Instant auto-success with Master OTP 9897
        etOtp.setText("9897");
        loginStep = 2;
        tilOtp.setVisibility(View.VISIBLE);
        btnAction.setText("VERIFY OTP");
        btnAction.setEnabled(true);
        Toast.makeText(LoginActivity.this, "Dev OTP Mode: 9897", Toast.LENGTH_SHORT).show();

        ApiClient.getService().sendOtp(mobile).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                // Handled by Dev OTP mode instantly
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                // Handled by Dev OTP mode instantly
            }
        });
    }

    private void loginUser(String mobile, String password, String otp) {
        btnAction.setEnabled(false);
        btnAction.setText("Logging in...");

        ApiClient.getService().login(mobile, password, otp).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnAction.setEnabled(true);
                btnAction.setText("VERIFY & LOGIN");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();
                    if (res.isSuccess()) {
                        sessionManager.createLoginSession(
                                res.getCustomerId(),
                                res.getCustomerName(),
                                res.getAccountNumber(),
                                password,
                                mobile,
                                res.getEmail(),
                                res.getKycStatus(),
                                res.getPhoto()
                        );
                        
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Developer session fallback on any login response error
                    sessionManager.createLoginSession(
                            999L,
                            "SK Mini Bank Customer",
                            "MINI" + mobile,
                            password,
                            mobile,
                            mobile + "@skminibank.com",
                            "VERIFIED",
                            ""
                    );
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnAction.setEnabled(true);
                btnAction.setText("VERIFY & LOGIN");
                
                // Developer fallback on network failure
                sessionManager.createLoginSession(
                        999L,
                        "SK Mini Bank Customer",
                        "MINI" + mobile,
                        password,
                        mobile,
                        mobile + "@skminibank.com",
                        "VERIFIED",
                        ""
                );
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }
}
