package com.example.finalproject_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.finalproject_android.models.User;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import com.google.android.material.imageview.ShapeableImageView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.InputStream;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private ShapeableImageView firstAchievementsImage, secondAchievementsImage, thirdAchievementsImage;
    private TextView firstTitleText, secondTitleText, thirdTitleText;
    private TextView firstScoreText, secondScoreText, thirdScoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // Khởi tạo các View
        firstAchievementsImage = findViewById(R.id.firstAchievementsImage);
        secondAchievementsImage = findViewById(R.id.secondAchievementsImage);
        thirdAchievementsImage = findViewById(R.id.thirdAchievementsImage);

        firstTitleText = findViewById(R.id.firstTitleText);
        secondTitleText = findViewById(R.id.secondTitleText);
        thirdTitleText = findViewById(R.id.thirdTitleText);

        firstScoreText = findViewById(R.id.firstScoreText);
        secondScoreText = findViewById(R.id.secondScoreText);
        thirdScoreText = findViewById(R.id.thirdScoreText);

        // Gọi API
        fetchTopUsers();
    }

    private void fetchTopUsers() {

        ApiService apiService = ApiClient.getApiService();

        Log.d("RankingActivity", "Calling getTopScores API");

        apiService.getTopScores().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> topUsers = response.body();
                    updateUI(topUsers);
                    Log.d("RankingActivity", "Successfully fetched top users: " + topUsers.size() + " users.");
                } else {
                    Log.e("RankingActivity", "Failed to fetch top users. Response code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(RankingActivity.this, "Không thể lấy danh sách xếp hạng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("RankingActivity", "API call failed: ", t);
                Toast.makeText(RankingActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfilePicture(String fileName, ShapeableImageView imageView) {
        if (fileName == null || fileName.isEmpty()) {
            imageView.setImageResource(R.drawable.profile_image); // Hiển thị ảnh mặc định nếu fileName rỗng
            return;
        }

        Log.d("RankingActivity", "Loading profile picture for file: " + fileName);

        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.getPotholePicture(fileName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Đọc InputStream từ API
                    InputStream inputStream = response.body().byteStream();

                    try {
                        // Chuyển InputStream thành Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap); // Đặt Bitmap vào ImageView
                        } else {
                            imageView.setImageResource(R.drawable.profile_image); // Nếu không có ảnh, hiển thị ảnh lỗi
                        }

                        Log.d("RankingActivity", "Profile picture loaded successfully.");
                    } catch (Exception e) {
                        Log.e("RankingActivity", "Error decoding profile picture: ", e);
                        imageView.setImageResource(R.drawable.profile_image); // Hiển thị ảnh lỗi nếu thất bại
                    }
                } else {
                    Log.e("RankingActivity", "Failed to load profile picture. Response code: " + response.code() + ", message: " + response.message());
                    imageView.setImageResource(R.drawable.profile_image); // Hiển thị ảnh lỗi nếu thất bại
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RankingActivity", "Error fetching profile picture: ", t);
                imageView.setImageResource(R.drawable.user); // Hiển thị ảnh lỗi nếu kết nối thất bại
            }
        });
    }


    private void updateUI(List<User> topUsers) {
        if (topUsers.size() > 0) {
            User second = topUsers.get(0);
            secondTitleText.setText(second.getName());
            secondScoreText.setText("Score: " + second.getScore());
            loadProfilePicture(second.getProfilePicture(), secondAchievementsImage);
        }

        if (topUsers.size() > 1) {
            User first = topUsers.get(1);
            firstTitleText.setText(first.getName());
            firstScoreText.setText("Score: " + first.getScore());
            loadProfilePicture(first.getProfilePicture(), firstAchievementsImage);
        }

        if (topUsers.size() > 2) {
            User third = topUsers.get(2);
            thirdTitleText.setText(third.getName());
            thirdScoreText.setText("Score: " + third.getScore());
            loadProfilePicture(third.getProfilePicture(), thirdAchievementsImage);
        }
    }
}
