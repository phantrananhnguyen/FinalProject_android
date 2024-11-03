package com.example.finalproject_android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_android.models.UserRequest;
import com.example.finalproject_android.models.UserResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import com.example.finalproject_android.models.EmailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    ImageButton back, showpass, showconfirm;
    EditText username, email, password, confirmpass;
    ApiService apiService;
    Button signup;
    ProgressDialog progressDialog;

    private Handler handler = new Handler();
    private Runnable checkVerificationStatusRunnable;
    private static final int POLLING_INTERVAL = 5000;
    private int verificationAttemptCount = 0;
    private static final int MAX_ATTEMPTS = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        back = findViewById(R.id.back_su);
        showpass = findViewById(R.id.showpass_su);
        showconfirm = findViewById(R.id.show_confirm_up);
        username = findViewById(R.id.username_signUp);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password_signUp);
        confirmpass = findViewById(R.id.confirm_pass);
        signup = findViewById(R.id.signUpButton1);

        apiService = ApiClient.getClient().create(ApiService.class);

        back.setOnClickListener(view -> finish());

        showpass.setOnClickListener(view -> togglePasswordVisibility(password, showpass));
        showconfirm.setOnClickListener(view -> togglePasswordVisibility(confirmpass, showconfirm));
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSignUp();
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageButton toggleButton) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleButton.setImageResource(R.drawable.eye);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.eyeclosed);
        }
        editText.setSelection(editText.length());
    }

    private void performSignUp() {
        String user = username.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confirmPass = confirmpass.getText().toString().trim();

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRequest userRequest = new UserRequest(user, mail, pass);

        Call<UserResponse> call = apiService.signup(userRequest);
        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage("Vui lòng kiểm tra email của bạn để xác nhận...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (!userResponse.isVerify()) {
                        waitForEmailVerification(userResponse.getToken());
                    } else {
                        Toast.makeText(SignUp.this, "Đăng ký thất bại. Thử lại sau!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(SignUp.this, "Đăng ký thất bại. Thử lại sau!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignUp.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void waitForEmailVerification(String token) {
        checkVerificationStatusRunnable = new Runnable() {
            @Override
            public void run() {
                Call<EmailResponse> verifyEmailCall = apiService.verifyEmail(token);
                verifyEmailCall.enqueue(new Callback<EmailResponse>() {
                    @Override
                    public void onResponse(Call<EmailResponse> call, Response<EmailResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isVerified()) {
                            Log.e("Email","aaaaa" );
                            progressDialog.dismiss();
                            handler.removeCallbacks(checkVerificationStatusRunnable);
                            showSuccessDialog();
                        } else {
                            handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL);
                        }
                    }

                    @Override
                    public void onFailure(Call<EmailResponse> call, Throwable t) {
                        Log.e("EmailVerification", "Error verifying email: " + t.getMessage());
                        Toast.makeText(SignUp.this, "Lỗi kết nối, thử lại sau...", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL);
                    }
                });
            }
        };
        handler.post(checkVerificationStatusRunnable);
    }


    private void handleVerificationError(int errorCode) {
        progressDialog.dismiss();
        handler.removeCallbacks(checkVerificationStatusRunnable);
        if (errorCode == 400) {
            Toast.makeText(this, "Token không hợp lệ hoặc đã hết hạn.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra. Thử lại sau!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showSuccessDialog() {
        Log.d("SignUp", "Showing success dialog");
        new AlertDialog.Builder(SignUp.this)
                .setTitle("Đăng ký thành công")
                .setMessage("Tài khoản của bạn đã được xác nhận thành công!!!")
                .setPositiveButton("LOGIN NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish(); // Kết thúc Activity hoặc chuyển hướng về màn hình đăng nhập
                    }
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkVerificationStatusRunnable);
    }
}