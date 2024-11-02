package com.example.finalproject_android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
                    //loading  on  pause
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignUp.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
