package com.example.finalproject_android.models;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_TOKEN = "userToken";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    public UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createUserSession(String email, String token) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_TOKEN, token);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public String getUserToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, null);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}
