package com.example.finalproject_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import android.widget.ImageView;

public class GetStartedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        ImageView busGif = findViewById(R.id.busGif);

        // Load GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.bus_gif)
                .into(busGif);

        // Check if this is the user's first login
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", true);

        // Set loading time for the gif (5 seconds)
        int loadingTime = 5000;
        new Handler().postDelayed(() -> {
            Intent intent;
            if (isFirstLogin) {
                intent = new Intent(GetStartedActivity.this, SetupInformationActivity.class);
            } else {
                intent = new Intent(GetStartedActivity.this, BottomNavigation.class);
            }

            startActivity(intent);
            finish();
        }, loadingTime);
    }
}
