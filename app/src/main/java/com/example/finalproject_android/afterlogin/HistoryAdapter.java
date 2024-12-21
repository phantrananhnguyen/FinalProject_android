package com.example.finalproject_android.afterlogin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.R;
import com.example.finalproject_android.models.HistoryItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryItem> historyList;
    private List<HistoryItem> filteredList;
    private Context context;

    public HistoryAdapter(List<HistoryItem> historyList, Context context) {
        this.historyList = historyList;
        this.filteredList = new ArrayList<>(historyList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = filteredList.get(position);
        holder.tvLocation.setText(item.getLatitude()+", " + item.getLongitude());
        holder.tvType.setText(item.getType());
        holder.tvCreatedAt.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    public void filter(String type) {
        filteredList.clear();
        if (type.equalsIgnoreCase("All")) {
            filteredList.addAll(historyList); // Hiển thị tất cả
        } else {
            for (HistoryItem item : historyList) {
                if (item.getType().equalsIgnoreCase(type)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Cập nhật giao diện
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation, tvType, tvCreatedAt;
        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.coor);
            tvType = itemView.findViewById(R.id.h_type);
            tvCreatedAt = itemView.findViewById(R.id.h_date);
        }
    }
}
