package com.bank.skminibank.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.api.ApiService;
import com.bank.skminibank.model.LoginResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRequestActivity extends AppCompatActivity {

    private Spinner spinnerRequestType;
    private MaterialButton btnSubmit;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Service Request");
        }

        sessionManager = new SessionManager(this);
        spinnerRequestType = findViewById(R.id.spinnerRequestType);
        btnSubmit = findViewById(R.id.btnSubmitRequest);

        String[] types = {"ATM Card Apply", "Cheque Book Request", "Loan Application", "Net Banking Enable", "Mobile Banking Enable", "UPI ID Creation"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerRequestType.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void submitRequest() {
        String requestType = spinnerRequestType.getSelectedItem().toString();
        int customerId = sessionManager.getCustomerId();

        if (customerId == -1) return;

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        ApiService apiService = ApiClient.getService();
        Call<LoginResponse> call = apiService.submitServiceRequest(customerId, requestType);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("SUBMIT REQUEST");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        Toast.makeText(CreateRequestActivity.this, "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateRequestActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("SUBMIT REQUEST");
                Toast.makeText(CreateRequestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
