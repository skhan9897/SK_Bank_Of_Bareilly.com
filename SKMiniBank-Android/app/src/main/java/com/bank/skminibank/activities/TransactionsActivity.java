package com.bank.skminibank.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bank.skminibank.R;
import com.bank.skminibank.adapters.TransactionsAdapter;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.api.ApiService;
import com.bank.skminibank.database.AppDatabase;
import com.bank.skminibank.database.ChatMessageEntity;
import com.bank.skminibank.database.TransactionEntity;
import com.bank.skminibank.model.Transaction;
import com.bank.skminibank.model.TransactionResponse;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    private RecyclerView rvTransactions;
    private TransactionsAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private List<Transaction> fullTransactionList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager sessionManager;
    private MaterialButton btnDownloadPdf;
    private TextView tvTotalTransactions;
    private EditText etSearch;
    private com.google.android.material.chip.Chip chipToday;
    private AppDatabase db;
    private boolean showOnlyToday = false;
    private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Passbook");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        db = AppDatabase.getInstance(this);
        rvTransactions = findViewById(R.id.rvTransactions);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);
        tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        etSearch = findViewById(R.id.etSearch);
        chipToday = findViewById(R.id.chipToday);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionsAdapter(transactionList);
        rvTransactions.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::fetchTransactions);

        btnDownloadPdf.setOnClickListener(v -> downloadStatement());

        if (chipToday != null) {
            chipToday.setOnClickListener(v -> {
                showOnlyToday = !showOnlyToday;
                if (showOnlyToday) {
                    chipToday.setChipBackgroundColorResource(android.R.color.holo_green_dark);
                    filterTransactions(etSearch.getText().toString());
                } else {
                    chipToday.setChipBackgroundColorResource(R.color.premium_gold);
                    filterTransactions(etSearch.getText().toString());
                }
            });
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadTransactionsFromLocal();
        fetchTransactions();
        startWaveAnimation();
    }

    private void loadTransactionsFromLocal() {
        String acc = sessionManager.getAccountNumber();
        if (acc == null) return;
        String cleanAcc = acc.replaceAll("\\s+", "");
        
        new Thread(() -> {
            // 1. Get from Transactions table
            List<TransactionEntity> entities = db.transactionDao().getAllTransactions(cleanAcc);
            
            // 2. ALSO Get from Chats table (important for instant display of recent transfers)
            List<ChatMessageEntity> chatMessages = db.chatDao().getAllMessages(cleanAcc);
            
            runOnUiThread(() -> {
                fullTransactionList.clear();
                
                // Add from transactions table
                for (TransactionEntity e : entities) {
                    fullTransactionList.add(new Transaction(e.getTransactionId(), e.getType(), e.getAmount(), e.getDescription(), e.getDate(), e.getBalanceAfter()));
                }
                
                // Add from chat messages if not already present (match by txnId)
                Set<String> existingIds = new HashSet<>();
                for (Transaction t : fullTransactionList) {
                    if (t.getTransactionId() != null) existingIds.add(t.getTransactionId());
                }
                
                for (ChatMessageEntity cm : chatMessages) {
                    if (cm.getType() == 2 && cm.getTransactionId() != null && !existingIds.contains(cm.getTransactionId())) { // TYPE_PAYMENT
                        String type = cm.isSentByMe() ? "DEBIT" : "CREDIT";
                        fullTransactionList.add(new Transaction(cm.getTransactionId(), type, cm.getAmount(), cm.getContent(), cm.getTimestamp(), -1));
                    }
                }
                
                // Sort by date (descending)
                java.util.Collections.sort(fullTransactionList, (t1, t2) -> {
                    if (t1.getDate() == null || t2.getDate() == null) return 0;
                    return t2.getDate().compareTo(t1.getDate());
                });

                filterTransactions(etSearch.getText().toString());
            });
        }).start();
    }

    private void filterTransactions(String query) {
        transactionList.clear();
        
        // Sane date formats for comparison
        java.text.SimpleDateFormat serverFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        java.text.SimpleDateFormat localFormat = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String todayServer = serverFormat.format(new java.util.Date());
        String todayLocal = localFormat.format(new java.util.Date());
        
        String lowerQuery = query.toLowerCase(Locale.getDefault());

        for (Transaction t : fullTransactionList) {
            boolean matchesSearch = query.isEmpty() ||
                    (t.getDescription() != null && t.getDescription().toLowerCase().contains(lowerQuery)) ||
                    (t.getTransactionId() != null && t.getTransactionId().toLowerCase().contains(lowerQuery)) ||
                    (t.getType() != null && t.getType().toLowerCase().contains(lowerQuery));

            String tDate = t.getDate();
            boolean matchesToday = !showOnlyToday || (tDate != null && (tDate.contains(todayServer) || tDate.contains(todayLocal)));

            if (matchesSearch && matchesToday) {
                transactionList.add(t);
            }
        }

        adapter.notifyDataSetChanged();
        if (tvTotalTransactions != null) {
            tvTotalTransactions.setText("Records Found: " + transactionList.size());
        }
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
            loadTransactionsFromLocal();
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTransactions();
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.transactionsRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    private void downloadStatement() {
        String accountNumber = sessionManager.getAccountNumber();
        int customerId = sessionManager.getCustomerId();
        if (accountNumber == null) return;

        // Clean account number and add customer ID for the server to find records correctly
        String cleanAcc = accountNumber.replaceAll("\\s+", "").toUpperCase(Locale.getDefault());
        String url = "https://skminibank-1.onrender.com/TransactionPDFServlet?accountNumber=" + cleanAcc + "&customerId=" + customerId;
        
        Toast.makeText(this, "Generating Statement PDF...", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void fetchTransactions() {
        if (swipeRefresh != null) swipeRefresh.setRefreshing(true);
        String accountNumber = sessionManager.getAccountNumber();

        if (accountNumber == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }

        String cleanAcc = accountNumber.replaceAll("\\s+", "");
        int customerId = sessionManager.getCustomerId();

        // Try with cleaned account number first
        executeFetch(cleanAcc, customerId, false);
    }

    private void executeFetch(String accNo, int customerId, boolean isRetry) {
        // Robust cleaning: Always use no-space version for API if possible
        String apiAcc = accNo.replaceAll("\\s+", "");
        
        ApiClient.getService().getTransactions(apiAcc, customerId).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> txns = response.body().getTransactions();
                    
                    if (txns != null && !txns.isEmpty()) {
                        // Only clear and update if we actually got new transactions
                        fullTransactionList.clear();
                        fullTransactionList.addAll(txns);
                        saveTransactionsToLocal(txns);
                        filterTransactions(etSearch.getText().toString());
                    } else {
                        // If no transactions found with clean account, try with raw if it's different and not a retry
                        if (!isRetry) {
                            String rawAcc = sessionManager.getAccountNumber();
                            if (rawAcc != null && !rawAcc.equals(apiAcc)) {
                                executeFetch(rawAcc, customerId, true);
                                return;
                            }
                        }
                        
                        // If we reach here, it's either a retry or rawAcc == apiAcc
                        // If the list is empty, show local data instead of an empty screen
                        if (fullTransactionList.isEmpty()) {
                            loadTransactionsFromLocal();
                        } else {
                            filterTransactions(etSearch.getText().toString());
                        }
                        
                        if (txns == null) {
                             Toast.makeText(TransactionsActivity.this, "Unable to fetch latest transactions", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    loadTransactionsFromLocal();
                    Toast.makeText(TransactionsActivity.this, "Server Response Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                loadTransactionsFromLocal();
                Toast.makeText(TransactionsActivity.this, "Network error. Loading offline data...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
