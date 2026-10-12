package com.bank.skminibank.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class ElectricityBillActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerBoard;
    private EditText etConsumerNumber, etAmount, etPin;
    private MaterialButton btnPay;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_bill);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Electricity Bill");
        }

        sessionManager = new SessionManager(this);
        spinnerBoard = findViewById(R.id.spinnerBoard);
        etConsumerNumber = findViewById(R.id.etConsumerNumber);
        etAmount = findViewById(R.id.etAmount);
        etPin = findViewById(R.id.etPin);
        btnPay = findViewById(R.id.btnPayBill);

        // Setup Electricity Boards
        String[] boards = {"UPPCL (Uttar Pradesh)", "BSES Yamuna", "BSES Rajdhani", "Tata Power", "Adani Electricity", "PSPCL (Punjab)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, boards);
        spinnerBoard.setAdapter(adapter);

        btnPay.setOnClickListener(v -> submitPayment());
    }

    private void submitPayment() {
        String board = spinnerBoard.getText().toString();
        String consumerNo = etConsumerNumber.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (board.isEmpty() || consumerNo.isEmpty() || amountStr.isEmpty() || pin.length() != 4) {
            Toast.makeText(this, "Please fill all fields and enter 4-digit PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int customerId = sessionManager.getCustomerId();

        btnPay.setEnabled(false);
        btnPay.setText("Processing Payment...");

        ApiClient.getService().payElectricityBill(customerId, consumerNo, board, amount, pin).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnPay.setEnabled(true);
                btnPay.setText("PROCEED TO PAY");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(ElectricityBillActivity.this, "Bill Payment Successful!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ElectricityBillActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnPay.setEnabled(true);
                btnPay.setText("PROCEED TO PAY");
                Toast.makeText(ElectricityBillActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
