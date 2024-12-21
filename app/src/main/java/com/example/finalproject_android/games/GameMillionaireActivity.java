package com.example.finalproject_android.games;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalproject_android.R;

import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameMillionaireActivity extends AppCompatActivity {

    private TextView tvPlayerName, tvQuestionNumber, tvQuestion, tvScore;
    private Button btnFiftyFifty, btnCallFriend;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD;
    private ProgressBar progressBarTime;
    private boolean isFiftyFiftyUsed = false;
    private boolean isCallFriendUsed = false;

    private SoundPool soundPool;
    private int wrong, right;
    private MediaPlayer backgroundMusic;

    private int questionNumber = 1;
    private int score = 0;
    private int highScore = 0;
    private int timeLeft = 100; // Thời gian ban đầu cho progress bar (giả lập)
    private boolean gameOver = false;

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_is_the_millionaire);

        // Ánh xạ các nút và thành phần giao diện
        btnFiftyFifty = findViewById(R.id.btnFiftyFifty);
        btnCallFriend = findViewById(R.id.btnCallFriend);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        progressBarTime = findViewById(R.id.progressBarTime);
        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);

        // Gắn sự kiện cho các nút trợ giúp
        btnCallFriend.setOnClickListener(view -> useCallFriend());
        btnFiftyFifty.setOnClickListener(view -> useFiftyFifty(currentQuestion.correctAnswer));

        // Lấy điểm cao nhất từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);

        // Hiển thị thông tin ban đầu
        tvPlayerName.setText("Người chơi: Player 1");
        tvScore.setText("Điểm: 0 | Kỷ lục: " + highScore);

        // Đọc file câu hỏi
        loadQuestionsFromFile();

        // Hiển thị câu hỏi đầu tiên
        loadQuestion();

        // Sự kiện cho các nút lựa chọn đáp án
        View.OnClickListener answerClickListener = view -> {
            Button selectedButton = (Button) view;
            checkAnswer(selectedButton);  // Gọi phương thức kiểm tra đáp án khi nhấn nút
        };

        // Ánh xạ sự kiện cho các nút đáp án
        btnOptionA.setOnClickListener(answerClickListener);
        btnOptionB.setOnClickListener(answerClickListener);
        btnOptionC.setOnClickListener(answerClickListener);
        btnOptionD.setOnClickListener(answerClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }



// Load các âm thanh từ res/raw
        wrong = soundPool.load(this, R.raw.wrong, 1);
        right = soundPool.load(this, R.raw.right, 1);

        // Đăng ký listener
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) { // 0 là SUCCESS
                // Phát âm thanh khi đã sẵn sàng
                soundPool.play(sampleId, 1, 1, 0, 0, 1);
            } else {
                // Báo lỗi nếu tải âm thanh thất bại
                Log.e("SoundPool", "Failed to load sound.");
            }
        });

        backgroundMusic = MediaPlayer.create(this, R.raw.background_millionare);
        backgroundMusic.setLooping(true); // Lặp nhạc
        backgroundMusic.start(); // Phát nhạc

// Tăng âm lượng tối đa
        backgroundMusic.setVolume(1.0f, 1.0f);

        Log.e("MediaPlayer", "MediaPlayer initialization đang chạy.");

        if (backgroundMusic != null) {
            backgroundMusic.setOnPreparedListener(mp -> mp.start());
            backgroundMusic.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "Error occurred: " + what + ", " + extra);
                return true;
            });
        } else {
            Log.e("MediaPlayer", "MediaPlayer initialization failed.");
        }


    }

    private void gameOver() {

            // Tính toán điểm (nếu lớn hơn 1000, chia cho 10)
            int finalScore = (score > 1000) ? (int) Math.round((double) score / 10) : 0;

            if (finalScore > 0) {

                // Gửi điểm lên backend để cập nhật
                sendScoreToBackend(finalScore);
            }
        }


    private void sendScoreToBackend(int finalScore) {


        // Khởi tạo ApiService
        ApiService apiService = ApiClient.getApiServiceWithToken(this);

        // Gửi yêu cầu cập nhật điểm lên backend
        Call<ResponseBody> call = apiService.updateScore(finalScore);

        // Thực hiện yêu cầu bất đồng bộ
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Cập nhật thành công
                    Log.d("GameOver", "Điểm đã được cập nhật thành công");
                } else {
                    // Xử lý khi response không thành công
                    Log.d("GameOver", "Cập nhật điểm thất bại");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Xử lý khi request bị lỗi
                Log.e("GameOver", "Request thất bại", t);
            }
        });
    }

    private void checkAnswer(Button selectedButton) {
        boolean isCorrect = false;

        if (selectedButton != null) {
            String selectedText = selectedButton.getText().toString();
            String correctAnswer = currentQuestion.correctAnswer;





            if (selectedText.trim().substring(0, 1).equalsIgnoreCase(correctAnswer)) {


                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                score += 10;
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();
                }
                soundPool.play(right, 1, 1, 0, 0, 1);
                backgroundMusic.start();
            } else {
                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();
                }
                soundPool.play(wrong, 1, 1, 0, 0, 1);
                backgroundMusic.start();
            }
        }

        // Cập nhật điểm số
        tvScore.setText("Điểm: " + score + " | Kỷ lục: " + highScore);

        // Cập nhật điểm cao nhất nếu cần
        if (score > highScore) {
            highScore = score;
            SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", highScore);
            editor.apply();
        }

        // Tự động chuyển sang câu hỏi tiếp theo hoặc kết thúc game
        new Handler().postDelayed(() -> {
            resetButtonColors();  // Đặt lại màu nút khi chuyển câu hỏi
            currentQuestionIndex++;  // Tăng chỉ số câu hỏi
            if (currentQuestionIndex < questionList.size()) {  // Kiểm tra nếu có câu hỏi tiếp theo
                loadQuestion();  // Tải câu hỏi mới
            } else {
                endGame();  // Kết thúc game nếu hết câu hỏi
            }
        }, 1000);

        // Dừng timer khi có lựa chọn
        if (timer != null) {
            timer.cancel();
        }

        // Khởi tạo lại timer cho câu hỏi tiếp theo
        startTimer();  // Đây là phương thức để khởi tạo lại timer
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusic.pause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        gameOver();
        backgroundMusic.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundMusic.start();

    }

    private void loadQuestion() {
        // Kiểm tra nếu danh sách câu hỏi không rỗng và chỉ số câu hỏi hợp lệ
        if (questionList != null && !questionList.isEmpty() && currentQuestionIndex < questionList.size()) {
            // Lấy câu hỏi hiện tại từ danh sách
            currentQuestion = questionList.get(currentQuestionIndex);

            // Hiển thị thông tin câu hỏi
            resetButtonColors(); // Đặt lại màu nút
            resetOptionsVisibility(); // Đặt lại sự hiển thị của các nút đáp án
            enableAnswerButtons(true); // Kích hoạt các nút đáp án

            if (currentQuestionIndex == 0)
                startTimer();

            tvQuestionNumber.setText("Câu " + (currentQuestionIndex + 1));  // Cập nhật số câu hỏi theo chỉ số
            tvQuestion.setText(currentQuestion.question);
            btnOptionA.setText("A. " + currentQuestion.optionA);
            btnOptionB.setText("B. " + currentQuestion.optionB);
            btnOptionC.setText("C. " + currentQuestion.optionC);
            btnOptionD.setText("D. " + currentQuestion.optionD);



        } else {
            // Nếu không có câu hỏi hợp lệ, kết thúc game hoặc thực hiện hành động khác
            endGame();
        }
    }


    private void resetButtonColors() {
        btnOptionA.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnOptionB.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnOptionC.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnOptionD.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void enableAnswerButtons(boolean enable) {
        // Kích hoạt hoặc vô hiệu hóa các nút đáp án
        btnOptionA.setEnabled(enable);
        btnOptionB.setEnabled(enable);
        btnOptionC.setEnabled(enable);
        btnOptionD.setEnabled(enable);
    }

    private void resetOptionsVisibility() {
        btnOptionA.setVisibility(View.VISIBLE);
        btnOptionB.setVisibility(View.VISIBLE);
        btnOptionC.setVisibility(View.VISIBLE);
        btnOptionD.setVisibility(View.VISIBLE);

        // Bật lại nút 50/50 nếu cần reset
        btnFiftyFifty.setEnabled(true);
    }

    private void startTimer() {
        // Dừng timer trước đó nếu có
        if (timer != null) {
            timer.cancel();
        }

        progressBarTime.setProgress(100);
        timer = new CountDownTimer(10000, 100) { // 10 giây, cập nhật mỗi 100ms
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 100);
                progressBarTime.setProgress(timeLeft);
            }

            @Override
            public void onFinish() {
                progressBarTime.setProgress(0);

            }
        };
        timer.start();
    }

    private void endGame() {


        // Lưu điểm cao nhất nếu cần
        if (score > highScore) {
            highScore = score;
            SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", highScore);
            editor.apply();
        }

        // Hiển thị Dialog kết thúc game
        new AlertDialog.Builder(this)
                .setTitle("Kết thúc trò chơi")
                .setMessage("Tổng điểm của bạn: " + score + "\nKỷ lục hiện tại: " + highScore)
                .setPositiveButton("Chơi lại", (dialog, which) -> restartGame())
                .setNegativeButton("Thoát", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        score = 0;
        questionNumber = 1;
        tvScore.setText("Điểm: 0 | Kỷ lục: " + highScore);
        loadQuestion();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, thực hiện cuộc gọi lại
                useCallFriend();
            } else {
                Toast.makeText(this, "Quyền gọi điện thoại bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void useCallFriend() {
        if (isCallFriendUsed) return; // Chỉ cho phép sử dụng 1 lần
        isCallFriendUsed = true;

        // Tạo Dialog để người chơi nhập số điện thoại
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập số điện thoại người thân");

        // Tạo một EditText trong Dialog
        final EditText inputPhoneNumber = new EditText(this);
        inputPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        inputPhoneNumber.setHint("Số điện thoại...");
        builder.setView(inputPhoneNumber);

        // Nút xác nhận
        builder.setPositiveButton("Gọi", (dialog, which) -> {
            String phoneNumber = inputPhoneNumber.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                makePhoneCall(phoneNumber); // Gọi hàm thực hiện cuộc gọi
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                isCallFriendUsed = false; // Cho phép gọi lại nếu người chơi chưa nhập
            }
        });

        // Nút hủy
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.cancel();
            isCallFriendUsed = false; // Không tính là đã sử dụng nếu hủy
        });

        builder.show();
    }

    private void makePhoneCall(String phoneNumber) {
        // Tạo Intent để gọi điện
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        try {
            // Kiểm tra quyền gọi điện thoại trước khi thực hiện
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                // Yêu cầu cấp quyền nếu chưa có
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show();
        }

        // Disable nút gọi điện thoại sau khi sử dụng
        btnCallFriend.setEnabled(false);
    }

    private void useFiftyFifty(String correct_answer) {
        if (isFiftyFiftyUsed) return; // Chỉ cho phép sử dụng 1 lần
        isFiftyFiftyUsed = true;

        // Disable the 50/50 button after it has been used
        btnFiftyFifty.setEnabled(false);

        // Giả lập đáp án đúng là A
        String correctAnswer = correct_answer;

        // Danh sách các nút lựa chọn
        Button[] buttons = {btnOptionA, btnOptionB, btnOptionC, btnOptionD};
        List<Button> incorrectOptions = new ArrayList<>();

        // Tìm các đáp án sai
        for (Button btn : buttons) {
            if (!btn.getText().toString().contains(correctAnswer)) {
                incorrectOptions.add(btn);
            }
        }

        // Ẩn 2 đáp án sai ngẫu nhiên
        Collections.shuffle(incorrectOptions);
        incorrectOptions.get(0).setVisibility(View.INVISIBLE);
        incorrectOptions.get(1).setVisibility(View.INVISIBLE);


    }

    private List<Question> questionList; // Danh sách câu hỏi
    private int currentQuestionIndex = 0; // Chỉ số câu hỏi hiện tại
    private Question currentQuestion; // Câu hỏi hiện tại

    // Lớp Question để chứa câu hỏi
    private class Question {
        int questionNumber;
        String question, optionA, optionB, optionC, optionD, correctAnswer;
    }

    private void loadQuestionsFromFile() {
        try {
            // Đọc file từ thư mục assets
            InputStream inputStream = getAssets().open("questions.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Chuyển dữ liệu từ JSON thành chuỗi
            String json = new String(buffer, "UTF-8");

            // Sử dụng Gson để parse JSON
            Gson gson = new Gson();
            Question[] questions = gson.fromJson(json, Question[].class);
            questionList = Arrays.asList(questions);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đọc file câu hỏi", Toast.LENGTH_SHORT).show();
        }
    }







}
