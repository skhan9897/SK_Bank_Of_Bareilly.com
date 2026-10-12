package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bank.skminibank.R;
import com.bank.skminibank.adapters.TransactionsAdapter;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.database.AppDatabase;
import com.bank.skminibank.database.TransactionEntity;
import com.bank.skminibank.model.DashboardResponse;
import com.bank.skminibank.model.Transaction;
import com.bank.skminibank.model.TransactionResponse;
import com.bank.skminibank.utils.SessionManager;
import com.bank.skminibank.service.PaymentNotificationService;
import com.bank.skminibank.utils.PaymentVoiceUtil;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeUser, tvBalanceAmount, tvAccNo;
    private ImageView ivProfileHeader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SessionManager sessionManager;
    private BottomNavigationView bottomNavigationView;
    private ImageView btnToggleBalance;
    private RecyclerView rvRecentTransactions;
    private TransactionsAdapter transactionAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private AppDatabase db;

    private double lastFetchedBalance = 0.0;
    private boolean isBalanceVisible = false;
    private TextToSpeech tts;
    private Handler pollingHandler = new Handler();
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);
        db = AppDatabase.getInstance(this);
        
        try {
            Intent paymentService = new Intent(this, PaymentNotificationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, paymentService);
            } else {
                startService(paymentService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshDashboard);
        bottomNavigationView = findViewById(R.id.bottomNav);
        rvRecentTransactions = findViewById(R.id.rvRecentTransactions);
        
        // Setup RecyclerView
        if (rvRecentTransactions != null) {
            rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this));
            transactionAdapter = new TransactionsAdapter(transactionList);
            rvRecentTransactions.setAdapter(transactionAdapter);
        }

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        tvAccNo = findViewById(R.id.tvAccNo);
        btnToggleBalance = findViewById(R.id.btnToggleBalance);
        ivProfileHeader = findViewById(R.id.ivProfileHeader);

        setupTTS();
        setupSpayActions();
        setupServiceGrid();
        setupBottomNav();
        
        populateBasicInfo();
        loadRecentTransactionsFromLocal();
        refreshData();
        startPolling();
        
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
        
        findViewById(R.id.btnBalanceInquiry).setOnClickListener(v -> startPinActivity());

        View btnViewAll = findViewById(R.id.btnViewAll);
        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(v -> startActivity(new Intent(this, TransactionsActivity.class)));
        }

        if (btnToggleBalance != null) {
            btnToggleBalance.setOnClickListener(v -> {
                isBalanceVisible = !isBalanceVisible;
                updateBalanceVisibility();
            });
        }

        View fabScan = findViewById(R.id.fabScan);
        if (fabScan != null) {
            fabScan.setOnClickListener(v -> startScanner());
        }

        View searchContainer = findViewById(R.id.searchContainer);
        if (searchContainer != null) {
            searchContainer.setOnClickListener(v -> startActivity(new Intent(this, AllServicesActivity.class)));
        }

        View ivNotifications = findViewById(R.id.ivNotifications);
        if (ivNotifications != null) {
            ivNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationHistoryActivity.class)));
        }

        View btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }

        View btnProfileHeader = findViewById(R.id.btnProfileHeader);
        if (btnProfileHeader != null) {
            btnProfileHeader.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        View btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> handleLogout());
        }
    }

    private void setupSpayActions() {
        setupActionPill(R.id.spayScan, "Scan & Pay", android.R.drawable.ic_menu_camera, v -> startScanner());
        setupActionPill(R.id.spayToContact, "To Mobile", android.R.drawable.ic_menu_call, v -> {
            Intent intent = new Intent(this, UpiPaymentActivity.class);
            intent.putExtra("mode", "mobile");
            startActivity(intent);
        });
        setupActionPill(R.id.spayToUpi, "To UPI ID", android.R.drawable.ic_menu_send, v -> {
            Intent intent = new Intent(this, UpiPaymentActivity.class);
            intent.putExtra("mode", "upi");
            startActivity(intent);
        });
        setupActionPill(R.id.spayToBank, "To Bank", android.R.drawable.ic_input_add, v -> startActivity(new Intent(this, TransferActivity.class)));
    }

    private void setupActionPill(int id, String title, int icon, View.OnClickListener listener) {
        View v = findViewById(id);
        if (v != null) {
            TextView tv = v.findViewById(R.id.tvActionTitle);
            ImageView iv = v.findViewById(R.id.ivActionIcon);
            if (tv != null) tv.setText(title);
            if (iv != null) iv.setImageResource(icon);
            v.setOnClickListener(listener);
        }
    }

    private void handleLogout() {
        sessionManager.logoutUser();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupServiceGrid() {
        // Quick Actions
        setupServiceCard(R.id.cardFundTransfer, "Fund Transfer", android.R.drawable.ic_menu_send, v -> startActivity(new Intent(this, TransferActivity.class)));
        setupServiceCard(R.id.cardToMobile, "To Mobile & UPI", android.R.drawable.ic_menu_call, v -> startActivity(new Intent(this, SendMoneyActivity.class)));
        setupServiceCard(R.id.cardToWallet, "To Wallet", android.R.drawable.ic_menu_save, v -> startActivity(new Intent(this, RazorpayActivity.class)));
        
        // Main Services
        setupServiceCard(R.id.cardBalanceInq, "Balance Inquiry", android.R.drawable.ic_menu_info_details, v -> startPinActivity());
        setupServiceCard(R.id.cardWithdrawal, "Cash Withdrawal", android.R.drawable.ic_menu_myplaces, v -> startActivity(new Intent(this, CashWithdrawalActivity.class)));
        setupServiceCard(R.id.cardPassbook, "Passbook", android.R.drawable.ic_menu_agenda, v -> startActivity(new Intent(this, TransactionsActivity.class)));
        
        setupServiceCard(R.id.cardFixedDeposit, "Fixed Deposit", android.R.drawable.ic_menu_view, v -> startActivity(new Intent(this, FixedDepositActivity.class)));

        setupServiceCard(R.id.cardUpiPayment, "UPI Payment", android.R.drawable.ic_menu_camera, v -> startActivity(new Intent(this, UpiActivity.class)));
        setupServiceCard(R.id.cardScanPay, "Scan & Pay", android.R.drawable.ic_menu_camera, v -> startScanner());
        
        setupServiceCard(R.id.cardBeneficiary, "View Beneficiary", android.R.drawable.ic_menu_mylocation, v -> startActivity(new Intent(this, AddBankActivity.class)));
        setupServiceCard(R.id.cardAtm, "ATM Card", android.R.drawable.ic_lock_lock, v -> startActivity(new Intent(this, AtmApplyActivity.class)));
        setupServiceCard(R.id.cardKyc, "Update KYC", android.R.drawable.ic_menu_save, v -> startActivity(new Intent(this, KYCUpdateActivity.class)));

        setupServiceCard(R.id.cardRequests, "Requests", android.R.drawable.ic_menu_edit, v -> startActivity(new Intent(this, MyRequestsActivity.class)));
        setupServiceCard(R.id.cardOffers, "Offers", android.R.drawable.ic_menu_gallery, v -> startActivity(new Intent(this, ReferEarnActivity.class)));
        setupServiceCard(R.id.cardMore, "All Services", android.R.drawable.ic_menu_more, v -> startActivity(new Intent(this, AllServicesActivity.class)));
    }

    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_history) {
                startActivity(new Intent(this, TransactionsActivity.class));
            } else if (id == R.id.nav_home) {
                refreshData();
            } else if (id == R.id.nav_placeholder) { // Scan & Pay
                startScanner();
                return false;
            }
            return true;
        });
    }

    private void populateBasicInfo() {
        String name = sessionManager.getCustomerName();
        tvWelcomeUser.setText("Hello, " + (name != null ? name : "User"));
        
        String acc = sessionManager.getAccountNumber();
        tvAccNo.setText("Account No. " + (acc != null ? acc : "XXXX XXXX XXXX"));
        
        updateBalanceVisibility();
    }

    private void setupServiceCard(int id, String title, int icon, View.OnClickListener listener) {
        View v = findViewById(id);
        if (v != null) {
            TextView tv = v.findViewById(R.id.tvTitle);
            ImageView iv = v.findViewById(R.id.ivIcon);
            if (tv != null) tv.setText(title);
            if (iv != null) iv.setImageResource(icon);
            
            // Explicitly set click listener on the CardView itself
            v.setClickable(true);
            v.setFocusable(true);
            v.setOnClickListener(listener);
            
            // Also set on children just in case they consume events
            View inner = v.findViewById(R.id.tvTitle);
            if (inner != null) inner.setOnClickListener(listener);
        }
    }

    private void updateBalanceVisibility() {
        if (btnToggleBalance == null) return;
        if (isBalanceVisible) {
            btnToggleBalance.setImageResource(android.R.drawable.ic_menu_view);
            tvBalanceAmount.setText(String.format(Locale.getDefault(), "₹ %.2f", lastFetchedBalance));
        } else {
            btnToggleBalance.setImageResource(android.R.drawable.ic_secure);
            tvBalanceAmount.setText("₹ •••••");
        }
    }

    private void refreshData() {
        int customerId = sessionManager.getCustomerId();
        String accNo = sessionManager.getAccountNumber();
        if (customerId != -1) {
            fetchDashboardData(customerId);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
        
        if (accNo != null) {
            fetchRecentTransactions(accNo);
        }
    }

    private void fetchDashboardData(int customerId) {
        ApiClient.getService().getDashboardData(customerId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchRecentTransactions(String accNo) {
        if (accNo == null) return;
        String cleanAcc = accNo.replaceAll("\\s+", "");
        int customerId = sessionManager.getCustomerId();

        ApiClient.getService().getTransactions(cleanAcc, customerId).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> txns = response.body().getTransactions();
                    if (txns != null && !txns.isEmpty()) {
                        // Update UI instantly
                        transactionList.clear();
                        for (int i = 0; i < Math.min(txns.size(), 10); i++) {
                            transactionList.add(txns.get(i));
                        }
                        if (transactionAdapter != null) {
                            transactionAdapter.notifyDataSetChanged();
                        }
                        
                        // Sync with local database
                        saveTransactionsToLocal(txns);
                    } else if (transactionList.isEmpty()) {
                        loadRecentTransactionsFromLocal();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                loadRecentTransactionsFromLocal();
            }
        });
    }

    private void loadRecentTransactionsFromLocal() {
        String acc = sessionManager.getAccountNumber();
        if (acc == null) return;
        String cleanAcc = acc.replaceAll("\\s+", "");
        
        new Thread(() -> {
            // 1. Get from Transactions table
            List<TransactionEntity> entities = db.transactionDao().getAllTransactions(cleanAcc);
            
            // 2. ALSO Get from Chats table (important for instant display of recent transfers)
            List<com.bank.skminibank.database.ChatMessageEntity> chatMessages = db.chatDao().getAllMessages(cleanAcc);
            
            runOnUiThread(() -> {
                transactionList.clear();
                java.util.Set<String> ids = new java.util.HashSet<>();
                
                // Add from transactions table (up to 10)
                for (int i = 0; i < Math.min(entities.size(), 10); i++) {
                    TransactionEntity e = entities.get(i);
                    transactionList.add(new Transaction(e.getTransactionId(), e.getType(), e.getAmount(), e.getDescription(), e.getDate(), e.getBalanceAfter()));
                    if (e.getTransactionId() != null) ids.add(e.getTransactionId());
                }
                
                // Add from chat messages if not already present (match by txnId)
                for (com.bank.skminibank.database.ChatMessageEntity cm : chatMessages) {
                    if (transactionList.size() >= 10) break;
                    if (cm.getType() == 2 && cm.getTransactionId() != null && !ids.contains(cm.getTransactionId())) { // TYPE_PAYMENT
                        String type = cm.isSentByMe() ? "DEBIT" : "CREDIT";
                        transactionList.add(new Transaction(cm.getTransactionId(), type, cm.getAmount(), cm.getContent(), cm.getTimestamp(), -1));
                        ids.add(cm.getTransactionId());
                    }
                }
                
                // Sort by date (descending)
                java.util.Collections.sort(transactionList, (t1, t2) -> {
                    if (t1.getDate() == null || t2.getDate() == null) return 0;
                    return t2.getDate().compareTo(t1.getDate());
                });

                if (transactionAdapter != null) {
                    transactionAdapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

    private void saveTransactionsToLocal(List<Transaction> txns) {
        String acc = sessionManager.getAccountNumber();
        if (acc == null) return;
        String cleanAcc = acc.replaceAll("\\s+", "");
        
        new Thread(() -> {
            for (Transaction t : txns) {
                String tid = t.getTransactionId();
                if (tid == null || tid.isEmpty() || tid.equals("null")) {
                    tid = "TXN_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
                }
                
                if (!db.transactionDao().isTransactionExists(cleanAcc, tid)) {
                    db.transactionDao().insertTransaction(new TransactionEntity(
                            cleanAcc, tid, t.getType(), t.getAmount(), t.getDescription(), t.getDate(), t.getBalanceAfter()
                    ));
                }
            }
            loadRecentTransactionsFromLocal();
        }).start();
    }

    private void updateUI(DashboardResponse data) {
        double newBalance = data.getBalance();
        if (!isFirstLoad && newBalance > lastFetchedBalance) {
            double diff = newBalance - lastFetchedBalance;
            PaymentVoiceUtil.speakPayment(this, diff, true);
        }
        
        lastFetchedBalance = newBalance;
        isFirstLoad = false;
        
        updateBalanceVisibility();
        
        if (data.getAccountNumber() != null) {
            String cleanAcc = data.getAccountNumber().replaceAll("\\s+", "");
            sessionManager.setAccountNumber(cleanAcc);
            tvAccNo.setText("Account No. " + cleanAcc);
            
            // Immediately fetch transactions for this account
            fetchRecentTransactions(cleanAcc);
        }
        
        if (data.getCustomerName() != null) {
            tvWelcomeUser.setText("Hello, " + data.getCustomerName());
        }

        if (data.getKycStatus() != null) {
            sessionManager.setKycStatus(data.getKycStatus());
        }

        if (data.getPhoto() != null && ivProfileHeader != null) {
            sessionManager.setPhoto(data.getPhoto());
            String photoUrl = "https://skminibank-1.onrender.com/uploads/customer_photos/" + data.getPhoto();
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(ivProfileHeader);
        }
    }

    private void startScanner() {
        // Assume VerticalCaptureActivity is used for scanning
        Intent intent = new Intent(this, VerticalCaptureActivity.class);
        startActivity(intent);
    }

    private void startPinActivity() {
        Intent intent = new Intent(this, UpiPinActivity.class);
        intent.putExtra("purpose", "balance");
        startActivity(intent);
    }

    private void showPlaceholder(String feature) {
        Toast.makeText(this, feature + " feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("en", "IN"));
            }
        });
    }

    private void startPolling() {
        pollingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshData();
                pollingHandler.postDelayed(this, 10000); // Check every 10 seconds
            }
        }, 10000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
        // Force log for debugging (can be removed later)
        android.util.Log.d("Dashboard", "Current KYC Status in Session: " + sessionManager.getKycStatus());
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        pollingHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
