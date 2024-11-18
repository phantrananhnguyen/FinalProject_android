package com.example.finalproject_android.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.R;

import java.util.List;
public class PotholeAdapter extends RecyclerView.Adapter<PotholeAdapter.PotholeViewHolder> {
    private List<Pothole> potholeList;
    private Context context;

    public PotholeAdapter(Context context, List<Pothole> potholeList) {
        this.context = context;
        this.potholeList = potholeList;
    }

    @Override
    public PotholeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.pothole_item, parent, false);
        return new PotholeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PotholeViewHolder holder, int position) {
        Pothole pothole = potholeList.get(position);
        holder.potholeLocation.setText(pothole.getLocation());
        holder.potholeCoordinates.setText("Coordinates: " + pothole.getCoordinates());
        holder.potholeId.setText("ID: " + pothole.getPotholeId());
        holder.potholeDate.setText(pothole.getDate());
        //holder.potholeType.setText(pothole.getType());
        holder.stateValue.setText(pothole.getState());

        switch(pothole.getType()) {
            case "Caution":
                holder.cautionCircle.setVisibility(View.VISIBLE);
                holder.warningCircle.setVisibility(View.GONE);
                holder.dangerCircle.setVisibility(View.GONE);
                break;
case "Warning":
                holder.cautionCircle.setVisibility(View.GONE);
                holder.warningCircle.setVisibility(View.VISIBLE);
                holder.dangerCircle.setVisibility(View.GONE);
                break;
            case "Danger":
                holder.cautionCircle.setVisibility(View.GONE);
                holder.warningCircle.setVisibility(View.GONE);
                holder.dangerCircle.setVisibility(View.VISIBLE);
                break;
            default:
                holder.cautionCircle.setVisibility(View.GONE);
                holder.warningCircle.setVisibility(View.GONE);
                holder.dangerCircle.setVisibility(View.GONE);
                break;
        }


        // Update State Color
        switch (pothole.getState()) {
            case "Accept":
                holder.stateValue.setBackgroundColor(context.getResources().getColor(R.color.safe_green));
                break;
            case "Pending":
                holder.stateValue.setBackgroundColor(context.getResources().getColor(R.color.warning_orange));
                break;
            case "Reject":
                holder.stateValue.setBackgroundColor(context.getResources().getColor(R.color.dangerous_red));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return potholeList.size();
    }

    public static class PotholeViewHolder extends RecyclerView.ViewHolder {
        TextView potholeLocation, potholeCoordinates, potholeId, potholeDate, potholeType, stateValue;
        View cautionCircle, warningCircle, dangerCircle;

        public PotholeViewHolder(@NonNull View itemView) {
            super(itemView);
            potholeLocation = itemView.findViewById(R.id.potholeLocation);
            potholeCoordinates = itemView.findViewById(R.id.potholeCoordinates);
            potholeId = itemView.findViewById(R.id.potholeId);
            potholeDate = itemView.findViewById(R.id.potholeDate);
            potholeType = itemView.findViewById(R.id.potholeType);
            stateValue = itemView.findViewById(R.id.stateValue);
            cautionCircle = itemView.findViewById(R.id.cautionCircle);
            warningCircle = itemView.findViewById(R.id.warningCircle);
            dangerCircle = itemView.findViewById(R.id.dangerCircle);
        }
    }
}
