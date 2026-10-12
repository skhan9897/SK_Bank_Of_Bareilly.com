package com.bank.skminibank.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.UpiResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQrActivity extends AppCompatActivity {

    private ImageView ivQr;
    private TextView tvUpiId, tvCustomerName;
    private SessionManager sessionManager;
    private Bitmap generatedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        ivQr = findViewById(R.id.ivQrCode);
        tvUpiId = findViewById(R.id.tvUpiId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        
        tvCustomerName.setText(sessionManager.getCustomerName());

        findViewById(R.id.btnShare).setOnClickListener(v -> shareQr());
        findViewById(R.id.btnDownload).setOnClickListener(v -> saveQr());

        // INITIAL STEP: Immediately generate a placeholder QR using mobile number 
        // so the screen isn't blank while waiting for the server.
        String mobile = sessionManager.getMobile();
        if (mobile != null && !mobile.isEmpty()) {
            String initialUpi = mobile + "@skpay";
            tvUpiId.setText("UPI ID: " + initialUpi);
            generateQrCode(initialUpi);
        }

        // Then try to fetch the actual UPI ID from server
        fetchUpiDetails();
        startWaveAnimation();
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.qrRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    private void fetchUpiDetails() {
        String accNo = sessionManager.getAccountNumber();
        String mobile = sessionManager.getMobile();
        
        Log.d("QR_DEBUG", "Fetching UPI for: " + accNo);
        tvUpiId.setText("Fetching UPI Details...");

        ApiClient.getService().getUpiDetails(accNo).enqueue(new Callback<UpiResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpiResponse> call, @NonNull Response<UpiResponse> response) {
                Log.d("QR_DEBUG", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    String upiId = response.body().getUpiId();
                    Log.d("QR_DEBUG", "UPI ID from server: " + upiId);
                    
                    if (upiId != null && !upiId.isEmpty() && !upiId.equalsIgnoreCase("null")) {
                        tvUpiId.setText("UPI ID: " + upiId);
                        generateQrCode(upiId);
                    } else {
                        handleFallback(mobile);
                    }
                } else {
                    handleFallback(mobile);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpiResponse> call, @NonNull Throwable t) {
                Log.e("QR_DEBUG", "API Failure: " + t.getMessage());
                handleFallback(mobile);
            }
        });
    }

    private void handleFallback(String mobile) {
        if (mobile != null && !mobile.isEmpty()) {
            String fallbackUpi = mobile + "@skpay";
            Log.d("QR_DEBUG", "Using Fallback UPI: " + fallbackUpi);
            tvUpiId.setText("UPI ID: " + fallbackUpi);
            generateQrCode(fallbackUpi);
        } else {
            tvUpiId.setText("UPI ID Not Generated");
            Toast.makeText(this, "Please complete your profile to generate QR", Toast.LENGTH_LONG).show();
        }
    }

    private void generateQrCode(String upiId) {
        if (upiId == null || upiId.isEmpty()) {
            Log.e("QR_DEBUG", "Cannot generate QR: upiId is null or empty");
            return;
        }
        
        String name = sessionManager.getCustomerName();
        if (name == null || name.isEmpty()) name = "SK Bank User";
        
        // Correct UPI format for QR codes: upi://pay?pa=address&pn=name
        final String uri = "upi://pay?pa=" + upiId + "&pn=" + Uri.encode(name) + "&mc=0000&mode=02&purpose=11";
        Log.d("QR_DEBUG", "Generating QR for URI: " + uri);
        
        new Thread(() -> {
            try {
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                hints.put(EncodeHintType.MARGIN, 2);

                MultiFormatWriter writer = new MultiFormatWriter();
                BitMatrix bitMatrix = writer.encode(uri, BarcodeFormat.QR_CODE, 600, 600, hints);
                
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                    }
                }
                
                generatedBitmap = bitmap;
                runOnUiThread(() -> {
                    if (ivQr != null) {
                        ivQr.setImageBitmap(generatedBitmap);
                        ivQr.setVisibility(View.VISIBLE);
                        Log.d("QR_DEBUG", "QR Code successfully displayed");
                    }
                });
                
            } catch (Exception e) {
                Log.e("QR_DEBUG", "QR Generation Error", e);
                runOnUiThread(() -> Toast.makeText(MyQrActivity.this, "QR Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void shareQr() {
        if (generatedBitmap == null) return;
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), generatedBitmap, "MyQR", "SK Mini Bank QR");
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Scan this QR to pay me using any UPI app.");
        startActivity(Intent.createChooser(intent, "Share QR via"));
    }

    private void saveQr() {
        if (generatedBitmap == null) return;
        MediaStore.Images.Media.insertImage(getContentResolver(), generatedBitmap, "SKBank_QR_" + System.currentTimeMillis(), "My Payment QR");
        Toast.makeText(this, "QR Code saved to Gallery", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
