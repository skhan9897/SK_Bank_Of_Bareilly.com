package com.skbankofbareilly.mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.skbankofbareilly.mobile.databinding.ActivityLoginBinding;
import com.skbankofbareilly.mobile.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.btnOpenAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, OpenAccountActivity.class));
        });
    }

    private void attemptLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        viewModel.login(username, password).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);

            if (response != null && response.isSuccess() && response.getData() != null) {
                Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                intent.putExtra("purpose", "API_CUSTOMER_LOGIN");
                intent.putExtra("devOtp", response.getData().getMessage());
                startActivity(intent);
            } else {
                String errMsg = response != null ? response.getMessage() : "Login failed";
                Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
