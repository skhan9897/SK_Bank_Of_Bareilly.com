package com.bank.skminibank.adapters;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bank.skminibank.R;
import com.bank.skminibank.model.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bubble, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.tv.setText(msg.getText());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.card.getLayoutParams();
        if (msg.isUser()) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
            holder.card.setCardBackgroundColor(Color.parseColor("#B71C1C"));
            holder.tv.setTextColor(Color.WHITE);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            holder.card.setCardBackgroundColor(Color.WHITE);
            holder.tv.setTextColor(Color.BLACK);
        }
        holder.card.setLayoutParams(params);
    }

    @Override
    public int getItemCount() { return messages.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        CardView card;
        public ViewHolder(@NonNull View v) {
            super(v);
            tv = v.findViewById(R.id.tvChatMessage);
            card = v.findViewById(R.id.cardChat);
        }
    }
}
