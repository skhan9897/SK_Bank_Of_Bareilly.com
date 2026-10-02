package com.skbankofbareilly.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.skbankofbareilly.mobile.R;
import com.skbankofbareilly.mobile.databinding.ActivityMainBinding;
import com.skbankofbareilly.mobile.security.TokenManager;
import com.skbankofbareilly.mobile.ui.auth.LoginActivity;
import com.skbankofbareilly.mobile.ui.paymentbank.PaymentsBankFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verify session
        if (!TokenManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default Fragment
        loadFragment(new DashboardFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;

            if (itemId == R.id.nav_home) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.nav_accounts) {
                fragment = new AccountsFragment();
            } else if (itemId == R.id.nav_payments) {
                fragment = new PaymentsBankFragment();
            } else if (itemId == R.id.nav_transactions) {
                fragment = new TransactionsFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            return loadFragment(fragment);
        });
    }

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
