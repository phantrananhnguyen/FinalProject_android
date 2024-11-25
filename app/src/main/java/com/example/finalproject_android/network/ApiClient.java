package com.example.finalproject_android.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofitWithoutToken;
    private static Retrofit retrofitWithToken;

    // Base URL cho API (Cập nhật theo địa chỉ server của bạn)
    private static final String BASE_URL = "http://10.0.157.120:3000";

    // Client không cần token
    public static Retrofit getClient() {
        if (retrofitWithoutToken == null) {
            retrofitWithoutToken = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithoutToken;
    }

    // Client với token (Lấy token từ SharedPreferences)
    public static Retrofit getClientWithToken(Context context) {
        if (retrofitWithToken == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context)) // AuthInterceptor tự lấy token từ SharedPreferences
                    .build();

            retrofitWithToken = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithToken;
    }

    // Lấy ApiService không cần token
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    // Lấy ApiService với token
    public static ApiService getApiServiceWithToken(Context context) {
        return getClientWithToken(context).create(ApiService.class);
    }
}
