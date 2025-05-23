package com.example.finalproject_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject_android.models.UserLoginRequest;
import com.example.finalproject_android.models.UserLoginResponse;
import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {
    ImageButton back, showpass;
    EditText username, password;
    Button signin;
    ApiService apiService;
    TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        username = findViewById(R.id.username_signin);
        back = findViewById(R.id.back_si);
        showpass = findViewById(R.id.showpass_si);
        password = findViewById(R.id.password_signIn);
        signin = findViewById(R.id.signInButton);
        forgot = findViewById(R.id.forgot_txt);

        apiService = ApiClient.getClient(SignIn.this).create(ApiService.class);

        forgot.setOnClickListener(view -> {
            Intent intent = new Intent(SignIn.this, ForgotPassword.class);
            startActivity(intent);
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSignIn();
            }
        });
        showpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showpass.setImageResource(R.drawable.eye);
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showpass.setImageResource(R.drawable.eyeclosed);
                }
                password.setSelection(password.length());
            }
        });


    }
    private void performSignIn() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        UserLoginRequest userLoginRequest = new UserLoginRequest(user, pass);
        Call<UserLoginResponse> call = apiService.login(userLoginRequest);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String email = response.body().getEmail();

                    UserSession userSession = new UserSession(SignIn.this);
                    userSession.createUserSession(email, token, user);
                    Intent intent = new Intent(SignIn.this, BottomNavigation.class);
                    Toast.makeText(SignIn.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(SignIn.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                Toast.makeText(SignIn.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}