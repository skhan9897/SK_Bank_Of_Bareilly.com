package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skminibank.R;
import com.bank.skminibank.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View card = findViewById(R.id.containerMain);
        ProgressBar progressBar = findViewById(R.id.splashProgress);

        // 1. Premium Entrance Animation
        card.setAlpha(0f);
        card.setScaleX(0.9f);
        card.setScaleY(0.9f);
        
        card.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // 2. Progress Bar Logic
        android.animation.ObjectAnimator progressAnimator = android.animation.ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(2500);
        progressAnimator.setInterpolator(new android.view.animation.LinearInterpolator());
        progressAnimator.start();

        // 3. Transition to Login (Always show login for security)
        new Handler().postDelayed(() -> {
            if (!isFinishing()) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 3000);
    }
}
