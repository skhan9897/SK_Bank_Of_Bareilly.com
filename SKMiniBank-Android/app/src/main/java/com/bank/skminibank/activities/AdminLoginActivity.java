package com.bank.skminibank.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bank.skminibank.R;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private AppCompatButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etUsername = findViewById(R.id.etAdminUsername);
        etPassword = findViewById(R.id.etAdminPassword);
        btnLogin = findViewById(R.id.btnAdminLogin);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user.equals("admin") && pass.equals("skadmin123")) {
                Toast.makeText(this, "Admin Portal Authenticated", Toast.LENGTH_LONG).show();
                // Placeholder for Admin Dashboard
                // startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Access Denied: Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
