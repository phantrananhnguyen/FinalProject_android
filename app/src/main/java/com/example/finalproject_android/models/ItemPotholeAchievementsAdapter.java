package com.example.finalproject_android.models;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
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

public class ItemPotholeAchievementsAdapter extends RecyclerView.Adapter<ItemPotholeAchievementsAdapter.AchievementViewHolder> {
    private AchievementViewHolder.OnAchievementClickListener clickListener;
    private Context context;

    private List<Achievement> achievements;

    public ItemPotholeAchievementsAdapter(Context context, List<Achievement> achievements) {
        this.achievements = achievements;
        this.context = context;
    }

    // Setter for click listener
    public void setOnAchievementClickListener(AchievementViewHolder.OnAchievementClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the individual item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pothole_achievements, parent, false);
        return new AchievementViewHolder(view); // Return the new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        // Bind data to the ViewHolder (set the text, colors, and icon for the achievement)
        Achievement achievement = achievements.get(position);

        // Get the background drawable from the resource
        GradientDrawable background = (GradientDrawable) holder.itemView.getBackground();

        // Set the background color dynamically
        background.setColor(achievement.getBackgroundColor()); // Set the color from the achievement

        // Set icon (image resource)
        holder.achievementsImage.setImageResource(
                context.getResources().getIdentifier(achievement.getIcon(), "drawable", context.getPackageName())
        );


        // Set the title text
        holder.titleText.setText(achievement.getName());  // Assuming the name is the title
        holder.titleText.setTextColor(achievement.getTitleTextColor());

        // Set the subtitle text (You may want to use another getter for subtitle text)
        holder.subtitleText.setText(achievement.getSubtitleText());  // Example subtitle, update as needed
        holder.subtitleText.setTextColor(achievement.getSubtitleTextColor());

        // Set the quote text (You may want to use another getter for quote text)
        holder.quoteText.setText(achievement.getQuoteText());  // Example quote, update as needed
        holder.quoteText.setTextColor(achievement.getQuoteTextColor());

        // Add click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAchievementClick(achievement);
            }
        });
    }

    @Override
    public int getItemCount() {
        return achievements.size(); // Return the size of the dataset
    }

    // ViewHolder class to hold the views for each item
    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView achievementsImage;
        TextView titleText;
        TextView subtitleText;
        TextView quoteText;

        public AchievementViewHolder(View itemView) {
            super(itemView);
            achievementsImage = itemView.findViewById(R.id.achievementsImage);
            titleText = itemView.findViewById(R.id.titleText);
            subtitleText = itemView.findViewById(R.id.subtitleText);
            quoteText = itemView.findViewById(R.id.quoteText);
        }

        // Interface for click listener
        public interface OnAchievementClickListener {
            void onAchievementClick(Achievement achievement);
        }
    }
}
