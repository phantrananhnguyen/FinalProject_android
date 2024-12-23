package com.example.finalproject_android.afterlogin;

import android.content.Intent;
import android.os.Bundle;
import com.bumptech.glide.Glide;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject_android.R;
import com.example.finalproject_android.models.UserInfo;
import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.models.UserUpdateResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Profile extends Fragment {
    private TextView name, birthday, address, email, since, bio;
    private ImageView avatar, gender, member;
    private String userEmail;
    private ApiService apiService;
    private UserSession userSession;
    LinearLayout setting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(getActivity());
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        userSession = new UserSession(getContext());
        if (userSession != null) {
            userEmail = userSession.getUserEmail();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profile_name);
        birthday = view.findViewById(R.id.birthday);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.profile_email);
        since = view.findViewById(R.id.since);
        bio = view.findViewById(R.id.bio);
        avatar = view.findViewById(R.id.avatar);
        gender = view.findViewById(R.id.gender);
        member = view.findViewById(R.id.member);
        setting = view.findViewById(R.id.setting);
        setting.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), Setting.class);
            startActivity(intent);
        });
        getUserProfile();
        return view;
    }

    private void getUserProfile() {
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "No email found in session", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the API to fetch user data
        apiService.getUser(userEmail).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfo user = response.body();
                    setupProfile(user);
                } else {
                    Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable throwable) {
                Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Profile", "API call failed", throwable);
            }
        });
    }

    private void setupProfile(UserInfo user) {
        name.setText(user.getName());
        birthday.setText(user.getBirthday());
        address.setText(user.getAddress());
        email.setText(userEmail); // Use the session email
        since.setText(user.getSince());
        bio.setText(user.getBio());
        if (user.getSex() != null) {
            switch (user.getSex().toLowerCase()) {
                case "male":
                    gender.setImageResource(R.drawable.male);
                    break;
                case "female":
                    gender.setImageResource(R.drawable.female);
                    break;
                default:
                    gender.setImageResource(R.drawable.equality);
                    break;
            }
        }
        if (user.getProfilePicture() != null){
            switch (user.getProfilePicture().toLowerCase()){
                case "bronze":
                    member.setImageResource(R.drawable.bronze);
                    break;
                case "silver":
                    member.setImageResource(R.drawable.silver);
                case "gold":
                    member.setImageResource(R.drawable.gold);
                    break;
                default:
                    member.setImageResource(R.drawable.bronze);
                    break;
            }
        }
    }


}