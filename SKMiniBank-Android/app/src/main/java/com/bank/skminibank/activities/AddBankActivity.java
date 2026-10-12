package com.bank.skminibank.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddBankActivity extends AppCompatActivity {

    private RecyclerView rvBanks;
    private TextInputEditText etSearch;
    private BankAdapter adapter;
    private List<BankModel> bankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvBanks = findViewById(R.id.rvBanks);
        etSearch = findViewById(R.id.etSearchBank);

        initBankList();
        
        adapter = new BankAdapter(bankList);
        rvBanks.setLayoutManager(new LinearLayoutManager(this));
        rvBanks.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBanks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initBankList() {
        bankList = new ArrayList<>();
        bankList.add(new BankModel("State Bank of India", R.drawable.sk_logo));
        bankList.add(new BankModel("HDFC Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("ICICI Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("Axis Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("Punjab National Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("Bank of Baroda", R.drawable.sk_logo));
        bankList.add(new BankModel("Canara Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("Union Bank of India", R.drawable.sk_logo));
        bankList.add(new BankModel("IDFC FIRST Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("IndusInd Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("Kotak Mahindra Bank", R.drawable.sk_logo));
        bankList.add(new BankModel("Yes Bank", R.drawable.sk_logo));
    }

    private void filterBanks(String text) {
        List<BankModel> filtered = new ArrayList<>();
        for (BankModel bank : bankList) {
            if (bank.name.toLowerCase().contains(text.toLowerCase())) {
                filtered.add(bank);
            }
        }
        adapter.updateList(filtered);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class BankModel {
        String name;
        int logo;
        BankModel(String name, int logo) {
            this.name = name;
            this.logo = logo;
        }
    }

    private class BankAdapter extends RecyclerView.Adapter<BankAdapter.ViewHolder> {
        private List<BankModel> list;

        BankAdapter(List<BankModel> list) {
            this.list = new ArrayList<>(list);
        }

        void updateList(List<BankModel> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_select, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BankModel bank = list.get(position);
            holder.tvName.setText(bank.name);
            holder.ivLogo.setImageResource(bank.logo);
            
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(AddBankActivity.this, "Linking " + bank.name + "...", Toast.LENGTH_SHORT).show();
                // Simulate linking success
                new android.os.Handler().postDelayed(() -> {
                    Toast.makeText(AddBankActivity.this, bank.name + " linked successfully!", Toast.LENGTH_LONG).show();
                    finish();
                }, 2000);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageView ivLogo;
            ViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvBankName);
                ivLogo = v.findViewById(R.id.ivBankLogo);
            }
        }
    }
}
