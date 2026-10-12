package com.bank.skminibank.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditCardActivity extends AppCompatActivity {

    private EditText etCcNumber, etCcAmount, etPin;
    private MaterialButton btnPay;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Credit Card Payment");
        }

        sessionManager = new SessionManager(this);
        etCcNumber = findViewById(R.id.etCcNumber);
        etCcAmount = findViewById(R.id.etCcAmount);
        etPin = findViewById(R.id.etPin);
        btnPay = findViewById(R.id.btnPayCc);

        btnPay.setOnClickListener(v -> submitCcPayment());
    }

    private void submitCcPayment() {
        String ccNumber = etCcNumber.getText().toString().trim();
        String amountStr = etCcAmount.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (ccNumber.length() < 16 || amountStr.isEmpty() || pin.length() != 4) {
            Toast.makeText(this, "Valid card, amount and 4-digit PIN required", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int customerId = sessionManager.getCustomerId();

        btnPay.setEnabled(false);
        btnPay.setText("Processing Payment...");

        ApiClient.getService().payCreditCardBill(customerId, ccNumber, amount, pin).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnPay.setEnabled(true);
                btnPay.setText("PAY BILL NOW");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(CreditCardActivity.this, "Credit Card Bill Paid Successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(CreditCardActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnPay.setEnabled(true);
                btnPay.setText("PAY BILL NOW");
                Toast.makeText(CreditCardActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
