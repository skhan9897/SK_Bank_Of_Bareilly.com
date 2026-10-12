package com.bank.skminibank.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bank.skminibank.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

public class TransactionDetailActivity extends AppCompatActivity {

    private LinearLayout receiptContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Transaction Detail");
        }

        receiptContent = findViewById(R.id.receiptContent);
        
        TextView tvAmount = findViewById(R.id.tvDetailAmount);
        TextView tvType = findViewById(R.id.tvDetailType);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        TextView tvTxnId = findViewById(R.id.tvDetailTxnId);
        TextView tvDateTime = findViewById(R.id.tvDetailDateTime);
        TextView tvPostBalance = findViewById(R.id.tvDetailPostBalance);

        // Get Data from Intent
        String amount = getIntent().getStringExtra("amount");
        String type = getIntent().getStringExtra("type");
        String desc = getIntent().getStringExtra("desc");
        String txnId = getIntent().getStringExtra("txnId");
        String date = getIntent().getStringExtra("date");
        double postBalance = getIntent().getDoubleExtra("postBalance", 0.0);

        // Populate Views
        tvAmount.setText(amount);
        tvType.setText(type != null ? type.toUpperCase() : "PAYMENT");
        tvDesc.setText(desc);
        tvTxnId.setText(txnId != null ? txnId : "SK" + System.currentTimeMillis());
        tvDateTime.setText(date);
        tvPostBalance.setText(String.format(Locale.getDefault(), "₹ %.2f", postBalance));

        // Styling Amount based on Type
        if (type != null && type.toUpperCase().contains("CREDIT")) {
            tvAmount.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            tvAmount.setTextColor(Color.parseColor("#C62828"));
        }

        findViewById(R.id.btnShareDetail).setOnClickListener(v -> shareReceipt());
        findViewById(R.id.btnDownloadDetail).setOnClickListener(v -> downloadReceipt());
    }

    private void shareReceipt() {
        Bitmap bitmap = Bitmap.createBitmap(receiptContent.getWidth(), receiptContent.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        receiptContent.draw(canvas);

        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "transaction_receipt.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error sharing receipt", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadReceipt() {
        Bitmap bitmap = Bitmap.createBitmap(receiptContent.getWidth(), receiptContent.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        receiptContent.draw(canvas);

        String fileName = "Receipt_" + System.currentTimeMillis() + ".png";
        try {
            OutputStream fos;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SKMiniBank");
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = getContentResolver().openOutputStream(imageUri);
            } else {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(dir, fileName);
                fos = new FileOutputStream(file);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(this, "Saved to Pictures/SKMiniBank", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving receipt", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
