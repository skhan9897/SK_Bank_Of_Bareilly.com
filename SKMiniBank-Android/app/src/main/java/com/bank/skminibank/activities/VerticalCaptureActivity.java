package com.bank.skminibank.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bank.skminibank.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class VerticalCaptureActivity extends CaptureActivity {

    private boolean isFlashOn = false;
    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        findViewById(R.id.btnGalleryScanner).setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickIntent, 1001);
        });

        findViewById(R.id.btnBackScanner).setOnClickListener(v -> finish());

        findViewById(R.id.btnFlashlight).setOnClickListener(v -> toggleFlash());
    }

    private void toggleFlash() {
        if (isFlashOn) {
            barcodeView.setTorchOff();
            isFlashOn = false;
        } else {
            barcodeView.setTorchOn();
            isFlashOn = true;
        }
    }

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_custom_scanner);
        barcodeView = findViewById(R.id.zxing_barcode_scanner);
        return barcodeView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri == null) return;
            try (InputStream is = getContentResolver().openInputStream(imageUri)) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                if (bitmap == null) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    return;
                }
                String result = scanQRImage(bitmap);
                if (result != null) {
                    Intent intent = new Intent();
                    intent.putExtra("SCAN_RESULT", result);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(this, "No QR Code found. Please select a clear, focused QR image.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("Scanner", "Error", e);
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String scanQRImage(Bitmap bMap) {
        // Limit size to improve performance and prevent OOM
        int maxWidth = 1200;
        int maxHeight = 1200;
        if (bMap.getWidth() > maxWidth || bMap.getHeight() > maxHeight) {
            float ratio = Math.min((float) maxWidth / bMap.getWidth(), (float) maxHeight / bMap.getHeight());
            bMap = Bitmap.createScaledBitmap(bMap, Math.round(ratio * bMap.getWidth()), Math.round(ratio * bMap.getHeight()), true);
        }

        int width = bMap.getWidth();
        int height = bMap.getHeight();
        int[] intArray = new int[width * height];
        bMap.getPixels(intArray, 0, width, 0, 0, width, height);

        LuminanceSource source = new RGBLuminanceSource(width, height, intArray);
        MultiFormatReader reader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        // Try Method 1: standard
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            return reader.decode(bitmap, hints).getText();
        } catch (Exception e) {
            // Try Method 2: Global (better for low contrast)
            try {
                BinaryBitmap bitmap = new BinaryBitmap(new com.google.zxing.common.GlobalHistogramBinarizer(source));
                return reader.decode(bitmap, hints).getText();
            } catch (Exception e1) {
                // Try Method 3: Inverted (white on black)
                try {
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source.invert()));
                    return reader.decode(bitmap, hints).getText();
                } catch (Exception e2) {
                    return null;
                }
            }
        }
    }
}
