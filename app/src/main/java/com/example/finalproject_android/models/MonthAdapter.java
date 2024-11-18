package com.example.finalproject_android.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.R;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {
    private List<MonthItem<?>> monthList;  // List of month items with any type of data

    public MonthAdapter(List<MonthItem<?>> monthList) {
        this.monthList = monthList;
    }

    @Override
    public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Using context from parent to avoid NullPointerException
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pothole_month, parent, false);
        return new MonthViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MonthViewHolder holder, int position) {
        MonthItem<?> monthItem = monthList.get(position);
        holder.textViewMonth.setText(monthItem.getMonthName());

        // Retrieve the items associated with this month
        List<?> items = monthItem.getItems();

        if (items != null && !items.isEmpty()) {
            // Check and set adapter for Potholes if the list contains Pothole items
            if (items.get(0) instanceof Pothole) {
                PotholeAdapter potholeAdapter = new PotholeAdapter(holder.itemView.getContext(), (List<Pothole>) items);
                holder.recyclerViewPotholes.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));  // Ensure RecyclerView has a LayoutManager
                holder.recyclerViewPotholes.setAdapter(potholeAdapter);
            }
            // Check and set adapter for PointPlusItem if the list contains PointPlusItem
            else if (items.get(0) instanceof PointPlusItem) {
                PointPlusAdapter pointPlusAdapter = new PointPlusAdapter((List<PointPlusItem>) items);
                holder.recyclerViewPotholes.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));  // Ensure RecyclerView has a LayoutManager
                holder.recyclerViewPotholes.setAdapter(pointPlusAdapter);
            }
        } else {
            // If items list is empty, hide RecyclerView or display a placeholder
            holder.recyclerViewPotholes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    public class MonthViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMonth;
        public RecyclerView recyclerViewPotholes;

        public MonthViewHolder(View view) {
            super(view);
            textViewMonth = view.findViewById(R.id.textViewMonth);
            recyclerViewPotholes = view.findViewById(R.id.recyclerViewPotholes);  // Make sure ID matches the layout
        }
    }
}
