package com.bank.skminibank.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.bank.skminibank.activities.TransactionDetailActivity;
import com.bank.skminibank.model.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private List<Transaction> list;

    public TransactionsAdapter(List<Transaction> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction item = list.get(position);
        
        String desc = item.getDescription();
        if (desc == null || desc.isEmpty() || desc.equalsIgnoreCase("null")) {
            desc = "Transaction ID: " + item.getTransactionId();
            if (item.getTransactionId() == null || item.getTransactionId().equalsIgnoreCase("null")) {
                desc = "Bank Transaction";
            }
        }
        holder.tvDesc.setText(desc);
        
        String date = item.getDate();
        if (date == null || date.isEmpty() || date.equalsIgnoreCase("null")) {
            date = "N/A";
        }
        holder.tvDate.setText(date);
        
        String amountText;
        String type = item.getType() != null ? item.getType().toUpperCase() : "DEBIT";
        
        if (type.contains("CREDIT") || type.contains("DEPOSIT") || type.contains("LOAN")) {
            amountText = String.format(Locale.getDefault(), "+ ₹ %.2f", item.getAmount());
            holder.tvAmount.setText(amountText);
            holder.tvAmount.setTextColor(Color.parseColor("#2E7D32")); // Green
            holder.ivIcon.setImageResource(android.R.drawable.ic_input_add);
            holder.ivIcon.setColorFilter(Color.parseColor("#2E7D32"));
        } else {
            amountText = String.format(Locale.getDefault(), "- ₹ %.2f", item.getAmount());
            holder.tvAmount.setText(amountText);
            holder.tvAmount.setTextColor(Color.parseColor("#C62828")); // Red
            holder.ivIcon.setImageResource(android.R.drawable.ic_menu_send);
            holder.ivIcon.setColorFilter(Color.parseColor("#004C8F"));
        }

        if (item.getBalanceAfter() >= 0) {
            holder.tvBalanceAfter.setVisibility(View.VISIBLE);
            holder.tvBalanceAfter.setText(String.format(Locale.getDefault(), "Bal: ₹ %.2f", item.getBalanceAfter()));
        } else {
            holder.tvBalanceAfter.setVisibility(View.GONE);
        }

        // Handle Click to open Detailed Receipt
        String finalDesc = desc;
        String finalDate = date;
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TransactionDetailActivity.class);
            intent.putExtra("amount", amountText);
            intent.putExtra("type", item.getType());
            intent.putExtra("desc", finalDesc);
            intent.putExtra("txnId", item.getTransactionId());
            intent.putExtra("date", finalDate);
            intent.putExtra("postBalance", item.getBalanceAfter());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc, tvDate, tvAmount, tvBalanceAfter;
        ImageView ivIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBalanceAfter = itemView.findViewById(R.id.tvClosingBalance);
            ivIcon = itemView.findViewById(R.id.ivTypeIcon);
        }
    }
}
