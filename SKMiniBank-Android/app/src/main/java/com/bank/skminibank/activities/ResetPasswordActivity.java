package com.bank.skminibank.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.LoginResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etAcc, etMobile, etNewPass;
    private MaterialButton btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etAcc = findViewById(R.id.etResetAcc);
        etMobile = findViewById(R.id.etResetMobile);
        etNewPass = findViewById(R.id.etNewPassword);
        btnReset = findViewById(R.id.btnResetPassword);

        btnReset.setOnClickListener(v -> handleReset());
    }

    private void handleReset() {
        String acc = etAcc.getText().toString().trim();
        String mob = etMobile.getText().toString().trim();
        String pass = etNewPass.getText().toString().trim();

        if (acc.isEmpty() || mob.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnReset.setEnabled(false);
        btnReset.setText("Updating...");

        ApiClient.getService().resetPassword(acc, mob, pass).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnReset.setEnabled(true);
                btnReset.setText("RESET PASSWORD");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();
                    Toast.makeText(ResetPasswordActivity.this, res.getMessage(), Toast.LENGTH_LONG).show();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        finish();
                    }
                } else {
                    String errorMsg = "Server Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    Toast.makeText(ResetPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnReset.setEnabled(true);
                btnReset.setText("RESET PASSWORD");
                Toast.makeText(ResetPasswordActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
