package com.example.finalproject_android;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.finalproject_android.models.Potholemodel;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotholeDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Spinner typeSpinner;
    private Button submitButton, deleteButton;
    private ImageButton addImageButton, backButton;
    private Uri imageUri;
    private String potholeId;
    private String img;
    private String author;
    private EditText coordinatesInput, dateInput, idInput  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        imageView = findViewById(R.id.image_view);
        typeSpinner = findViewById(R.id.type_spinner);
        submitButton = findViewById(R.id.report_submit_btn);
        deleteButton = findViewById(R.id.report_delete_btn);
        addImageButton = findViewById(R.id.image_view);
        coordinatesInput = findViewById(R.id.coordinates_text_input);
        dateInput = findViewById(R.id.date_input);
        idInput = findViewById(R.id.id_input);
        backButton = findViewById(R.id.report_back_btn);

        backButton.setOnClickListener(v -> {
            finish();
        });



        // Nhận thông tin ổ gà từ Activity trước
        Intent intent = getIntent();
        potholeId = intent.getStringExtra("potholeId");
        String state = intent.getStringExtra("state");
        String dateMillis = intent.getStringExtra("date");
        String type = intent.getStringExtra("type");
        Double latitude = intent.getDoubleExtra("latitude", 0.0);
        Double longitude = intent.getDoubleExtra("longitude", 0.0);



        img = intent.getStringExtra("img");
        author = intent.getStringExtra("author");

        Log.d("PotholeDetailActivity", "Day la link img: " + img);




        coordinatesInput.setText("Vĩ độ: " + latitude + "\nKinh độ: " + longitude);
        dateInput.setText(dateMillis);  // Dùng chuỗi ngày tháng định dạng
        idInput.setText(potholeId);

        // Thiết lập spinner cho loại cảnh báo
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // Gán giá trị mặc định cho Spinner nếu có giá trị 'type' từ Intent
        if (type != null) {
            int spinnerPosition = adapter.getPosition(type);
            typeSpinner.setSelection(spinnerPosition);
        }

        if (img != null) {
            // Lấy tên file từ URL
            String fileName = getFileNameFromUrl(img);
            Log.d("PotholeDetailActivity", "Tên file: " + fileName);

            loadPotholePicture(fileName);


        }

        // Khóa các ô nhập liệu
        coordinatesInput.setEnabled(false);
        coordinatesInput.setFocusable(false);
        dateInput.setEnabled(false);
        dateInput.setFocusable(false);
        idInput.setEnabled(false);
        idInput.setFocusable(false);




        // Thêm logic chọn ảnh
        addImageButton.setOnClickListener(v -> openFileChooser());

        // Xử lý khi nhấn nút gửi
        submitButton.setOnClickListener(v -> uploadData());

        deleteButton.setOnClickListener(v -> showDeleteDialog());
    }

    private String getFileNameFromUrl(String url) {
        // Tách phần tên file sau dấu "/"
        if (url != null && url.contains("/")) {
            // Tách từ phần cuối URL sau dấu "/"
            String[] parts = url.split("/");
            return parts[parts.length - 1];  // Lấy phần cuối cùng (tên file)
        }
        return null;
    }


    private void loadPotholePicture(String fileName) {
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.getPotholePicture(fileName);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Dashboard", "Profile picture loaded successfully");

                    if (response.body() != null) {
                        InputStream inputStream = response.body().byteStream();
                        Log.d("Dashboard", "InputStream size: " + response.body().contentLength() + " bytes");

                        try {
                            // Chuyển InputStream thành Bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            if (bitmap != null) {
                                Log.d("Dashboard", "Bitmap decoded successfully");
                                imageView.setImageBitmap(bitmap);
                            } else {
                                Log.e("Dashboard", "Failed to decode Bitmap.");
                            }
                        } catch (Exception e) {
                            Log.e("Dashboard", "Error decoding InputStream to Bitmap: ", e);
                        }
                    } else {
                        Log.e("Dashboard", "Response body is null!");
                    }
                } else {
                    Log.e("Dashboard", "Response failed: " + response.message());
                }
            }




            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Xử lý khi có lỗi kết nối
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Hiển thị ảnh ngay lập tức sau khi người dùng chọn
            imageView.setImageURI(imageUri);
        }
    }


    private void uploadData() {
        if (imageUri != null) {
            try {
                // Chuyển đổi Uri thành File tạm thời
                File file = createTempFileFromUri(imageUri);

                // Kiểm tra MIME type
                String mimeType = getMimeType(imageUri);
                if (mimeType == null) mimeType = "image/jpeg";

                // Tạo request cho file ảnh
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("img", file.getName(), requestFile);

                // Tạo request cho loại (nếu được chọn)
                String selectedType = typeSpinner.getSelectedItem().toString();
                RequestBody type = RequestBody.create(MediaType.parse("text/plain"), selectedType);

                // Gửi request qua API
                ApiService apiService = ApiClient.getApiServiceWithToken(PotholeDetailActivity.this);
                Call<ResponseBody> call = apiService.updatePotholeImage(potholeId, body, type);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PotholeDetailActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PotholeDetailActivity.this, "Upload failed! Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(PotholeDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("UploadError", "Error processing image file: " + e.getMessage(), e);
                Toast.makeText(this, "Error processing image file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private File createTempFileFromUri(Uri uri) throws Exception {
        ContentResolver resolver = getContentResolver();
        InputStream inputStream = resolver.openInputStream(uri);
        File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    private String getMimeType(Uri uri) {
        ContentResolver resolver = getContentResolver();
        return resolver.getType(uri);
    }



    // Hiển thị hộp thoại yêu cầu xóa ổ gà
    private void showDeleteDialog() {
        final EditText reasonInput = new EditText(this);
        reasonInput.setHint("Nhập lý do xóa");

        new AlertDialog.Builder(this)
                .setTitle("Xóa ổ gà")
                .setMessage("Bạn có chắc chắn muốn yêu cầu xóa ổ gà này?")
                .setView(reasonInput)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String reason = reasonInput.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập lý do!", Toast.LENGTH_SHORT).show();
                    } else {
                        sendDeleteRequest(reason);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendDeleteRequest(String reason) {
        // Chuẩn bị dữ liệu cho request
        Map<String, String> requestData = new HashMap<>();
        requestData.put("author", author);
        requestData.put("potholeId", potholeId); // Thay potholeId bằng giá trị thực tế
        requestData.put("reason", reason);

        // Gửi yêu cầu xóa đến server
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.deleteRequest(requestData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PotholeDetailActivity.this, "Yêu cầu xóa đã được gửi thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(PotholeDetailActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(PotholeDetailActivity.this, "Đã xảy ra lỗi. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PotholeDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
