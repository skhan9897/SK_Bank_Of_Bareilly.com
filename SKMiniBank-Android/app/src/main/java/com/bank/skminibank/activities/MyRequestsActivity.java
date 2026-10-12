package com.bank.skminibank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bank.skminibank.R;
import com.bank.skminibank.adapters.RequestsAdapter;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.api.ApiService;
import com.bank.skminibank.model.MyRequestsResponse;
import com.bank.skminibank.model.ServiceRequest;
import com.bank.skminibank.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestsActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private RequestsAdapter adapter;
    private List<ServiceRequest> requestList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager sessionManager;
    private FloatingActionButton fabNewRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Service Requests");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        rvRequests = findViewById(R.id.rvRequests);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        fabNewRequest = findViewById(R.id.fabNewRequest);

        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RequestsAdapter(requestList);
        rvRequests.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::fetchRequests);

        fabNewRequest.setOnClickListener(v -> {
            startActivity(new Intent(MyRequestsActivity.this, CreateRequestActivity.class));
        });

        fetchRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRequests(); // Auto refresh when coming back from creation
    }

    private void fetchRequests() {
        swipeRefresh.setRefreshing(true);
        int customerId = sessionManager.getCustomerId();

        ApiService apiService = ApiClient.getService();
        Call<MyRequestsResponse> call = apiService.getMyRequests(customerId);

        call.enqueue(new Callback<MyRequestsResponse>() {
            @Override
            public void onResponse(Call<MyRequestsResponse> call, Response<MyRequestsResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    MyRequestsResponse res = response.body();
                    if (res.isSuccess()) {
                        requestList.clear();
                        if (res.getRequests() != null) {
                            requestList.addAll(res.getRequests());
                        }
                        adapter.notifyDataSetChanged();
                        
                        if (requestList.isEmpty()) {
                            Toast.makeText(MyRequestsActivity.this, "No requests found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MyRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyRequestsActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyRequestsResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MyRequestsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
