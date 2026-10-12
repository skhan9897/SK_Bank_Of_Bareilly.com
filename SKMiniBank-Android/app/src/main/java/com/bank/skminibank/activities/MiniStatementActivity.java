package com.bank.skminibank.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
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
import com.bank.skminibank.model.Transaction;
import com.bank.skminibank.model.TransactionResponse;
import com.bank.skminibank.utils.SessionManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiniStatementActivity extends AppCompatActivity {

    private RecyclerView rvMiniStatement;
    private TransactionsAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_statement);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mini Statement");
        }

        sessionManager = new SessionManager(this);
        rvMiniStatement = findViewById(R.id.rvMiniStatement);
        swipeRefresh = findViewById(R.id.swipeRefreshMini);
        
        rvMiniStatement.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionsAdapter(transactionList);
        rvMiniStatement.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::fetchMiniStatement);
        
        findViewById(R.id.btnDownloadMiniPdf).setOnClickListener(v -> downloadStatement());

        fetchMiniStatement();
        startWaveAnimation();
    }

    private void downloadStatement() {
        String accountNumber = sessionManager.getAccountNumber();
        int customerId = sessionManager.getCustomerId();
        if (accountNumber == null) return;

        String cleanAcc = accountNumber.replaceAll("\\s+", "").toUpperCase(java.util.Locale.getDefault());
        String url = "https://skminibank-1.onrender.com/TransactionPDFServlet?accountNumber=" + cleanAcc + "&customerId=" + customerId;
        
        Toast.makeText(this, "Generating Mini Statement PDF...", Toast.LENGTH_SHORT).show();
        
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMiniStatement();
    }

    private void startWaveAnimation() {
        View root = findViewById(R.id.miniStatementRoot);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchMiniStatement() {
        swipeRefresh.setRefreshing(true);
        String accountNumber = sessionManager.getAccountNumber();
        int customerId = sessionManager.getCustomerId();
        
        if (accountNumber == null) {
            swipeRefresh.setRefreshing(false);
            return;
        }

        String apiAcc = accountNumber.replaceAll("\\s+", "").toUpperCase(java.util.Locale.getDefault());
        
        // Load local first for instant display
        loadLocalTransactions();

        ApiClient.getService().getTransactions(apiAcc, customerId).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                swipeRefresh.setRefreshing(true); // Keep refreshing for a bit if we want, or just turn off
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> txns = response.body().getTransactions();
                    if (txns != null && !txns.isEmpty()) {
                        // Merge or update local logic could go here, but for now just refresh local view
                        saveTransactionsLocally(txns);
                    }
                }
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MiniStatementActivity.this, "Viewing offline data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocalTransactions() {
        String acc = sessionManager.getAccountNumber();
        if (acc == null) return;
        String cleanAcc = acc.replaceAll("\\s+", "");
        com.bank.skminibank.database.AppDatabase db = com.bank.skminibank.database.AppDatabase.getInstance(this);

        new Thread(() -> {
            List<com.bank.skminibank.database.TransactionEntity> entities = db.transactionDao().getAllTransactions(cleanAcc);
            List<com.bank.skminibank.database.ChatMessageEntity> chats = db.chatDao().getAllMessages(cleanAcc);
            
            runOnUiThread(() -> {
                transactionList.clear();
                java.util.Set<String> ids = new java.util.HashSet<>();
                
                for (com.bank.skminibank.database.TransactionEntity e : entities) {
                    transactionList.add(new Transaction(e.getTransactionId(), e.getType(), e.getAmount(), e.getDescription(), e.getDate(), e.getBalanceAfter()));
                    if (e.getTransactionId() != null) ids.add(e.getTransactionId());
                }
                
                for (com.bank.skminibank.database.ChatMessageEntity c : chats) {
                    if (c.getType() == 2 && c.getTransactionId() != null && !ids.contains(c.getTransactionId())) {
                        String type = c.isSentByMe() ? "DEBIT" : "CREDIT";
                        transactionList.add(new Transaction(c.getTransactionId(), type, c.getAmount(), c.getContent(), c.getTimestamp(), -1));
                    }
                }
                
                java.util.Collections.sort(transactionList, (t1, t2) -> {
                    if (t1.getDate() == null || t2.getDate() == null) return 0;
                    return t2.getDate().compareTo(t1.getDate());
                });
                
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void saveTransactionsLocally(List<Transaction> txns) {
        String acc = sessionManager.getAccountNumber();
        if (acc == null) return;
        String cleanAcc = acc.replaceAll("\\s+", "");
        com.bank.skminibank.database.AppDatabase db = com.bank.skminibank.database.AppDatabase.getInstance(this);
        
        new Thread(() -> {
            for (Transaction t : txns) {
                if (!db.transactionDao().isTransactionExists(cleanAcc, t.getTransactionId())) {
                    db.transactionDao().insertTransaction(new com.bank.skminibank.database.TransactionEntity(
                            cleanAcc, t.getTransactionId(), t.getType(), t.getAmount(), t.getDescription(), t.getDate(), t.getBalanceAfter()
                    ));
                }
            }
            loadLocalTransactions();
        }).start();
    }
}