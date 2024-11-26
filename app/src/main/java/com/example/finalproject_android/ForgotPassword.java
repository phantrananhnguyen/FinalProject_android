package com.example.finalproject_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.finalproject_android.models.ForgotPassRequest;
import com.example.finalproject_android.models.ForgotPassResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {
    EditText email;
    ApiService apiService;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.email_forgot);
        button = findViewById(R.id.forgotButton);
        apiService = ApiClient.getClient().create(ApiService.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performForgotPass();
                String data = email.getText().toString();
                Intent intent = new Intent(ForgotPassword.this, Verify.class);
                intent.putExtra("data_key",data);
                startActivity(intent);

            }
        });

    }
    private void performForgotPass() {
        String mail = email.getText().toString().trim();


        ForgotPassRequest forgotPassRequest = new ForgotPassRequest(mail);

        Call<ForgotPassResponse> call = apiService.forgot(forgotPassRequest);
        call.enqueue(new Callback<ForgotPassResponse>() {
            @Override
            public void onResponse(Call<ForgotPassResponse> call, Response<ForgotPassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                } else if (response.code() == 404) {
                    Toast.makeText(ForgotPassword.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPassResponse> call, Throwable t) {
                Toast.makeText(ForgotPassword.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}