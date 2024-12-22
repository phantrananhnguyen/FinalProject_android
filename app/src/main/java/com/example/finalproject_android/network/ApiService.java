package com.example.finalproject_android.network;

import com.example.finalproject_android.models.EmailResponse;
import com.example.finalproject_android.models.HistoryItem;
import com.example.finalproject_android.models.Journey;
import com.example.finalproject_android.models.ListJourneyResponse;
import com.example.finalproject_android.models.ListPotholeResponse;
import com.example.finalproject_android.models.Places;
import com.example.finalproject_android.models.Potholemodel;
import com.example.finalproject_android.models.UserInfo;
import com.example.finalproject_android.models.UserLoginRequest;
import com.example.finalproject_android.models.UserLoginResponse;
import com.example.finalproject_android.models.UserRequest;
import com.example.finalproject_android.models.UserResponse;
import com.example.finalproject_android.models.ForgotPassRequest;
import com.example.finalproject_android.models.ForgotPassResponse;
import com.example.finalproject_android.models.UserUpdateResponse;
import com.example.finalproject_android.models.VerificationStatusResponse;
import com.example.finalproject_android.models.VerifyRequest;
import com.example.finalproject_android.models.VerifyResponse;
import com.example.finalproject_android.models.ResetRequest;
import com.example.finalproject_android.models.ResetResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
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
    Call<VerificationStatusResponse> checkVerificationStatus(@Query("email") String email);

    @GET("/api/search/")
    Call<Places> searchPlaces(@Query("keyword") String query);

    @GET("/api/navigation")
    Call<Places> route(@Query("start") String start, @Query("destination") String destination);

    @GET("api/history/history")
    Call<List<HistoryItem>> history(@Query("username") String username);

    @GET("/api/download-map")
    Call<ResponseBody> downloadMap(@Body String email);
    @GET("/api/profile/get")
    Call<UserInfo> getUser(@Query("email") String email);
    @Multipart
    @PUT("/api/profile/update")
    Call<ResponseBody> updateUser(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("sex") RequestBody sex,
            @Part("bio") RequestBody bio,
            @Part("birthday") RequestBody birthday,
            @Part("phone") RequestBody phone,
            @Part("since") RequestBody since,
            @Query("email") String email
            );
    @GET("/api/user/profile-picture/{username}")
    Call<ResponseBody> getProfilePicture(@Path("username") String username);
    @GET("/api/hole/person")
    Call<List<Potholemodel>> getPotholes(@Query("username") String username);
    @GET("/api/journey/current_user")
    Call<ListJourneyResponse> getJourneys(@Query("username") String username);
    @POST("/api/journey/add")
    Call<Void> sendJourney(@Body Journey journey);
}

