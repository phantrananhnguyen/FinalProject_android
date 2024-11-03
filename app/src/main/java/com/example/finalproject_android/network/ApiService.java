package com.example.finalproject_android.network;

import com.example.finalproject_android.models.EmailResponse;
import com.example.finalproject_android.models.GoogleLoginRequest;
import com.example.finalproject_android.models.GoogleLoginResponse;
import com.example.finalproject_android.models.UserLoginRequest;
import com.example.finalproject_android.models.UserLoginResponse;
import com.example.finalproject_android.models.UserRequest;
import com.example.finalproject_android.models.UserResponse;
import com.example.finalproject_android.models.ForgotPassRequest;
import com.example.finalproject_android.models.ForgotPassResponse;
import com.example.finalproject_android.models.VerifyRequest;
import com.example.finalproject_android.models.VerifyResponse;
import com.example.finalproject_android.models.ResetRequest;
import com.example.finalproject_android.models.ResetResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/auth/signup")
    Call<UserResponse> signup(@Body UserRequest userRequest);

    @POST("/api/auth/login")
    Call<UserLoginResponse> login(@Body UserLoginRequest userLoginRequest);

    @POST("/api/auth/forgot-password")
    Call<ForgotPassResponse> forgot(@Body ForgotPassRequest forgotpassRequest);

    @POST("/api/auth/verify-code")
    Call<VerifyResponse> verify(@Body VerifyRequest verifyRequest);

    @POST("/api/auth/reset-password")
    Call<ResetResponse> reset(@Body ResetRequest resetRequest);

    @GET("/api/auth/verify-email")
    Call<EmailResponse> verifyEmail(@Query("token") String token);

    @POST("api/auth/google-login")
    Call<GoogleLoginResponse> logingg(@Body GoogleLoginRequest googleLoginRequest);


}
