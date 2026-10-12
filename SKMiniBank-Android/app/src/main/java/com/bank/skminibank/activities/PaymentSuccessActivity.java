package com.bank.skminibank.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bank.skminibank.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    private LinearLayout receiptLayout;
    private TextToSpeech tts;
    private String amountToSpeak = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        receiptLayout = findViewById(R.id.receiptLayout);
        TextView tvAmount = findViewById(R.id.tvSuccessAmount);
        TextView tvName = findViewById(R.id.tvResName);
        TextView tvAcc = findViewById(R.id.tvResAcc);
        TextView tvBalance = findViewById(R.id.tvResBalance);
        TextView tvTxnId = findViewById(R.id.tvResTxnId);
        TextView tvSuccessTime = findViewById(R.id.tvSuccessTime);
        TextView tvDebitedFrom = findViewById(R.id.tvDebitedFrom);

        String amount = getIntent().getStringExtra("amount");
        String name = getIntent().getStringExtra("name");
        String acc = getIntent().getStringExtra("acc");
        String balance = getIntent().getStringExtra("balance");

        // Sanitize "null" strings from backend or intent
        if ("null".equalsIgnoreCase(name)) name = "Unknown Receiver";
        if ("null".equalsIgnoreCase(acc)) acc = "N/A";
        if ("null".equalsIgnoreCase(amount)) amount = "0.00";
        if ("null".equalsIgnoreCase(balance)) balance = "0.00";

        amountToSpeak = amount != null ? amount : "0";

        // Generate a dummy transaction ID
        String txnId = "SKTXN" + System.currentTimeMillis();
        String currentDateTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());

        tvAmount.setText(String.format("₹ %s", amount));
        tvName.setText(name != null ? name : "Unknown");
        tvAcc.setText(acc != null ? "UPI ID: " + acc : "");
        tvBalance.setText(String.format("₹ %s", balance != null ? balance : "0.0"));
        tvTxnId.setText(txnId);
        tvSuccessTime.setText(currentDateTime);
        
        // Debited from logic
        String fromAcc = new com.bank.skminibank.utils.SessionManager(this).getAccountNumber();
        if (fromAcc != null && fromAcc.length() >= 4) {
            tvDebitedFrom.setText("SK Bank - " + fromAcc.substring(fromAcc.length() - 4));
        } else {
            tvDebitedFrom.setText("SK Bank Account");
        }

        // Initialize TextToSpeech (PhonePe Style Voice)
        setupTTS();

        // Play Success Chime
        playSuccessSound();

        findViewById(R.id.btnDone).setOnClickListener(v -> finish());
        findViewById(R.id.btnShare).setOnClickListener(v -> shareReceipt());
        // btnDownload is removed in new XML to simplify UI, if you want it back I can add it
        View btnDownload = findViewById(R.id.btnDownload);
        if (btnDownload != null) btnDownload.setOnClickListener(v -> downloadReceipt());
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("en", "IN"));
                tts.setPitch(1.1f);
                tts.setSpeechRate(0.9f);
                
                // Delay voice to let chime play first
                new android.os.Handler().postDelayed(() -> {
                    String speechText = "Received payment of " + amountToSpeak + " Rupees on S K Mini Bank";
                    tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "TxnId");
                }, 1000);
            }
        });
    }

    private void playSuccessSound() {
        try {
            // "TEE TUU" Sound using ToneGenerator
            ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen.startTone(ToneGenerator.TONE_DTMF_0, 150); // TEE
            
            new android.os.Handler().postDelayed(() -> {
                toneGen.startTone(ToneGenerator.TONE_DTMF_4, 150); // TUU
            }, 160);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void shareReceipt() {
        Bitmap bitmap = getBitmapFromView(receiptLayout);
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "receipt.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
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
        Bitmap bitmap = getBitmapFromView(receiptLayout);
        String fileName = "SKBank_Receipt_" + System.currentTimeMillis() + ".png";
        try {
            OutputStream fos;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SKMiniBank");
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri != null) {
                    fos = getContentResolver().openOutputStream(imageUri);
                    if (fos != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    }
                }
            } else {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(dir, fileName);
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            }
            Toast.makeText(this, "Saved to Pictures/SKMiniBank", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving receipt", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
