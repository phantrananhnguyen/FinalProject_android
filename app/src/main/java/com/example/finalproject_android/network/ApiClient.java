package com.example.finalproject_android.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://e2f1-2001-ee0-5681-e4e1-402b-1076-b8e7-da7e.ngrok-free.app") // Địa chỉ localhost hoặc IP của server Node.js
                   // .baseUrl("https://server-pothole-androi-app.onrender.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
