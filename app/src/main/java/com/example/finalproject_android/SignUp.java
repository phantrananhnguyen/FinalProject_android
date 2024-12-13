package com.example.finalproject_android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
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

        apiService = ApiClient.getClient(SignUp.this).create(ApiService.class);

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
                            if (response.body().getIsVerified()) {
                                checkAndDownloadMap(email);
                                showSuccessDialog(email);
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                handler.removeCallbacks(checkVerificationStatusRunnable);
                            } else {
                                handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL);
                            }
                        } else {
                            Toast.makeText(SignUp.this, "Lỗi khi kiểm tra trạng thái xác thực", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<VerificationStatusResponse> call, Throwable t) {
                        Toast.makeText(SignUp.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL);
                    }
                });
            }
        };
        handler.postDelayed(checkVerificationStatusRunnable, POLLING_INTERVAL);
    }

    private void showSuccessDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Verify email successfully");
        builder.setMessage("Please provide us your information to continue");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SignUp.this, SetupInfor.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        builder.show();
    }
    private void checkAndDownloadMap(String email) {
        File mapFile = new File(getFilesDir(), "langdaihoc.map");
        if (mapFile.exists()) {
            Toast.makeText(this, "Map already exists.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Downloading map...", Toast.LENGTH_SHORT).show();
        apiService.downloadMap(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isSaved = saveMapFile(response.body());
                    if (isSaved) {
                        Toast.makeText(SignUp.this, "Map downloaded successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUp.this, "Failed to save map.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUp.this, "Error downloading map.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignUp.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean saveMapFile(ResponseBody responseBody) {
        try (InputStream inputStream = responseBody.byteStream();
             OutputStream outputStream = new FileOutputStream(new File(getFilesDir(), "langdaihoc.map"))) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}



