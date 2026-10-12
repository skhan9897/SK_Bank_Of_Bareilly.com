package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.utils.SessionManager;

public class ManageCardActivity extends AppCompatActivity {

    private SwitchCompat switchBlock, switchOnline, switchNfc;
    private TextView tvCardNumber, tvCardName;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_card);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        
        switchBlock = findViewById(R.id.switchBlock);
        switchOnline = findViewById(R.id.switchOnline);
        switchNfc = findViewById(R.id.switchNfc);
        tvCardNumber = findViewById(R.id.tvDisplayCardNumber);
        tvCardName = findViewById(R.id.tvDisplayCardName);

        setupCardDetails();
        setupListeners();
    }

    private void setupCardDetails() {
        String acc = sessionManager.getAccountNumber();
        if (acc != null && acc.length() >= 4) {
            tvCardNumber.setText("5218 4400 9800 " + acc.substring(acc.length() - 4));
        }
        tvCardName.setText(sessionManager.getCustomerName().toUpperCase());
    }

    private void setupListeners() {
        switchBlock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Card Blocked Temporarily" : "Card Unblocked";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        switchOnline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Online Transactions Enabled" : "Online Transactions Disabled";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnSetPin).setOnClickListener(v -> {
            startActivity(new Intent(this, SetPinActivity.class));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
