package com.example.finalproject_android.models;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.R;

import java.util.List;

public class PotholeReportAdapter extends RecyclerView.Adapter<PotholeReportAdapter.PotholeViewHolder> {

    private final List<Potholemodel> potholeList;
    private final OnPotholeClickListener listener;

    public interface OnPotholeClickListener {
        void onPotholeClick(Potholemodel pothole);
    }

    public PotholeReportAdapter(List<Potholemodel> potholeList, OnPotholeClickListener listener) {
        this.potholeList = potholeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PotholeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pothole_report, parent, false);
        return new PotholeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PotholeViewHolder holder, int position) {
        Potholemodel pothole = potholeList.get(position);
        holder.author.setText(pothole.getAuthor());
        holder.date.setText("" + pothole.getDate());
        holder.type.setText("" + pothole.getType());
        holder.state.setText("" + pothole.getState());

        holder.itemView.setOnClickListener(v -> listener.onPotholeClick(pothole));
    }

    @Override
    public int getItemCount() {
        return potholeList.size();
    }

    public static class PotholeViewHolder extends RecyclerView.ViewHolder {
        TextView author, date, type, state;

        public PotholeViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
            type = itemView.findViewById(R.id.type);
            state = itemView.findViewById(R.id.state);
        }
    }
}
