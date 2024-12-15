package com.example.finalproject_android.afterlogin;

import android.os.Bundle;
import com.bumptech.glide.Glide;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("profile","created");
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        userSession = new UserSession(getContext());
        if (userSession != null) {
            userEmail = userSession.getUserEmail();
        }
        Log.e("profile",userEmail);
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
                    // Populate the profile with user data
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
        Log.e("profile",user.getSince());
        bio.setText(user.getBio());

        // Load avatar using Glide
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            String imageUrl = "http://10.0.2.2:3000" + user.getProfilePicture();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.defaultusr) // Placeholder image
                    .error(R.drawable.defaultusr)      // Error image
                    .into(avatar);
        } else {
            avatar.setImageResource(R.drawable.defaultusr);
        }

        // Set gender icon
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

        // Set membership icon (optional)
        member.setImageResource(R.drawable.bronze);
    }


}