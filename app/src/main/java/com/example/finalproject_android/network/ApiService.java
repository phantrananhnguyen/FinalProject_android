package com.example.finalproject_android.network;

import com.example.finalproject_android.models.EmailResponse;
import com.example.finalproject_android.models.GoogleLoginRequest;
import com.example.finalproject_android.models.GoogleLoginResponse;
import com.example.finalproject_android.models.Potholemodel;
import com.example.finalproject_android.models.UserLoginRequest;
import com.example.finalproject_android.models.UserLoginResponse;
import com.example.finalproject_android.models.UserRequest;
import com.example.finalproject_android.models.UserResponse;
import com.example.finalproject_android.models.ForgotPassRequest;
import com.example.finalproject_android.models.ForgotPassResponse;
import com.example.finalproject_android.models.UserUpdateRequest;
import com.example.finalproject_android.models.UserUpdateResponse;
import com.example.finalproject_android.models.VerificationStatusResponse;
import com.example.finalproject_android.models.VerifyRequest;
import com.example.finalproject_android.models.VerifyResponse;
import com.example.finalproject_android.models.ResetRequest;
import com.example.finalproject_android.models.ResetResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @POST("/api/hole/add")
    Call<Void> sendBumpData(@Body Potholemodel potholemodel);
    @GET("/api/hole/add")
    Call<List<Potholemodel>> getPotholeData();
    @GET("/api/auth/check-verification-status")
    Call<VerificationStatusResponse> checkVerificationStatus(@Query("email")String email);

    @POST("/api/auth/google-login")
    Call<GoogleLoginResponse> logingg(@Body GoogleLoginRequest googleLoginRequest);

    @GET("/api/user/profile")
    Call<UserResponse> getUserData(@Header("Authorization") String token);

    @Multipart
    @POST("/api/user/new_user/update")
    Call<UserUpdateResponse> updateUser(
            @Part("nickname") RequestBody nickname,
            @Part("bio") RequestBody bio,
            @Part("address") RequestBody address,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("birthday") RequestBody birthday,
            @Part("sex") RequestBody sex,
            @Part MultipartBody.Part profilePicture
    );



}


