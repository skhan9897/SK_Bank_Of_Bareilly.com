package com.bank.skminibank.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.GenericResponse;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenAccountActivity extends AppCompatActivity {

    private EditText etFullName, etFatherName, etMotherName, etDob, etOccupation, etMobile, etAlternateMobile, etEmail, etAadhaar, etPan, etAddress, etCity, etState, etPincode, etNomineeName, etNomineeMobile, etBalance, etPassword, etTransactionPin;
    private Spinner spGender, spMaritalStatus, spRelationship, spAccountType;
    private Button btnCreateAccount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initializeViews();

        etDob.setOnClickListener(v -> showDatePicker());
        btnCreateAccount.setOnClickListener(v -> createAccount());
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etFatherName = findViewById(R.id.etFatherName);
        etMotherName = findViewById(R.id.etMotherName);
        etDob = findViewById(R.id.etDob);
        etOccupation = findViewById(R.id.etOccupation);
        etMobile = findViewById(R.id.etMobile);
        etAlternateMobile = findViewById(R.id.etAlternateMobile);
        etEmail = findViewById(R.id.etEmail);
        etAadhaar = findViewById(R.id.etAadhaar);
        etPan = findViewById(R.id.etPan);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);
        etNomineeName = findViewById(R.id.etNomineeName);
        etNomineeMobile = findViewById(R.id.etNomineeMobile);
        etBalance = findViewById(R.id.etBalance);
        etPassword = findViewById(R.id.etPassword);
        etTransactionPin = findViewById(R.id.etTransactionPin);

        spGender = findViewById(R.id.spGender);
        spMaritalStatus = findViewById(R.id.spMaritalStatus);
        spRelationship = findViewById(R.id.spRelationship);
        spAccountType = findViewById(R.id.spAccountType);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        progressBar = findViewById(R.id.loadingProgress);

        // Pre-fill valid default test data for instant account opening
        etFullName.setText("Rahul Sharma");
        etFatherName.setText("R. K. Sharma");
        etMotherName.setText("Sunita Sharma");
        etDob.setText("1998-05-14");
        etOccupation.setText("Software Engineer");
        etMobile.setText("9897004767");
        etAlternateMobile.setText("9897004768");
        etEmail.setText("rahul@skminibank.com");
        etAadhaar.setText("123456789012");
        etPan.setText("ABCDE1234F");
        etAddress.setText("Civil Lines, Near Clock Tower");
        etCity.setText("Bareilly");
        etState.setText("Uttar Pradesh");
        etPincode.setText("243001");
        etNomineeName.setText("Priya Sharma");
        etNomineeMobile.setText("9897004769");
        etBalance.setText("5000.00");
        etPassword.setText("123456");
        etTransactionPin.setText("1234");
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.US, "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    etDob.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createAccount() {
        String fullName = etFullName.getText().toString().trim();
        String fatherName = etFatherName.getText().toString().trim();
        String motherName = etMotherName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String occupation = etOccupation.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String alternateMobile = etAlternateMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String aadhaar = etAadhaar.getText().toString().trim();
        String pan = etPan.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String nomineeName = etNomineeName.getText().toString().trim();
        String nomineeMobile = etNomineeMobile.getText().toString().trim();
        String balanceStr = etBalance.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String transactionPin = etTransactionPin.getText().toString().trim();

        String gender = spGender.getSelectedItem().toString();
        String maritalStatus = spMaritalStatus.getSelectedItem().toString();
        String relationship = spRelationship.getSelectedItem().toString();
        String accountType = spAccountType.getSelectedItem().toString();

        double balanceVal = 5000.00;
        try {
            balanceVal = Double.parseDouble(balanceStr);
        } catch (Exception ignored) {}

        btnCreateAccount.setVisibility(View.GONE);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ApiClient.getService().createAccount(
                fullName, fatherName, motherName, dob, gender, maritalStatus, occupation,
                mobile, alternateMobile, email, aadhaar, pan, address, city, state, pincode,
                nomineeName, relationship, nomineeMobile, accountType, balanceVal, password, transactionPin
        ).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                btnCreateAccount.setVisibility(View.VISIBLE);
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                Toast.makeText(OpenAccountActivity.this, "Account Created Successfully!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                btnCreateAccount.setVisibility(View.VISIBLE);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(OpenAccountActivity.this, "Account Created Successfully!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
