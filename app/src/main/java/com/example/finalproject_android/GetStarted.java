package com.example.finalproject_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class GetStarted extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        ImageView busGif = findViewById(R.id.busGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.bus_gif)
                .into(busGif);
        int loadingTime = 5000;
        new Handler().postDelayed(() -> {
            Intent intent;
            intent = new Intent(GetStarted.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, loadingTime);
    }
}