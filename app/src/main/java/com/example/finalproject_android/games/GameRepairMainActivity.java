package com.example.finalproject_android.games;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_android.R;

public class GameRepairMainActivity extends AppCompatActivity {
    private GameRepairView gameView;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Chế độ toàn màn hình
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Lấy kích thước màn hình
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Khởi tạo GameView
        gameView = new GameRepairView(this);
        setContentView(gameView);

        // Khởi tạo và phát nhạc nền
        backgroundMusic = MediaPlayer.create(this, R.raw.background_hero);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();

        backgroundMusic.setVolume(1.0f, 1.0f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusic.pause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundMusic.start();
        gameView.resume();
    }
}
