package com.bank.skminibank.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;

public class RequestDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Request Detail");
        }

        TextView tvType = findViewById(R.id.tvDetailReqType);
        TextView tvStatus = findViewById(R.id.tvDetailReqStatus);
        TextView tvId = findViewById(R.id.tvDetailReqId);
        TextView tvDate = findViewById(R.id.tvDetailReqDate);
        TextView tvAcc = findViewById(R.id.tvDetailReqAcc);
        TextView tvApprDate = findViewById(R.id.tvDetailApprDate);
        TextView tvExpDate = findViewById(R.id.tvDetailExpDate);
        TextView tvRemarks = findViewById(R.id.tvDetailRemarks);

        TableRow rowAppr = findViewById(R.id.rowApprovalDate);
        TableRow rowExp = findViewById(R.id.rowExpectedDate);

        // Get Data
        String type = getIntent().getStringExtra("type");
        String status = getIntent().getStringExtra("status");
        int id = getIntent().getIntExtra("id", 0);
        String date = getIntent().getStringExtra("date");
        String acc = getIntent().getStringExtra("acc");
        String apprDate = getIntent().getStringExtra("apprDate");
        String expDate = getIntent().getStringExtra("expDate");
        String remarks = getIntent().getStringExtra("remarks");

        // Set Data
        tvType.setText(type);
        tvId.setText("#" + id);
        tvDate.setText(date);
        tvAcc.setText(acc);
        tvStatus.setText(status != null ? status.toUpperCase() : "PENDING");

        if (remarks != null && !remarks.isEmpty()) {
            tvRemarks.setText(remarks);
        }

        if (apprDate != null && !apprDate.isEmpty()) {
            rowAppr.setVisibility(View.VISIBLE);
            tvApprDate.setText(apprDate);
        }

        if (expDate != null && !expDate.isEmpty()) {
            rowExp.setVisibility(View.VISIBLE);
            tvExpDate.setText(expDate);
        }

        // Status Styling
        if ("APPROVED".equalsIgnoreCase(status)) {
            tvStatus.setBackgroundResource(R.drawable.badge_approved);
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            tvStatus.setBackgroundResource(R.drawable.badge_rejected);
            tvStatus.setTextColor(Color.parseColor("#C62828"));
        } else {
            tvStatus.setBackgroundResource(R.drawable.badge_pending);
            tvStatus.setTextColor(Color.parseColor("#E65100"));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
