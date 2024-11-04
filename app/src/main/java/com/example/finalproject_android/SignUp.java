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
import com.example.finalproject_android.models.VerificationStatusResponse;
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
    private static final int POLLING_INTERVAL = 2000;

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
        call.enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showWaitingDialog();

                    checkStatusVerify(response.body().getEmail());
                } else {
                    Toast.makeText(SignUp.this, "Đăng ký thất bại. Thử lại sau!", Toast.LENGTH_LONG).show();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignUp.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void showWaitingDialog() {

        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage("Vui lòng kiểm tra email của bạn để xác nhận...");

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void checkStatusVerify(String email) {
        checkVerificationStatusRunnable = new Runnable() {
            @Override
            public void run() {
                Call<VerificationStatusResponse> call = apiService.checkVerificationStatus(email);
                call.enqueue(new Callback<VerificationStatusResponse>() {

                    @Override
                    public void onResponse(Call<VerificationStatusResponse> call, Response<VerificationStatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("Email",""+ response.body().getIsVerified());// Kiểm tra nếu đã xác thực

                            if (response.body().getIsVerified()) {
                                showSuccessDialog();
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                handler.removeCallbacks(checkVerificationStatusRunnable); // Dừng kiểm tra định kỳ
                            } else {
                                handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL); // Lặp lại sau 2 giây
                            }
                        } else {
                            Toast.makeText(SignUp.this, "Lỗi khi kiểm tra trạng thái xác thực", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VerificationStatusResponse> call, Throwable t) {
                        Toast.makeText(SignUp.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL); // Lặp lại sau 2 giây nếu có lỗi
                    }
                });
            }
        };

        handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL); // Bắt đầu kiểm tra
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
}



