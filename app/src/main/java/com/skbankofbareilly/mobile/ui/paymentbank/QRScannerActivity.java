package com.skbankofbareilly.mobile.ui.paymentbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.skbankofbareilly.mobile.databinding.ActivityQrScannerBinding;

public class QRScannerActivity extends AppCompatActivity {

    private ActivityQrScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan UPI QR Code for SK Bank Payment");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "QR Scan Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String qrContent = result.getContents();
                Toast.makeText(this, "Scanned UPI QR: " + qrContent, Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
