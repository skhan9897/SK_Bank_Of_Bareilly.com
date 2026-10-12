package com.bank.skminibank.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.utils.SessionManager;
import com.bank.skminibank.utils.PaymentVoiceUtil;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentNotificationService extends Service {

    private static final String TAG = "PaymentService";
    private static final String CHANNEL_ID = "PaymentVoiceChannel";
    private Handler handler = new Handler();
    private SessionManager sessionManager;
    private boolean isRunning = true;

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);
        createNotificationChannel();
        
        // Android 14 (API 34) Foreground Service fix
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(101, getForegroundNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(101, getForegroundNotification());
        }

        startPolling();
    }

    private void startPolling() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                int customerId = sessionManager.getCustomerId();
                if (customerId != -1) {
                    Log.d(TAG, "Checking balance for customer: " + customerId);
                    checkBalance(customerId);
                } else {
                    Log.d(TAG, "No customer ID found, skipping balance check");
                }
                handler.postDelayed(this, 1000); // Improved: Check every 1 second for "Instant" voice alert
            }
        }, 1000);
    }

    private void checkBalance(int customerId) {
        ApiClient.getService().getDashboardData(customerId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    float currentBalance = (float) response.body().getBalance();
                    float lastBalance = sessionManager.getLastBalance();
                    
                    Log.d(TAG, "Current balance: " + currentBalance + ", Last balance: " + lastBalance);

                    if (lastBalance != -1 && currentBalance > lastBalance) {
                        double received = currentBalance - lastBalance;
                        Log.d(TAG, "Payment received: " + received);
                        PaymentVoiceUtil.speakPayment(PaymentNotificationService.this, received, true);
                    }
                    sessionManager.setLastBalance(currentBalance);
                } else {
                    Log.e(TAG, "Balance check failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Balance check failed: " + t.getMessage());
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Payment Voice Alert Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification getForegroundNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SK Bank Secure Sync")
                .setContentText("Listening for incoming payments...")
                .setSmallIcon(R.drawable.sk_logo)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Service destroyed");
        super.onDestroy();
    }
}