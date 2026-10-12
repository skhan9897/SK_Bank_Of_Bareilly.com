package com.bank.skminibank.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bank.skminibank.R;

public class AllServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupBankingServices();
        setupRechargeServices();
        setupTravelServices();
        startWaveAnimation();
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.allServicesRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    private void setupBankingServices() {
        setupSvc(R.id.svcLoan, "Apply Loan", android.R.drawable.ic_menu_agenda, v -> startActivity(new Intent(this, LoanActivity.class)));
        setupSvc(R.id.svcAtm, "Manage Card", android.R.drawable.ic_menu_myplaces, v -> startActivity(new Intent(this, ManageCardActivity.class)));
        setupSvc(R.id.svcCheque, "Cheque Book", android.R.drawable.ic_menu_edit, v -> startActivity(new Intent(this, ChequeBookActivity.class)));
        setupSvc(R.id.svcNetBanking, "Net Banking", android.R.drawable.ic_menu_view, v -> startActivity(new Intent(this, NetBankingActivity.class)));
        setupSvc(R.id.svcMobileBanking, "Mobile Banking", android.R.drawable.ic_menu_call, v -> startActivity(new Intent(this, MobileBankingActivity.class)));
        setupSvc(R.id.svcKyc, "Update KYC", android.R.drawable.ic_menu_save, v -> startActivity(new Intent(this, KYCUpdateActivity.class)));
        setupSvc(R.id.svcStatement, "E-Statement", android.R.drawable.ic_menu_recent_history, v -> startActivity(new Intent(this, TransactionsActivity.class)));
        setupSvc(R.id.svcFd, "Fixed Deposit", android.R.drawable.ic_input_add, v -> {
            Intent intent = new Intent(this, WebPortalActivity.class);
            intent.putExtra("url", "https://skminibank.onrender.com/admin/fixed-deposit.jsp");
            intent.putExtra("title", "Fixed Deposit");
            startActivity(intent);
        });
    }

    private void setupRechargeServices() {
        setupSvc(R.id.svcRecharge, "Mobile", android.R.drawable.ic_menu_call, v -> showPlaceholder("Mobile Recharge"));
        setupSvc(R.id.svcDth, "DTH", android.R.drawable.ic_menu_slideshow, v -> showPlaceholder("DTH Recharge"));
        setupSvc(R.id.svcElectricity, "Electricity", android.R.drawable.ic_menu_view, v -> startActivity(new Intent(this, ElectricityBillActivity.class)));
        setupSvc(R.id.svcCreditCard, "CC Bill", android.R.drawable.ic_menu_manage, v -> startActivity(new Intent(this, CreditCardActivity.class)));
        setupSvc(R.id.svcGas, "Gas Bill", android.R.drawable.ic_menu_edit, v -> startActivity(new Intent(this, GasBillActivity.class)));
        setupSvc(R.id.svcWater, "Water Bill", android.R.drawable.ic_menu_compass, v -> showPlaceholder("Water Bill"));
        setupSvc(R.id.svcInsurance, "Insurance", android.R.drawable.ic_menu_save, v -> showPlaceholder("Insurance"));
        setupSvc(R.id.svcFastag, "FASTag", android.R.drawable.ic_menu_directions, v -> showPlaceholder("FASTag"));
    }

    private void setupTravelServices() {
        setupSvc(R.id.svcFlights, "Flights", android.R.drawable.ic_menu_send, v -> showPlaceholder("Flights"));
        setupSvc(R.id.svcBus, "Bus", android.R.drawable.ic_menu_send, v -> showPlaceholder("Bus"));
        setupSvc(R.id.svcTrains, "Trains", android.R.drawable.ic_menu_send, v -> showPlaceholder("Trains"));
        setupSvc(R.id.svcHotels, "Hotels", android.R.drawable.ic_menu_send, v -> showPlaceholder("Hotels"));
    }

    private void setupSvc(int id, String title, int icon, View.OnClickListener listener) {
        View v = findViewById(id);
        if (v != null) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
            ((ImageView) v.findViewById(R.id.ivIcon)).setImageResource(icon);
            v.setOnClickListener(listener);
        }
    }

    private void showPlaceholder(String feature) {
        Toast.makeText(this, feature + " feature coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
