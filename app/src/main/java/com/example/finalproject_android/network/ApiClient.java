package com.example.finalproject_android.network;

import android.content.Context;

import com.example.finalproject_android.models.UserSession;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static OkHttpClient client;
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(new AuthInterceptor(context));
            client = httpClientBuilder.build();
            retrofit = new Retrofit.Builder()
                    //.baseUrl("http://54.144.2.202:3000/") // Địa chỉ localhost hoặc IP của server Node.js
                    .baseUrl("https://bb6a-171-247-164-15.ngrok-free.app")
                    //.baseUrl("http://10.0.2.2:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
