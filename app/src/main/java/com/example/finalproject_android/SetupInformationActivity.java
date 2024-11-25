package com.example.finalproject_android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject_android.afterlogin.Dashboard;
import com.example.finalproject_android.models.UserUpdateRequest;
import com.example.finalproject_android.viewmodels.SetupInformationViewModel;

public class SetupInformationActivity extends AppCompatActivity {

    private SetupInformationViewModel viewModel;
    private EditText nicknameInput, addressInput, phoneNumberInput, bioInput;
    private DatePicker dateOfBirthPicker;
    private Spinner sexSpinner;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_information);

        // Kiểm tra quyền truy cập bộ nhớ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(SetupInformationViewModel.class);

        // Ánh xạ các thành phần giao diện
        nicknameInput = findViewById(R.id.nickname_input);
        addressInput = findViewById(R.id.address_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        dateOfBirthPicker = findViewById(R.id.date_of_birth_picker);
        sexSpinner = findViewById(R.id.sex_spinner);
        Button submitButton = findViewById(R.id.submit_button);
        ImageView profileImage = findViewById(R.id.profile_image);

        // Sự kiện chọn ảnh
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });

        // Quan sát LiveData từ ViewModel
        observeLiveData();

        // Xử lý sự kiện nhấn nút Submit
        submitButton.setOnClickListener(v -> {
            String name = nicknameInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            String bio = "bio";
            int day = dateOfBirthPicker.getDayOfMonth();
            int month = dateOfBirthPicker.getMonth();
            int year = dateOfBirthPicker.getYear();
            String sex = sexSpinner.getSelectedItem().toString();

            if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String birthday = year + "-" + (month + 1) + "-" + day;
            UserUpdateRequest request = new UserUpdateRequest(name, address, profilePictureUri, sex, bio, birthday, phoneNumber);
            viewModel.updateUserInfo(request, profilePictureUri);
        });
    }

    private void observeLiveData() {
        // Quan sát trạng thái tải
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                // Hiển thị ProgressBar hoặc popup loading
            } else {
                // Ẩn ProgressBar
            }
        });

        // Quan sát thông báo Toast
        viewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdateSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Intent intent = new Intent(SetupInformationActivity.this, BottomNavigation.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            profilePictureUri = data.getData();
            ImageView profileImage = findViewById(R.id.profile_image);
            profileImage.setImageURI(profilePictureUri);
        }
    }
}
