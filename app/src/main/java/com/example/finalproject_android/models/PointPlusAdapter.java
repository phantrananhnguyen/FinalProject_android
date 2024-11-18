package com.example.finalproject_android.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.R;

import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PointPlusAdapter extends RecyclerView.Adapter<PointPlusAdapter.ViewHolder> {

    private final List<PointPlusItem> pointPlusItems;

    public PointPlusAdapter(List<PointPlusItem> pointPlusItems) {
        this.pointPlusItems = pointPlusItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_plus_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PointPlusItem item = pointPlusItems.get(position);

        // Gán dữ liệu văn bản
        holder.pointText.setText(getPointText(item.getPoint()));
        holder.potholeDate.setText(item.getPotholeDate());
        holder.pointValue.setText(String.valueOf(item.getPoint()));
        holder.potholeId.setText("ID: " + item.getPotholeId());

        // Cập nhật ảnh và màu sắc dựa trên loại cảnh báo
        int point = item.getPoint();
        if (point <= 50) {
            holder.starImage.setImageResource(R.drawable.one_star);
            holder.pointText.setText("Quite good!");
            holder.cautionCircle.setVisibility(View.VISIBLE);
            holder.warningCircle.setVisibility(View.GONE);
            holder.dangerCircle.setVisibility(View.GONE);

        } else if (point <= 100) {
            holder.starImage.setImageResource(R.drawable.two_stars);
            holder.pointText.setText("Very good!");
            holder.cautionCircle.setVisibility(View.GONE);
            holder.warningCircle.setVisibility(View.VISIBLE);
            holder.dangerCircle.setVisibility(View.GONE);
        } else {
            holder.starImage.setImageResource(R.drawable.three_stars);
            holder.pointText.setText("Excellent!");
            holder.cautionCircle.setVisibility(View.GONE);
            holder.warningCircle.setVisibility(View.GONE);
            holder.dangerCircle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return pointPlusItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView starImage;
        TextView pointText, potholeDate, pointValue, potholeId;
        View cautionCircle, warningCircle, dangerCircle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            starImage = itemView.findViewById(R.id.starImage);
            pointText = itemView.findViewById(R.id.pointText);
            potholeDate = itemView.findViewById(R.id.potholeDate);
            cautionCircle = itemView.findViewById(R.id.cautionCircle);
            warningCircle = itemView.findViewById(R.id.warningCircle);
            dangerCircle = itemView.findViewById(R.id.dangerCircle);
            pointValue = itemView.findViewById(R.id.pointValue);
            potholeId = itemView.findViewById(R.id.potholeId);
        }
    }

    private String getPointText(int point) {
        if (point <= 50) {
            return "Quite good!";
        } else if (point <= 100) {
            return "Very good!";
        } else {
            return "Excellent!";
        }
    }
}
