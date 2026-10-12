package com.bank.skminibank.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.bank.skminibank.activities.RequestDetailActivity;
import com.bank.skminibank.model.ServiceRequest;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private final List<ServiceRequest> list;

    public RequestsAdapter(List<ServiceRequest> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceRequest item = list.get(position);
        holder.tvType.setText(item.getRequestType());
        
        String status = item.getStatus();
        if (status == null) status = "PENDING";
        
        holder.tvStatus.setText(status.toUpperCase());
        holder.tvDate.setText(item.getRequestDate());
        holder.tvId.setText("ID: #" + item.getRequestId());
        
        // Modern Badge Styling
        if ("PENDING".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.badge_pending);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#E65100")); 
        } else if ("APPROVED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.badge_approved);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.badge_rejected);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#C62828"));
        }

        // Handle Click for Details
        String finalStatus = status;
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RequestDetailActivity.class);
            intent.putExtra("type", item.getRequestType());
            intent.putExtra("status", finalStatus);
            intent.putExtra("id", item.getRequestId());
            intent.putExtra("date", item.getRequestDate());
            intent.putExtra("acc", item.getAccountNumber());
            intent.putExtra("apprDate", item.getApprovalDate());
            intent.putExtra("expDate", item.getExpectedDeliveryDate());
            intent.putExtra("remarks", item.getRemarks());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvStatus, tvDate, tvId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvRequestType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvId = itemView.findViewById(R.id.tvRequestId);
        }
    }
}
