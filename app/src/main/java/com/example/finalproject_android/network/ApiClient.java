package com.example.finalproject_android.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofitWithoutToken;
    private static Retrofit retrofitWithToken;

    // Client without token
    public static Retrofit getClient() {
        if (retrofitWithoutToken == null) {
            retrofitWithoutToken = new Retrofit.Builder()
                    .baseUrl("https://server-pothole-androi-app.onrender.com") // Địa chỉ localhost hoặc IP của server Node.js
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithoutToken;
    }

    // Client with token
    public static Retrofit getClientWithToken(String token) {
        if (retrofitWithToken == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(token))
                    .build();

            retrofitWithToken = new Retrofit.Builder()
                    .baseUrl("https://server-pothole-androi-app.onrender.com") // Địa chỉ localhost hoặc IP của server Node.js
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithToken;
    }

    // Get API service without token
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    // Get API service with token
    public static ApiService getApiServiceWithToken(String token) {
        return getClientWithToken(token).create(ApiService.class);
    }
}
