package com.example.finalproject_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.finalproject_android.models.UserUpdateRequest;
import com.example.finalproject_android.viewmodels.SetupInformationViewModel;

public class SetupInformationActivity extends AppCompatActivity {

    private SetupInformationViewModel viewModel;
    private EditText nicknameInput, addressInput, phoneNumberInput;
    private DatePicker dateOfBirthPicker;
    private Spinner sexSpinner;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_information);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SetupInformationViewModel.class);

        // Bind views
        nicknameInput = findViewById(R.id.nickname_input);
        addressInput = findViewById(R.id.address_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        dateOfBirthPicker = findViewById(R.id.date_of_birth_picker);
        sexSpinner = findViewById(R.id.sex_spinner);
        Button submitButton = findViewById(R.id.submit_button);
        ImageView profileImage = findViewById(R.id.profile_image);

        // Event to select profile picture
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });

        // Observe LiveData from ViewModel
        observeLiveData();

        // Handle Submit button click
        submitButton.setOnClickListener(v -> {
            String name = nicknameInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            int day = dateOfBirthPicker.getDayOfMonth();
            int month = dateOfBirthPicker.getMonth();
            int year = dateOfBirthPicker.getYear();
            String sex = sexSpinner.getSelectedItem().toString();

            if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String birthday = year + "-" + (month + 1) + "-" + day;
            UserUpdateRequest request = new UserUpdateRequest(name, address, sex, "", birthday, phoneNumber);
            viewModel.updateUserInfo(request, profilePictureUri);
        });
    }

    private void observeLiveData() {
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                // Show loading progress bar or popup
            } else {
                // Hide loading progress bar
            }
        });

        // Observe toast message
        viewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdateSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Intent intent = new Intent(SetupInformationActivity.this, MainActivity.class);
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

            // Set image as circular using Glide with CircleCrop
            Glide.with(this)
                    .load(profilePictureUri)
                    .transform(new CircleCrop())  // Apply circular crop
                    .into(profileImage);
        }
    }
}
