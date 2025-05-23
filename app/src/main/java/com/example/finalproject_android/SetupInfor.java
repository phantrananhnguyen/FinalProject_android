package com.example.finalproject_android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.finalproject_android.models.UserInfo;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private ImageView profileImage;
    private Button submitButton;
    private ApiService apiService;
    private String email;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_infor);

        email = getIntent().getStringExtra("email");
        apiService = ApiClient.getClient(this).create(ApiService.class);
        initUI();
        setupEventListeners();

        userInfo = new UserInfo("", "", "","", "bio", "", "", "", "");
    }

    private void initUI() {
        nicknameInput = findViewById(R.id.nickname_input);
        addressInput = findViewById(R.id.address_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        dateOfBirthPicker = findViewById(R.id.date_of_birth_picker);
        sexSpinner = findViewById(R.id.sex_spinner);
        submitButton = findViewById(R.id.submit_button);
    }



    private void setupEventListeners() {
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String name = nicknameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        int day = dateOfBirthPicker.getDayOfMonth();
        int month = dateOfBirthPicker.getMonth();
        int year = dateOfBirthPicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String birthday = sdf.format(calendar.getTime());

        String currentDate = sdf.format(new Date());
        String sex = sexSpinner.getSelectedItem().toString();
        if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d{10}")) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        userInfo.updateUserInfo(name,birthday, address, "bio", "bronze",phoneNumber,currentDate, sex);
        sendUserInfoToServer();
    }
    private void sendUserInfoToServer() {
        RequestBody namePart = RequestBody.create(MultipartBody.FORM, userInfo.getName());
        RequestBody addressPart = RequestBody.create(MultipartBody.FORM, userInfo.getAddress());
        RequestBody sexPart = RequestBody.create(MultipartBody.FORM, userInfo.getSex());
        RequestBody bioPart = RequestBody.create(MultipartBody.FORM, userInfo.getBio());
        RequestBody birthdayPart = RequestBody.create(MultipartBody.FORM, userInfo.getBirthday());
        RequestBody phonePart = RequestBody.create(MultipartBody.FORM, userInfo.getPhone());
        RequestBody sincePart = RequestBody.create(MultipartBody.FORM, userInfo.getSince());
        RequestBody MembertypePart = RequestBody.create(MultipartBody.FORM, userInfo.getProfilePicture());
        apiService.updateUser(MembertypePart, namePart, addressPart, sexPart, bioPart, birthdayPart, phonePart,sincePart, email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    showDialog();
                } else {
                    Toast.makeText(SetupInfor.this, "Failed to update user information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SetupInfor.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
            finish();
        });
    }
}
