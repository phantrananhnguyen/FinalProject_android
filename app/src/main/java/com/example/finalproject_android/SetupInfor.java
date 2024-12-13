package com.example.finalproject_android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetupInfor extends AppCompatActivity {
    private EditText nicknameInput, addressInput, phoneNumberInput;
    private DatePicker dateOfBirthPicker;
    private Spinner sexSpinner;
    private Uri profilePictureUri;
    private ImageView profileImage;
    private Button submitButton;
    private ApiService apiService;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_infor);
        email = getIntent().getStringExtra("email");
        apiService = ApiClient.getClient(this).create(ApiService.class);
        initUI();
        checkPermissions();
        setupEventListeners();
    }

    private void initUI() {
        nicknameInput = findViewById(R.id.nickname_input);
        addressInput = findViewById(R.id.address_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        dateOfBirthPicker = findViewById(R.id.date_of_birth_picker);
        sexSpinner = findViewById(R.id.sex_spinner);
        profileImage = findViewById(R.id.profile_image);
        submitButton = findViewById(R.id.submit_button);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission is required to select a profile picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupEventListeners() {
        profileImage.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100 && data != null && data.getData() != null) {
            profilePictureUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profilePictureUri);
                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSubmit() {
        String name = nicknameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        int day = dateOfBirthPicker.getDayOfMonth();
        int month = dateOfBirthPicker.getMonth();
        int year = dateOfBirthPicker.getYear();
        String sex = sexSpinner.getSelectedItem().toString();
        String birthday = String.format("%d-%02d-%02d", year, month + 1, day);

        if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d{10}")) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody namePart = RequestBody.create(MultipartBody.FORM, name);
        RequestBody addressPart = RequestBody.create(MultipartBody.FORM, address);
        RequestBody sexPart = RequestBody.create(MultipartBody.FORM, sex);
        RequestBody bioPart = RequestBody.create(MultipartBody.FORM, "bio");
        RequestBody birthdayPart = RequestBody.create(MultipartBody.FORM, birthday);
        RequestBody phonePart = RequestBody.create(MultipartBody.FORM, phoneNumber);

        MultipartBody.Part imagePart = null;

        if (profilePictureUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(profilePictureUri);
                File tempFile = new File(getCacheDir(), "temp_image.jpg");
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"), tempFile);
                imagePart = MultipartBody.Part.createFormData("image", tempFile.getName(), fileRequestBody);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error preparing image file", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        updateUserInfo(namePart, addressPart, sexPart, bioPart, birthdayPart, phonePart, imagePart);
    }

    private void updateUserInfo(RequestBody name, RequestBody address, RequestBody sex,
                                RequestBody bio, RequestBody birthday, RequestBody phone,
                                MultipartBody.Part image) {
        apiService.updateUser(image, name, address, sex, bio, birthday, phone, email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("map", email);
                    showDialog();
                } else {
                    Toast.makeText(SetupInfor.this, "Failed to update user information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(SetupInfor.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void showDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.signup_congra, null);
        Button loginnow = dialogView.findViewById(R.id.si_congra);
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupInfor.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        loginnow.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(SetupInfor.this, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Đóng màn hình hiện tại
        });
    }
}
