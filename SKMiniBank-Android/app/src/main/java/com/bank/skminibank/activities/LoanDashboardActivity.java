package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import android.widget.Button;

public class LoanDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);

        MaterialButton btnApplyLoan = findViewById(R.id.btnApplyLoan);
        MaterialButton btnCheckStatus = findViewById(R.id.btnCheckStatus);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnApplyLoan.setOnClickListener(v -> {
            // Reusing the existing LoanActivity
            startActivity(new Intent(this, LoanActivity.class));
        });

        btnCheckStatus.setOnClickListener(v -> {
            // Reusing the existing MyRequestsActivity and filtering for loans
            Intent intent = new Intent(this, MyRequestsActivity.class);
            intent.putExtra("filter", "loan");
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LoanLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}