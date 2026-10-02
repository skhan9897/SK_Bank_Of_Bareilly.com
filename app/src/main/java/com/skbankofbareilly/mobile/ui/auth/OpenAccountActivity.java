package com.skbankofbareilly.mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.skbankofbareilly.mobile.databinding.ActivityOpenAccountBinding;
import com.skbankofbareilly.mobile.viewmodel.AuthViewModel;

import java.util.HashMap;
import java.util.Map;

public class OpenAccountActivity extends AppCompatActivity {

    private ActivityOpenAccountBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnSubmitAccount.setOnClickListener(v -> submitAccountRegistration());
    }

    private void submitAccountRegistration() {
        String fullName = binding.etFullName.getText().toString().trim();
        String dob = binding.etDob.getText().toString().trim();
        String mobile = binding.etMobile.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String aadhaar = binding.etAadhaar.getText().toString().trim();
        String pan = binding.etPan.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (fullName.isEmpty() || mobile.length() != 10 || email.isEmpty() || aadhaar.length() != 12 || pan.length() != 10 || username.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Please fill all required fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("fullName", fullName);
        body.put("dateOfBirth", dob.isEmpty() ? "1995-01-01" : dob);
        body.put("gender", binding.spGender.getSelectedItem().toString().toUpperCase());
        body.put("mobile", mobile);
        body.put("email", email);
        body.put("address", address.isEmpty() ? "Bareilly UP" : address);
        body.put("city", "Bareilly");
        body.put("state", "Uttar Pradesh");
        body.put("pincode", "243001");
        body.put("aadhaarNumber", aadhaar);
        body.put("panNumber", pan.toUpperCase());
        body.put("username", username);
        body.put("password", password);
        body.put("branchId", "1");
        body.put("accountTypeId", "1");

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSubmitAccount.setEnabled(false);

        viewModel.register(body).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSubmitAccount.setEnabled(true);

            if (response != null && response.isSuccess() && response.getData() != null) {
                Toast.makeText(this, "Account created successfully with initial balance ₹0.00!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OpenAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                String msg = response != null ? response.getMessage() : "Account opening failed";
                Toast.makeText(OpenAccountActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
