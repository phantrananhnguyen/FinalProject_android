package com.example.finalproject_android.games;



import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.finalproject_android.R;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;


import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameMillionaireView extends LinearLayout {

    // UI Elements
    private TextView tvQuestion, tvScore, tvQuestionNumber, tvPlayerName;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD;
    private ProgressBar progressBarTime;

    // Game Variables
    private String correctAnswer;
    private int score = 0;
    private int currentQuestionNumber = 1;
    private Timer timer;
    private int timeLeft = 100;


    private SoundPool soundPool;
    private int wrong, right;
    private MediaPlayer backgroundMusic;

    // Listener for answer clicks
    private OnAnswerSelectedListener answerSelectedListener;

    public GameMillionaireView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Inflate layout
        inflate(getContext(), R.layout.activity_who_is_the_millionaire, this);

        // Find views
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        progressBarTime = findViewById(R.id.progressBarTime);

        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);

        // Handle button clicks
        btnOptionA.setOnClickListener(v -> handleAnswer(btnOptionA.getText().toString()));
        btnOptionB.setOnClickListener(v -> handleAnswer(btnOptionB.getText().toString()));
        btnOptionC.setOnClickListener(v -> handleAnswer(btnOptionC.getText().toString()));
        btnOptionD.setOnClickListener(v -> handleAnswer(btnOptionD.getText().toString()));




    }



    // Set question and answers
    public void setQuestion(String question, String answerA, String answerB, String answerC, String answerD, String correctAnswer) {
        this.correctAnswer = correctAnswer;

        tvQuestion.setText(question);
        btnOptionA.setText(answerA);
        btnOptionB.setText(answerB);
        btnOptionC.setText(answerC);
        btnOptionD.setText(answerD);

        tvQuestionNumber.setText("Câu " + currentQuestionNumber);

        startTimer();
    }



    // Handle answer selection
    private void handleAnswer(String selectedAnswer) {
        stopTimer();



        if (selectedAnswer.equals(correctAnswer)) {
            score += 10; // Increase score
            tvScore.setText("Điểm: " + score);
            if (answerSelectedListener != null) {
                answerSelectedListener.onCorrectAnswer();

            }
        } else {
            if (answerSelectedListener != null) {
                answerSelectedListener.onWrongAnswer();

            }
        }
    }

    // Timer logic
    private void startTimer() {
        timeLeft = 100;
        progressBarTime.setProgress(timeLeft);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                progressBarTime.setProgress(timeLeft);

                if (timeLeft <= 0) {
                    stopTimer();
                    if (answerSelectedListener != null) {
                        answerSelectedListener.onTimeOut();
                    }
                }
            }
        }, 0, 100);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // Listener Interface
    public void setOnAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
    }

    public interface OnAnswerSelectedListener {
        void onCorrectAnswer();
        void onWrongAnswer();
        void onTimeOut();
    }
}

