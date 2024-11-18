package com.example.finalproject_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import com.example.finalproject_android.models.ItemPotholeAchievementsAdapter;
import com.example.finalproject_android.models.Achievement;
import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView achievementsRecyclerView;
    private ItemPotholeAchievementsAdapter adapter;
    private List<Achievement> achievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        achievementsRecyclerView = findViewById(R.id.achievementsRecyclerView);

        // Create list of sample achievements
        achievements = new ArrayList<>();

        achievements.add(new Achievement(
                "Pothole Picasso",
                5,
                "road_warrior",
                ContextCompat.getColor(this, R.color.light_blue),
                ContextCompat.getColor(this, R.color.dark_blue),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.gray),
                "Pothole Picasso",
                "Upload 5 creatively angled photos of potholes.",
                "You don’t just see potholes, you see art in them."
        ));

        achievements.add(new Achievement(
                "Speed Demon",
                10,
                "road_warrior",
                ContextCompat.getColor(this, R.color.safe_green),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.light_gray),
                "Speed Demon",
                "Complete 10 pothole detection runs under 30 minutes.",
                "Speed is nothing without control."
        ));

        achievements.add(new Achievement(
                "Explorer",
                20,
                "road_warrior",
                ContextCompat.getColor(this, R.color.bar_end_color),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.dark_gray),
                "Explorer",
                "Discover 20 unique potholes across different locations.",
                "Every journey has its challenges."
        ));

        achievements.add(new Achievement(
                "The Perfectionist",
                30,
                "road_warrior",
                ContextCompat.getColor(this, R.color.secondary),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.primary),
                "The Perfectionist",
                "Upload 30 high-quality photos of potholes with perfect angles.",
                "There is no substitute for perfection."
        ));

        achievements.add(new Achievement(
                "Pothole Hunter",
                50,
                "road_warrior",
                ContextCompat.getColor(this, R.color.dangerous_red),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.accent),
                "Pothole Hunter",
                "Detect 50 potholes in total.",
                "A hunter never stops until the job is done."
        ));

        achievements.add(new Achievement(
                "Master Mapper",
                100,
                "road_warrior",
                ContextCompat.getColor(this, R.color.light_gray),
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.light_blue),
                "Master Mapper",
                "Map 100 potholes in various regions.",
                "Mapping is the key to progress."
        ));

        // Set the adapter
        adapter = new ItemPotholeAchievementsAdapter(achievements);
        achievementsRecyclerView.setAdapter(adapter);
        achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
