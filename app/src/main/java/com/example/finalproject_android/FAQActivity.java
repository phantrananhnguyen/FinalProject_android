package com.example.finalproject_android;



import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FAQActivity extends AppCompatActivity {
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        back = findViewById(R.id.report_back_btn);


        setupToggle(findViewById(R.id.question1), findViewById(R.id.answer1_text));
        setupToggle(findViewById(R.id.question2), findViewById(R.id.answer2_text));
        setupToggle(findViewById(R.id.question3), findViewById(R.id.answer3_text));
        setupToggle(findViewById(R.id.question4), findViewById(R.id.answer4_text));
        setupToggle(findViewById(R.id.question5), findViewById(R.id.answer5_text));
        setupToggle(findViewById(R.id.question6), findViewById(R.id.answer6_text));
        setupToggle(findViewById(R.id.question7), findViewById(R.id.answer7_text));
        setupToggle(findViewById(R.id.question8), findViewById(R.id.answer8_text));
        setupToggle(findViewById(R.id.question9), findViewById(R.id.answer9_text));
        setupToggle(findViewById(R.id.question10), findViewById(R.id.answer10_text));

        back.setOnClickListener(v -> {
            finish();
        });

    }

    private void setupToggle(LinearLayout questionLayout, TextView answerText) {
        questionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerText.getVisibility() == View.GONE) {
                    answerText.setVisibility(View.VISIBLE);
                } else {
                    answerText.setVisibility(View.GONE);
                }
            }
        });
    }
}
