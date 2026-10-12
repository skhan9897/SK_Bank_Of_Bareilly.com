package com.bank.skminibank.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.GenericResponse;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstantAccountActivity extends AppCompatActivity {

    private EditText etFull, etFather, etMother, etDob, etMobile, etEmail, etAadhaar, etPan, etAddress, etCity, etPincode, etPass, etPin;
    private Spinner spGender, spMarital;
    private ImageView ivPhoto;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etFull = findViewById(R.id.etFullName);
        etFather = findViewById(R.id.etFatherName);
        etMother = findViewById(R.id.etMotherName);
        etDob = findViewById(R.id.etDob);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etAadhaar = findViewById(R.id.etAadhaar);
        etPan = findViewById(R.id.etPan);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etPincode = findViewById(R.id.etPincode);
        etPass = findViewById(R.id.etPassword);
        etPin = findViewById(R.id.etPin);
        spGender = findViewById(R.id.spGender);
        spMarital = findViewById(R.id.spMarital);
        ivPhoto = findViewById(R.id.ivUserPhoto);
        btnSubmit = findViewById(R.id.btnInstantOpen);
    }

    private void setupListeners() {
        etDob.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etDob.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        ivPhoto.setOnClickListener(v -> {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, 100);
        });

        btnSubmit.setOnClickListener(v -> submitForm());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivPhoto.setImageBitmap(photo);
            ivPhoto.setPadding(0, 0, 0, 0);
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void submitForm() {
        // Simple Validation
        if (etFull.getText().toString().isEmpty() || etMobile.getText().length() != 10) {
            Toast.makeText(this, "Please fill valid details", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Processing...");

        ApiClient.getService().createAccount(
                etFull.getText().toString(),
                etFather.getText().toString(),
                etMother.getText().toString(),
                etDob.getText().toString(),
                spGender.getSelectedItem().toString(),
                spMarital.getSelectedItem().toString(),
                "Self Employed", // Default
                etMobile.getText().toString(),
                "", // Alternate
                etEmail.getText().toString(),
                etAadhaar.getText().toString(),
                etPan.getText().toString(),
                etAddress.getText().toString(),
                etCity.getText().toString(),
                "State", // Default
                etPincode.getText().toString(),
                "Self", "Self", "0000000000", // Nominee defaults
                "Savings Account",
                1000.0,
                etPass.getText().toString(),
                etPin.getText().toString()
        ).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("OPEN MY ACCOUNT INSTANTLY");
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(InstantAccountActivity.this, "CONGRATULATIONS! Account Opened.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(InstantAccountActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("OPEN MY ACCOUNT INSTANTLY");
                Toast.makeText(InstantAccountActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
