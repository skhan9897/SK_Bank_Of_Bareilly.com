package com.skbankofbareilly.mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.skbankofbareilly.mobile.databinding.ActivityOtpVerificationBinding;
import com.skbankofbareilly.mobile.model.AuthResponseDTO;
import com.skbankofbareilly.mobile.security.TokenManager;
import com.skbankofbareilly.mobile.ui.main.MainActivity;
import com.skbankofbareilly.mobile.viewmodel.AuthViewModel;

public class OtpVerificationActivity extends AppCompatActivity {

    private ActivityOtpVerificationBinding binding;
    private AuthViewModel viewModel;
    private String purpose = "API_CUSTOMER_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (getIntent().hasExtra("purpose")) {
            purpose = getIntent().getStringExtra("purpose");
        }

        if (getIntent().hasExtra("devOtp")) {
            binding.tvDevOtp.setVisibility(View.VISIBLE);
            binding.tvDevOtp.setText(getIntent().getStringExtra("devOtp"));
        }

        binding.btnVerifyOtp.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {
        String otp = binding.etOtp.getText().toString().trim();
        if (otp.length() != 6) {
            Toast.makeText(this, "Please enter 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnVerifyOtp.setEnabled(false);

        viewModel.verifyOtp(otp, purpose).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnVerifyOtp.setEnabled(true);

            if (response != null && response.isSuccess() && response.getData() != null) {
                AuthResponseDTO data = response.getData();

                // Save session in EncryptedSharedPreferences
                TokenManager.getInstance(this).saveSession(
                        data.getToken(),
                        data.getUserId(),
                        data.getCustomerId(),
                        data.getCustomerName()
                );

                Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                String msg = response != null ? response.getMessage() : "OTP verification failed";
                Toast.makeText(OtpVerificationActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
