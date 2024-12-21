package com.example.finalproject_android.afterlogin;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject_android.R;
import com.example.finalproject_android.models.UserInfo;
import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {
    private TextView edt_name, edt_gender, edt_birthday, edt_address, edt_bio, edt_phone;
    private Button save;
    ApiService apiService;
    String email;
    ImageButton close;
    UserSession userSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        userSession = new UserSession(this);
        if (userSession != null) {
            email = userSession.getUserEmail();
        }
        edt_name = findViewById(R.id.edt_name);
        edt_gender = findViewById(R.id.edt_gender);
        edt_birthday = findViewById(R.id.edt_birthday);
        edt_address = findViewById(R.id.edt_address);
        edt_bio = findViewById(R.id.edt_bio);
        edt_phone = findViewById(R.id.edt_phone);
        save = findViewById(R.id.save);
        close = findViewById(R.id.edt_close);
        apiService = ApiClient.getClient(this).create(ApiService.class);
        getUserProfile();
        setEditListeners();
    }
    private void setEditListeners() {
        edt_name.setOnClickListener(v -> showEditNameDialog());
        edt_gender.setOnClickListener(v -> showGenderSelectionDialog());
        edt_birthday.setOnClickListener(v -> showDatePickerDialog());
        edt_address.setOnClickListener(v -> showEditAddressDialog());
        edt_bio.setOnClickListener(v -> showEditBioDialog());
        edt_phone.setOnClickListener(v -> showEditPhoneDialog());
        save.setOnClickListener(v-> updateUserProfile());
        close.setOnClickListener(v-> finish());
    }
    private void getUserProfile() {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "No email found in session", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the API to fetch user data
        apiService.getUser(email).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfo user = response.body();
                    setupProfile(user);
                } else {
                    Toast.makeText(EditProfile.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable throwable) {
                Toast.makeText(EditProfile.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupProfile(UserInfo user) {
        edt_name.setText(user.getName());
        edt_birthday.setText(user.getBirthday());
        edt_address.setText(user.getAddress());
        edt_gender.setText(user.getSex());
        edt_bio.setText(user.getBio());
        edt_phone.setText(user.getPhone());
    }
    public void updateUserProfile() {
        // Lấy dữ liệu từ các trường sửa
        String name, address, sex, bio, birthday, phone;
        name = edt_name.getText().toString();
        address = edt_address.getText().toString();
        birthday = edt_birthday.getText().toString();
        bio = edt_bio.getText().toString();
        phone = edt_phone.getText().toString();
        sex = edt_gender.getText().toString();
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody sexBody = RequestBody.create(MediaType.parse("text/plain"), sex);
        RequestBody bioBody = RequestBody.create(MediaType.parse("text/plain"), bio);
        RequestBody birthdayBody = RequestBody.create(MediaType.parse("text/plain"), birthday);
        RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);

        // Gọi API để gửi yêu cầu cập nhật
        Call<ResponseBody> call = apiService.updateUser(
                null,
                nameBody,
                addressBody,
                sexBody,
                bioBody,
                birthdayBody,
                phoneBody,
                null,
                this.email  // email lấy từ UserInfo
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    Log.d("Update", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Update", "Error: " + t.getMessage());
            }
        });
    }
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Congratulation!")
                .setMessage("Updated information successfully!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
    // 1. Hộp thoại chỉnh sửa tên
    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Name");

        final EditText input = new EditText(this);
        input.setText(edt_name.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            edt_name.setText(input.getText().toString());
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // 2. Hộp thoại chọn giới tính
    private void showGenderSelectionDialog() {
        String[] genders = {"Male", "Female", "Other"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");

        builder.setItems(genders, (dialog, which) -> {
            edt_gender.setText(genders[which]);
            dialog.dismiss();
        });

        builder.show();
    }

    // 3. Hộp thoại chọn ngày sinh
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> edt_birthday.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // 4. Hộp thoại chỉnh sửa địa chỉ
    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Address");

        final EditText input = new EditText(this);
        input.setText(edt_address.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            edt_address.setText(input.getText().toString());
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // 5. Hộp thoại chỉnh sửa tiểu sử
    private void showEditBioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Bio");

        final EditText input = new EditText(this);
        input.setText(edt_bio.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            edt_bio.setText(input.getText().toString());
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // 6. Hộp thoại chỉnh sửa số điện thoại
    private void showEditPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Phone");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE); // Chỉ nhập số
        input.setText(edt_phone.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            edt_phone.setText(input.getText().toString());
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}