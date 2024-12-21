package com.example.finalproject_android.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
//                    .baseUrl("https://4a49-1-53-50-221.ngrok-free.app") // Địa chỉ localhost hoặc IP của server Node.js
                    .baseUrl("https://695d-58-186-47-245.ngrok-free.app")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
