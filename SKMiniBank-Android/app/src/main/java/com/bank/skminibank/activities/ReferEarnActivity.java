package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bank.skminibank.R;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class ReferEarnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_earn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SessionManager session = new SessionManager(this);
        TextView tvCode = findViewById(R.id.tvReferCode);
        MaterialButton btnInvite = findViewById(R.id.btnShare);

        // Generate Referral Code from account number
        String acc = session.getAccountNumber();
        String code = "SKMB" + (acc != null && acc.length() >= 4 ? acc.substring(acc.length() - 4) : "0000");
        tvCode.setText(code);

        btnInvite.setOnClickListener(v -> {
            String msg = "Hey! Join SK Mini Bank and earn ₹100 using my referral code: " + code + "\nDownload App: https://skminibank.com";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(Intent.createChooser(intent, "Share using"));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
