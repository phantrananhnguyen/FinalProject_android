package com.example.finalproject_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_android.models.GoogleLoginRequest;
import com.example.finalproject_android.models.GoogleLoginResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button signin, signup;
    ImageButton google;
    ApiService apiService;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        signin = findViewById(R.id.signInButton);
        signup = findViewById(R.id.signUpButton);
        google = findViewById(R.id.gglog);

        apiService = ApiClient.getClient().create(ApiService.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    sendGoogleLoginRequest(idToken);
                }
            } catch (ApiException e) {
                Log.w("Google Sign-In", "Google sign in failed", e);
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendGoogleLoginRequest(String idToken) {
        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest(idToken);
        Call<GoogleLoginResponse> call = apiService.logingg(googleLoginRequest);

        call.enqueue(new Callback<GoogleLoginResponse>() {
            @Override
            public void onResponse(Call<GoogleLoginResponse> call, Response<GoogleLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GoogleLoginResponse googleLoginResponse = response.body();
                    String token = googleLoginResponse.getToken();
                    Toast.makeText(MainActivity.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình chính hoặc lưu token nếu cần
                } else {
                    Toast.makeText(MainActivity.this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GoogleLoginResponse> call, Throwable t) {
                Log.e("Google Login Error", t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
