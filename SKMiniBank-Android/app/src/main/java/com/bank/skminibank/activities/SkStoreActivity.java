package com.bank.skminibank.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bank.skminibank.R;
import com.bank.skminibank.api.ApiClient;
import com.bank.skminibank.model.StoreItem;
import com.bank.skminibank.model.StoreResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SkStoreActivity extends AppCompatActivity {

    private RecyclerView rvStores;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StoreAdapter adapter;
    private List<StoreItem> storeItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sk_store);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvStores = findViewById(R.id.rvStores);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshStore);

        rvStores.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new StoreAdapter(storeItems);
        rvStores.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchStores);

        fetchStores();
    }

    private void fetchStores() {
        swipeRefreshLayout.setRefreshing(true);
        ApiClient.getService().getStores().enqueue(new Callback<StoreResponse>() {
            @Override
            public void onResponse(@NonNull Call<StoreResponse> call, @NonNull Response<StoreResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().getStores() != null && !response.body().getStores().isEmpty()) {
                    storeItems.clear();
                    storeItems.addAll(response.body().getStores());
                    adapter.notifyDataSetChanged();
                } else {
                    // Agar server se data nahi aaya, toh mock data dikhao
                    showMockData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreResponse> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                // Network fail hone par bhi mock data dikhao
                showMockData();
            }
        });
    }

    private void showMockData() {
        storeItems.clear();
        storeItems.add(new StoreItem(1, "Premium Debit Card", "Exclusive benefits and higher limits", 499.0, "https://example.com/card1.png", "Cards"));
        storeItems.add(new StoreItem(2, "Personal Loan Pro", "Low interest rates for loyal customers", 0.0, "https://example.com/loan.png", "Loans"));
        storeItems.add(new StoreItem(3, "Insurance Shield", "Complete family health protection", 1999.0, "https://example.com/ins.png", "Insurance"));
        storeItems.add(new StoreItem(4, "Gold Investment", "Start investing in digital gold", 500.0, "https://example.com/gold.png", "Investment"));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
        private List<StoreItem> items;

        public StoreAdapter(List<StoreItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
            return new StoreViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
            StoreItem item = items.get(position);
            holder.tvName.setText(item.getName());
            holder.tvDescription.setText(item.getDescription());
            holder.tvPrice.setText(String.format(Locale.getDefault(), "₹ %.2f", item.getPrice()));

            Glide.with(SkStoreActivity.this)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.sk_logo)
                    .into(holder.ivImage);

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(SkStoreActivity.this, "Coming Soon: " + item.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class StoreViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvDescription, tvPrice;

            public StoreViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.ivStoreImage);
                tvName = itemView.findViewById(R.id.tvStoreName);
                tvDescription = itemView.findViewById(R.id.tvStoreDescription);
                tvPrice = itemView.findViewById(R.id.tvStorePrice);
            }
        }
    }
}
