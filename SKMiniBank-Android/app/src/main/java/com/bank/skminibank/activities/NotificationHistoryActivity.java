package com.bank.skminibank.activities;

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
import com.bank.skminibank.adapters.NotificationAdapter;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.NotificationResponse;
import com.bank.skminibank.model.NotificationResponse.NotificationItem;
import com.bank.skminibank.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHistoryActivity extends AppCompatActivity {
    private RecyclerView rv;
    private NotificationAdapter adapter;
    private List<NotificationItem> list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(this);
        rv = findViewById(R.id.rvNotifications);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(list);
        rv.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::fetchNotifications);
        fetchNotifications();
    }

    private void fetchNotifications() {
        swipeRefresh.setRefreshing(true);
        ApiClient.getService().getNotifications(session.getCustomerId()).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body().getNotifications());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(NotificationHistoryActivity.this, "Error loading notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
