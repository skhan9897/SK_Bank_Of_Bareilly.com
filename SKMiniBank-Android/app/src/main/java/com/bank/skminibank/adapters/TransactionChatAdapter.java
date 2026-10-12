package com.bank.skminibank.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.bank.skminibank.activities.TransactionDetailActivity;
import com.bank.skminibank.model.TransactionChatMessage;

import java.util.List;
import java.util.Locale;

public class TransactionChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TransactionChatMessage> messages;

    public TransactionChatAdapter(List<TransactionChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        TransactionChatMessage msg = messages.get(position);
        if (msg.isSentByMe()) {
            return msg.getType() == TransactionChatMessage.TYPE_TEXT ? 1 : 2;
        } else {
            return msg.getType() == TransactionChatMessage.TYPE_TEXT ? 3 : 4;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = 0;
        switch (viewType) {
            case 1: layout = R.layout.item_chat_sent; break;
            case 2: layout = R.layout.item_chat_payment_sent; break;
            case 3: layout = R.layout.item_chat_received; break;
            case 4: layout = R.layout.item_chat_payment_received; break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        if (viewType == 2 || viewType == 4) return new PaymentViewHolder(view);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionChatMessage msg = messages.get(position);
        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).tvText.setText(msg.getContent());
            ((TextViewHolder) holder).tvTime.setText(msg.getTimestamp());
        } else if (holder instanceof PaymentViewHolder) {
            ((PaymentViewHolder) holder).tvAmount.setText(String.format(Locale.getDefault(), "₹ %.2f", msg.getAmount()));
            
            // Set the Party Name (To/From)
            String prefix = msg.isSentByMe() ? "To: " : "From: ";
            String party = msg.getOtherPartyName() != null ? msg.getOtherPartyName() : "User";
            ((PaymentViewHolder) holder).tvStatus.setText(prefix + party);

            String status = msg.getStatus();
            if ("PROCESSING".equalsIgnoreCase(status)) {
                ((PaymentViewHolder) holder).tvStatus.setText("Processing...");
                ((PaymentViewHolder) holder).tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9100"));
            } else if ("SUCCESS".equalsIgnoreCase(status) || "PAID".equalsIgnoreCase(status)) {
                ((PaymentViewHolder) holder).tvStatus.setText("PAID");
                ((PaymentViewHolder) holder).tvStatus.setTextColor(android.graphics.Color.parseColor("#00C853"));
            } else {
                ((PaymentViewHolder) holder).tvStatus.setText(status != null ? status : "FAILED");
                ((PaymentViewHolder) holder).tvStatus.setTextColor(android.graphics.Color.RED);
            }
            ((PaymentViewHolder) holder).tvTime.setText(msg.getTimestamp());

            // Add Click Listener to open Detail Receipt
            holder.itemView.setOnClickListener(v -> {
                if (msg.getAmount() > 0) {
                    Intent intent = new Intent(v.getContext(), TransactionDetailActivity.class);
                    intent.putExtra("amount", String.format(Locale.getDefault(), "₹ %.2f", msg.getAmount()));
                    intent.putExtra("type", msg.isSentByMe() ? "DEBIT" : "CREDIT");
                    intent.putExtra("desc", msg.isSentByMe() ? "Payment Sent" : "Payment Received");
                    intent.putExtra("txnId", msg.getTransactionId() != null ? msg.getTransactionId() : "SK" + System.currentTimeMillis());
                    intent.putExtra("date", msg.getTimestamp());
                    intent.putExtra("postBalance", 0.0); 
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;
        TextViewHolder(View v) {
            super(v);
            tvText = v.findViewById(R.id.tvChatText);
            tvTime = v.findViewById(R.id.tvChatTime);
        }
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvStatus, tvTime;
        PaymentViewHolder(View v) {
            super(v);
            tvAmount = v.findViewById(R.id.tvPayAmount);
            tvStatus = v.findViewById(R.id.tvPayStatus);
            tvTime = v.findViewById(R.id.tvPayTime);
        }
    }
}
