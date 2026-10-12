package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.utils.PaymentVoiceUtil;
import com.bank.skminibank.utils.RecentRecipientStore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentProcessingActivity extends AppCompatActivity {

    private String type, fromAcc, toIdentifier, pin, amountStr, remarks, name;
    private double amount, currentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_processing);

        type = getIntent().getStringExtra("type");
        fromAcc = getIntent().getStringExtra("fromAcc");
        toIdentifier = getIntent().getStringExtra("toIdentifier");
        pin = getIntent().getStringExtra("pin");
        amountStr = getIntent().getStringExtra("amountStr");
        remarks = getIntent().getStringExtra("remarks");
        name = getIntent().getStringExtra("name");
        currentBalance = getIntent().getDoubleExtra("balance", 0.0);
        amount = Double.parseDouble(amountStr);

        TextView tvAmount = findViewById(R.id.tvProcessingAmount);
        tvAmount.setText(String.format("₹ %s", amountStr));

        ImageView ivLogo = findViewById(R.id.ivProcessingLogo);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_logo);
        ivLogo.startAnimation(rotate);

        new Handler().postDelayed(this::executePayment, 1000);
    }

    private void executePayment() {
        if ("upi".equalsIgnoreCase(type)) {
            performUpiPayment();
        } else {
            performAccountTransfer();
        }
    }

    private void performUpiPayment() {
        ApiClient.getService().performUpiPayment(fromAcc, toIdentifier, pin, amount, remarks)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        showErrorOnScreen("Network Error: " + t.getMessage());
                    }
                });
    }

    private void performAccountTransfer() {
        ApiClient.getService().transferAmount(fromAcc, toIdentifier, amount, remarks, pin)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        showErrorOnScreen("Network Error: " + t.getMessage());
                    }
                });
    }

    private void handleResponse(Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null && "success".equalsIgnoreCase(response.body().getStatus())) {
            saveRecentRecipient(name, toIdentifier);
            
            LoginResponse loginResponse = response.body();
            
            // Save to Local Database for instant Passbook update
            String tid = "TXN" + System.currentTimeMillis();
            String date = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date());
            com.bank.skminibank.database.AppDatabase db = com.bank.skminibank.database.AppDatabase.getInstance(this);
            String cleanAcc = fromAcc.replaceAll("\\s+", "");
            
            new Thread(() -> {
                db.transactionDao().insertTransaction(new com.bank.skminibank.database.TransactionEntity(
                        cleanAcc, tid, "DEBIT", amount, remarks != null && !remarks.isEmpty() ? remarks : "Paid to " + name, date, loginResponse.getBalance()
                ));
                
                // FORCE SYNC: Trigger dashboard refresh logic
                runOnUiThread(() -> {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
                });
            }).start();

            PaymentVoiceUtil.speakPayment(this, amount, false);
            
            // Navigate to success screen
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(PaymentProcessingActivity.this, PaymentSuccessActivity.class);
                intent.putExtra("amount", amountStr);
                intent.putExtra("name", name);
                intent.putExtra("acc", toIdentifier);
                intent.putExtra("balance", String.valueOf(loginResponse.getBalance()));
                intent.putExtra("transactionId", tid);
                startActivity(intent);
                finish();
            }, 500);
        } else {
            String msg = (response.body() != null) ? response.body().getMessage() : "Payment Failed";

            runOnUiThread(() -> {
                findViewById(R.id.tvStatus).setAlpha(0.5f);
                TextView tvStatus = findViewById(R.id.tvStatus);
                tvStatus.setText(msg);
                tvStatus.setTextColor(android.graphics.Color.RED);

                new Handler().postDelayed(this::finish, 3000);
            });
        }
    }

    private void saveRecentRecipient(String name, String mobile) {
        RecentRecipientStore.save(this, name, mobile);
    }

    private void showErrorOnScreen(String msg) {
        runOnUiThread(() -> {
            TextView tvStatus = findViewById(R.id.tvStatus);
            tvStatus.setText(msg);
            tvStatus.setTextColor(android.graphics.Color.RED);
            new Handler().postDelayed(this::finish, 3000);
        });
    }

    @Override
    public void onBackPressed() {
        // Disable back button during processing
    }
}
