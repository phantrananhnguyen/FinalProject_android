package com.example.finalproject_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject_android.models.ResetRequest;
import com.example.finalproject_android.models.ResetResponse;
import com.example.finalproject_android.models.UserRequest;
import com.example.finalproject_android.models.UserResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.example.finalproject_android.network.ApiService;

public class ResetPassword extends AppCompatActivity {
    String email;
    EditText newpass, confirmpass;
    ImageButton showpass, showconfirm;
    ApiService apiService;
    Button buttonreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        newpass = findViewById(R.id.newpassword);
        confirmpass = findViewById(R.id.confirm_newpass);
        showpass = findViewById(R.id.show_newpass);
        showconfirm = findViewById(R.id.show_confirm_new);
        buttonreset = findViewById(R.id.submitButton);
        apiService = ApiClient.getClient().create(ApiService.class);

        email = getIntent().getStringExtra("email_key");

        showpass.setOnClickListener(view -> togglePasswordVisibility(newpass, showpass));
        showconfirm.setOnClickListener(view -> togglePasswordVisibility(confirmpass, showconfirm));
        buttonreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performReset();
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
    private void performReset() {
        String mail = email.trim();
        String pass = newpass.getText().toString().trim();
        String confirmPass = confirmpass.getText().toString().trim();

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetRequest resetRequest = new ResetRequest(pass, mail);

        Call<ResetResponse> call = apiService.reset(resetRequest);
        call.enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(Call<ResetResponse> call, Response<ResetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(ResetPassword.this, SignIn.class);
                    Toast.makeText(ResetPassword.this, "Đổi mật khẩu thành công!!!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResetResponse> call, Throwable t) {
                Toast.makeText(ResetPassword.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}