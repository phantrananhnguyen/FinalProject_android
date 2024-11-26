package com.example.finalproject_android.afterlogin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.R;
import com.example.finalproject_android.models.Feature;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Feature> features;
    public SearchAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<Feature> features, Context context) {
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        this.features=features;
    }
    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_infor, parent, false);
        return new SearchAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(features.get(position).getName());
        holder.tvLocation.setText(features.get(position).getLat()+","+features.get(position).getLon());
        holder.tvAmenity.setText(features.get(position).getAmenity());
    }

    @Override
    public int getItemCount() {
        Log.e("SearchAdapter", "getItemCount: " + features.size());
        return features.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvAmenity;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvLocation = itemView.findViewById(R.id.coordinates);
            tvAmenity = itemView.findViewById(R.id.amenity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(features.get(pos));
                        }
                    }
                }
            });
        }
    }
}
