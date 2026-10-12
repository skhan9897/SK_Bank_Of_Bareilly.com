package com.bank.skminibank.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.GenericResponse;
import com.bank.skminibank.model.ProfileResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import android.view.LayoutInflater;
import android.widget.EditText;
import java.util.Locale;

import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvCode, tvKycBadge;
    private ImageView ivProfile;
    private View rowMobile, rowEmail, rowAccNo, rowAccType, rowBranch;
    private SessionManager sessionManager;
    private String currentMobile, currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        sessionManager = new SessionManager(this);
        tvName = findViewById(R.id.tvProfileName);
        tvCode = findViewById(R.id.tvProfileCode);
        tvKycBadge = findViewById(R.id.tvKycBadge);
        ivProfile = findViewById(R.id.ivProfileImg);
        
        rowMobile = findViewById(R.id.rowMobile);
        rowEmail = findViewById(R.id.rowEmail);
        rowAccNo = findViewById(R.id.rowAccNo);
        rowAccType = findViewById(R.id.rowAccType);
        rowBranch = findViewById(R.id.rowBranch);

        setupRows();
        fetchProfile();

        findViewById(R.id.tvProfileName).setOnClickListener(v -> showEditDialog());
    }

    private void showEditDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        EditText etMob = view.findViewById(R.id.etEditMobile);
        EditText etEmail = view.findViewById(R.id.etEditEmail);

        etMob.setText(currentMobile);
        etEmail.setText(currentEmail);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Profile")
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    String m = etMob.getText().toString().trim();
                    String e = etEmail.getText().toString().trim();
                    updateProfile(m, e);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateProfile(String mobile, String email) {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().updateProfile(customerId, mobile, email).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProfileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchProfile(); // Refresh
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRows() {
        setRowLabel(rowMobile, "Mobile");
        setRowLabel(rowEmail, "Email");
        setRowLabel(rowAccNo, "A/C Number");
        setRowLabel(rowAccType, "A/C Type");
        setRowLabel(rowBranch, "Branch");
    }

    private void setRowLabel(View row, String label) {
        if (row != null) {
            TextView tv = row.findViewById(R.id.tvLabel);
            if (tv != null) tv.setText(label);
        }
    }

    private void setRowValue(View row, String value) {
        if (row != null) {
            TextView tv = row.findViewById(R.id.tvValue);
            if (tv != null) tv.setText(value != null ? value : "---");
        }
    }

    private void fetchProfile() {
        int customerId = sessionManager.getCustomerId();
        ApiClient.getService().getProfileData(customerId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse p = response.body();
                    tvName.setText(p.getFullName());
                    tvCode.setText(String.format(Locale.getDefault(), "CUSTOMER ID: %d", p.getCustomerId()));
                    
                    if ("VERIFIED".equalsIgnoreCase(p.getKycStatus())) {
                        tvKycBadge.setVisibility(View.VISIBLE);
                        tvKycBadge.setText("KYC VERIFIED ✅");
                    } else {
                        tvKycBadge.setVisibility(View.VISIBLE);
                        tvKycBadge.setText("KYC PENDING ⚠️");
                        tvKycBadge.setTextColor(Color.parseColor("#E65100"));
                        tvKycBadge.setBackgroundResource(R.drawable.badge_pending);
                    }

                    currentMobile = p.getMobile();
                    currentEmail = p.getEmail();

                    setRowValue(rowMobile, p.getMobile());
                    setRowValue(rowEmail, p.getEmail());
                    setRowValue(rowAccNo, p.getAccountNumber());
                    setRowValue(rowAccType, p.getAccountType());
                    setRowValue(rowBranch, p.getBranch());

                    if (p.getPhoto() != null && ivProfile != null) {
                        sessionManager.setPhoto(p.getPhoto());
                        String photoUrl = "https://skminibank-1.onrender.com/uploads/customer_photos/" + p.getPhoto();
                        Glide.with(ProfileActivity.this)
                                .load(photoUrl)
                                .placeholder(android.R.drawable.ic_menu_myplaces)
                                .error(android.R.drawable.ic_menu_myplaces)
                                .into(ivProfile);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
