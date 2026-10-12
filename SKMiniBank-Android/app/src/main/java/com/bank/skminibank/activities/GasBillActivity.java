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

public class GasBillActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerProvider;
    private EditText etConsumerId, etAmount, etPin;
    private MaterialButton btnPay;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_bill);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gas Bill Payment");
        }

        sessionManager = new SessionManager(this);
        spinnerProvider = findViewById(R.id.spinnerGasProvider);
        etConsumerId = findViewById(R.id.etGasConsumerId);
        etAmount = findViewById(R.id.etGasAmount);
        etPin = findViewById(R.id.etPin);
        btnPay = findViewById(R.id.btnPayGasBill);

        // Setup Gas Providers
        String[] providers = {"Indane Gas", "Bharat Gas (BPCL)", "HP Gas (HPCL)", "Adani Gas", "IGL (Indraprastha Gas)", "Mahanagar Gas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, providers);
        spinnerProvider.setAdapter(adapter);

        btnPay.setOnClickListener(v -> submitPayment());
    }

    private void submitPayment() {
        String provider = spinnerProvider.getText().toString();
        String consumerId = etConsumerId.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (provider.isEmpty() || consumerId.isEmpty() || amountStr.isEmpty() || pin.length() != 4) {
            Toast.makeText(this, "Please fill all fields and enter 4-digit PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int customerId = sessionManager.getCustomerId();

        btnPay.setEnabled(false);
        btnPay.setText("Processing...");

        ApiClient.getService().payGasBill(customerId, consumerId, provider, amount, pin).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnPay.setEnabled(true);
                btnPay.setText("PROCEED TO PAY");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(GasBillActivity.this, "Gas Bill Paid Successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(GasBillActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnPay.setEnabled(true);
                btnPay.setText("PROCEED TO PAY");
                Toast.makeText(GasBillActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
