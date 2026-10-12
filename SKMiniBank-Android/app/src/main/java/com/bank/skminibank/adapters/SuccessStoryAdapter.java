package com.bank.skminibank.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bank.skminibank.R;
import com.bank.skminibank.model.SuccessStory;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class SuccessStoryAdapter extends RecyclerView.Adapter<SuccessStoryAdapter.ViewHolder> {

    private List<SuccessStory> list;

    public SuccessStoryAdapter(List<SuccessStory> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_success_story, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuccessStory item = list.get(position);
        holder.tvName.setText(item.getName());
        holder.tvRole.setText(item.getRole());
        holder.tvPackage.setText(String.format(Locale.getDefault(), "%.2f LPA", item.getCtc()));

        if (item.getPhotoUrl() != null && !item.getPhotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPhotoUrl())
                    .placeholder(R.drawable.sk_logo)
                    .circleCrop()
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.sk_logo);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvName, tvRole, tvPackage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivStudentPhoto);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvPackage = itemView.findViewById(R.id.tvPackage);
        }
    }
}
