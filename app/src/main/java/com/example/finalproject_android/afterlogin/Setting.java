package com.example.finalproject_android.afterlogin;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject_android.R;

public class Setting extends AppCompatActivity {
    LinearLayout edtProfile, notification, sercurity, help, more, language, change;
    CardView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        back =findViewById(R.id.setting_back);
        edtProfile = findViewById(R.id.edt_profile);
        edtProfile.setOnClickListener(v -> {

        });
        back.setOnClickListener(v -> {
            finish();
        });
    }
}