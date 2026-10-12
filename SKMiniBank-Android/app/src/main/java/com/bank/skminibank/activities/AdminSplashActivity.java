package com.bank.skminibank.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skminibank.R;

public class AdminSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_splash);

        View logoBox = findViewById(R.id.logoBox);
        ProgressBar progressBar = findViewById(R.id.splashProgress);
        TextView tvStatus = findViewById(R.id.tvStatus);

        // 1. Initial State
        logoBox.setAlpha(0f);
        logoBox.setScaleX(0.9f);
        logoBox.setScaleY(0.9f);

        // 2. Animation Sequence
        logoBox.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // 3. Progress Animation (2.5 Seconds)
        android.animation.ObjectAnimator progressAnimator = android.animation.ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(2500);
        progressAnimator.setInterpolator(new android.view.animation.LinearInterpolator());
        progressAnimator.start();

        // Update status text
        new Handler().postDelayed(() -> tvStatus.setText("Establishing Encrypted Connection..."), 800);
        new Handler().postDelayed(() -> tvStatus.setText("Syncing Administrative Data..."), 1600);

        // 4. Transition
        new Handler().postDelayed(() -> {
            if (isFinishing()) return;
            Intent intent = new Intent(AdminSplashActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 3000);
    }
}
