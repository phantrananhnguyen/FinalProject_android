package com.example.finalproject_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_android.models.ForgotPassRequest;
import com.example.finalproject_android.models.ForgotPassResponse;
import com.example.finalproject_android.models.VerifyRequest;
import com.example.finalproject_android.models.VerifyResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class Verify extends AppCompatActivity {
    TextView Countdown;
    EditText verifyBox1, verifyBox2, verifyBox3, verifyBox4;
    Button button;
    ApiService apiService;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify);
        verifyBox1 = findViewById(R.id.verify_box1);
        verifyBox2 = findViewById(R.id.verify_box2);
        verifyBox3 = findViewById(R.id.verify_box3);
        verifyBox4 = findViewById(R.id.verify_box4);
        setupEditTextListeners();
        button = findViewById(R.id.verifyButton);
        apiService = ApiClient.getClient().create(ApiService.class);
        Countdown = findViewById(R.id.countdown);
        email = getIntent().getStringExtra("data_key");
        startCountdown(60);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performVerify();
            }
        });
        Button resendButton = findViewById(R.id.resendButton);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode();
            }
        });
    }
    private String getVerificationCode() {
        return verifyBox1.getText().toString() +
                verifyBox2.getText().toString() +
                verifyBox3.getText().toString() +
                verifyBox4.getText().toString();
    }

    private void startCountdown(int seconds) {
        button.setVisibility(View.VISIBLE); // Hiển thị nút Verify
        button.setEnabled(true); // Kích hoạt nút Verify
        findViewById(R.id.resendButton).setVisibility(View.GONE); // Ẩn nút Resend khi bắt đầu đếm ngược

        new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                Countdown.setText(String.valueOf(secondsLeft)); // Cập nhật TextView
            }

            @Override
            public void onFinish() {
                Countdown.setText("Expired");
                button.setEnabled(false); // Vô hiệu nút xác nhận
                button.setVisibility(View.GONE); // Ẩn nút Verify
                findViewById(R.id.resendButton).setVisibility(View.VISIBLE); // Hiển thị nút Resend
                findViewById(R.id.resendButton).setEnabled(true); // Kích hoạt nút Resend
            }
        }.start();
    }

    private void resendVerificationCode() {
        // Gọi API forgot-password để gửi lại mã xác nhận
        Call<ForgotPassResponse> call = apiService.forgot(new ForgotPassRequest(email));
        call.enqueue(new Callback<ForgotPassResponse>() {
            @Override
            public void onResponse(Call<ForgotPassResponse> call, Response<ForgotPassResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Verify.this, "Đã gửi mã xác nhận mới qua email", Toast.LENGTH_SHORT).show();
                    startCountdown(60); // Bắt đầu lại countdown
                    button.setVisibility(View.VISIBLE); // Hiển thị nút Verify
                    button.setEnabled(true); // Kích hoạt lại nút xác nhận
                    findViewById(R.id.resendButton).setVisibility(View.GONE); // Ẩn nút Resend
                } else {
                    Toast.makeText(Verify.this, "Không thể gửi mã, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPassResponse> call, Throwable t) {
                Toast.makeText(Verify.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performVerify() {
        String verificationCode = getVerificationCode();
        String mail = email.trim();
        if (verificationCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã xác minh", Toast.LENGTH_SHORT).show();
            return;
        }

        VerifyRequest verifyRequest = new VerifyRequest(verificationCode, mail);
        Call<VerifyResponse> call = apiService.verify(verifyRequest);
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Chuyển hướng sang ResetPassword khi xác minh thành công
                    Intent intent = new Intent(Verify.this, ResetPassword.class);
                    intent.putExtra("email_key", mail);
                    startActivity(intent);
                    finish(); // Đóng Verify để không quay lại
                } else if (response.code() == 400) {
                    Toast.makeText(Verify.this, "Mã xác nhận không hợp lệ", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(Verify.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                Toast.makeText(Verify.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupEditTextListeners() {
        // Tự động chuyển từ ô này sang ô tiếp theo khi nhập xong
        verifyBox1.addTextChangedListener(new MoveToNextTextWatcher(verifyBox1, verifyBox2));
        verifyBox2.addTextChangedListener(new MoveToNextTextWatcher(verifyBox2, verifyBox3));
        verifyBox3.addTextChangedListener(new MoveToNextTextWatcher(verifyBox3, verifyBox4));
        verifyBox4.addTextChangedListener(new MoveToNextTextWatcher(verifyBox4, null)); // Ô cuối cùng
    }

    private class MoveToNextTextWatcher implements TextWatcher {
        private EditText currentEditText;
        private EditText nextEditText;

        public MoveToNextTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1) { // Kiểm tra nếu người dùng nhập xong 1 ký tự
                if (nextEditText != null) {
                    nextEditText.requestFocus(); // Chuyển sang ô tiếp theo
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }


}