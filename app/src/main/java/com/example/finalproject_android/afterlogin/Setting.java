package com.example.finalproject_android.afterlogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject_android.FAQActivity;
import com.example.finalproject_android.R;

public class Setting extends AppCompatActivity {
    LinearLayout edtProfile, notification, sercurity, qna, more, language, change;
    TextView help;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        back =findViewById(R.id.setting_back);
        qna = findViewById(R.id.qna);
        edtProfile = findViewById(R.id.edt_profile);
        edtProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Setting.this, EditProfile.class);
            startActivity(intent);
        });
        back.setOnClickListener(v -> {
            finish();
        });

        qna.setOnClickListener(v -> {
            Intent intent = new Intent(Setting.this, FAQActivity.class);
            startActivity(intent);
        });

        }
    }