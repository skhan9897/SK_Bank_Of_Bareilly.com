package com.bank.skminibank.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bank.skminibank.R;
import com.bank.skminibank.model.NotificationResponse.NotificationItem;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationItem> list;

    public NotificationAdapter(List<NotificationItem> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());
        holder.tvDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvDate;
        public ViewHolder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvNotifTitle);
            tvMessage = v.findViewById(R.id.tvNotifMessage);
            tvDate = v.findViewById(R.id.tvNotifDate);
        }
    }
}
