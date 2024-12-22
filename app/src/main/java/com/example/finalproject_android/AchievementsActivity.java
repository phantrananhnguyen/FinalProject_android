package com.example.finalproject_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.finalproject_android.games.GameMillionaireActivity;
import com.example.finalproject_android.games.GameRepairMainActivity;
import com.example.finalproject_android.games.GameRunnerActivity;
import com.example.finalproject_android.models.ItemPotholeAchievementsAdapter;
import com.example.finalproject_android.models.Achievement;
import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView achievementsRecyclerView;
    private ItemPotholeAchievementsAdapter adapter;
    private List<Achievement> achievements;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        achievementsRecyclerView = findViewById(R.id.achievementsRecyclerView);
        backButton = findViewById(R.id.report_back_btn);

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // Create list of sample achievements
        achievements = new ArrayList<>();
        achievements.add(new Achievement(
                "Road Runner: Pothole Escape",
                5,
                "runner_icon",
                Color.parseColor("#FFF9E6"),
                Color.parseColor("#002D62"),
                Color.parseColor("#6E6E6E"),
                Color.parseColor("#6E6E6E"),
                "Road Runner: Pothole Escape",
                "Take control off a tiny car racing on a challenging road filled with potholes!",
                "Jump to avoid obstacles, test your reflexes, and see how far you can go. Simple to play, hard to master - Can you conquer the road?"
        ));
        achievements.add(new Achievement(
                "Pothole Repair Hero",
                10,
                "hero_icon",
                Color.parseColor("#88C999"),
                ContextCompat.getColor(this, R.color.white),
                Color.parseColor("#212121"),
                Color.parseColor("#212121"),
                "Pothole Repair Hero",
                "Become a Pothole Repair Hero! Fix roads, earn rewards, and keep the city moving in this fast-paced repair game.",
                "The city needs you! Step into the role of a Pothole Repair Hero and save the streets from damage."
        ));
        achievements.add(new Achievement(
                "Who Wants to Be a Millionaire:\nPothole Edition",
                20,
                "millionaire_icon",
                Color.parseColor("#1A237E"),  // Nền: xanh tím đậm (giống màu logo của Millionaire)
                Color.parseColor("#FFD700"), // Tiêu đề: vàng óng ánh
                Color.parseColor("#FFFFFF"), // Phụ đề: trắng
                Color.parseColor("#FFFFF8"),
                "Who Wants to Be a Millionaire:\nPothole Edition",
                "How far can your knowledge take you? Puts your trivia skills to the test. Answer correctly to win big!",
                "Test your knowledge and climb the money ladder in Who Wants to Be a Millionaire? Answer increasingly difficult questions to win the top prize!"
        ));

        // Set the adapter
        adapter = new ItemPotholeAchievementsAdapter(this, achievements);
        achievementsRecyclerView.setAdapter(adapter);
        achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Handle item click
        adapter.setOnAchievementClickListener(achievement -> {
            Intent intent;
            switch (achievement.getTitleText()) {
                case "Road Runner: Pothole Escape":
                    intent = new Intent(AchievementsActivity.this, GameRunnerActivity.class);
                    break;
                case "Pothole Repair Hero":
                    intent = new Intent(AchievementsActivity.this, GameRepairMainActivity.class);
                    break;
                case "Who Wants to Be a Millionaire:\nPothole Edition":
                    intent = new Intent(AchievementsActivity.this, GameMillionaireActivity.class);
                    break;
                default:
                    Toast.makeText(AchievementsActivity.this, "Game not available", Toast.LENGTH_SHORT).show();
                    return;
            }
            startActivity(intent);
        });
    }
}
