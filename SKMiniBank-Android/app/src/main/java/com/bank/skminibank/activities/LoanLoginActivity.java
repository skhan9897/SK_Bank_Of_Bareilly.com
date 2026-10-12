package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanLoginActivity extends AppCompatActivity {

    private EditText etMobile, etPassword;
    private MaterialButton btnLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_login);

        sessionManager = new SessionManager(this);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String mobile = etMobile.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (mobile.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Mobile and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        // Reusing the same login API
        ApiClient.getService().login(mobile, password, "").enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
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
                        // Go to the Loan Dashboard
                        startActivity(new Intent(LoanLoginActivity.this, LoanDashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoanLoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoanLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                Toast.makeText(LoanLoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}