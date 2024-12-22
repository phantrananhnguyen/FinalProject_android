package com.example.finalproject_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button signin, signup;
    Spinner languageSpinner;
    ImageView flagImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lấy ngôn ngữ đã lưu trong SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String languageCode = prefs.getString("Language", "en"); // Mặc định là tiếng Anh

        // Áp dụng ngôn ngữ đã lưu
        setLocale(languageCode);

        signin = findViewById(R.id.signInButton);
        signup = findViewById(R.id.signUpButton);
        languageSpinner = findViewById(R.id.language_spinner);
        flagImageView = findViewById(R.id.flagImageView);

        // Thiết lập các tùy chọn ngôn ngữ cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        String savedLanguage = prefs.getString("Language", "en");
        if (savedLanguage.equals("en")) {
            languageSpinner.setSelection(0); // English
            flagImageView.setImageResource(R.drawable.flag_uk); // Hiển thị cờ của Anh
        } else if (savedLanguage.equals("ko")) {
            languageSpinner.setSelection(1); // Korean
            flagImageView.setImageResource(R.drawable.flag_kor); // Hiển thị cờ của Hàn Quốc
        }

        // Đặt giá trị Spinner dựa trên ngôn ngữ hiện tại
        if (languageCode.equals("en")) {
            languageSpinner.setSelection(0); // Tiếng Anh
        } else if (languageCode.equals("ko")) {
            languageSpinner.setSelection(1); // Tiếng Hàn
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    setLocale("en"); // Tiếng Anh
                } else if (position == 1) {
                    setLocale("ko"); // Tiếng Hàn
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        signin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
        });

        signup.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });
    }

    // Phương thức thay đổi ngôn ngữ
    private void setLocale(String languageCode) {
        // Lấy ngôn ngữ hiện tại từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String currentLanguage = prefs.getString("Language", "en");

        // Nếu ngôn ngữ hiện tại khác với ngôn ngữ muốn thay đổi thì mới thực hiện
        if (!currentLanguage.equals(languageCode)) {
            // Lưu ngôn ngữ vào SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putString("Language", languageCode);
            editor.apply();

            // Thay đổi ngôn ngữ
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            // Áp dụng ngôn ngữ mới cho ứng dụng
            Context context = getBaseContext();
            Resources resources = context.getResources();
            Configuration newConfig = resources.getConfiguration();
            newConfig.setLocale(locale);
            context.createConfigurationContext(newConfig);

            // Khởi động lại Activity để áp dụng thay đổi ngôn ngữ
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
