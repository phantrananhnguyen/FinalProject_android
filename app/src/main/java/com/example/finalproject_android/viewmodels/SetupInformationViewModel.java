package com.example.finalproject_android.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.models.UserUpdateRequest;
import com.example.finalproject_android.models.UserUpdateResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class SetupInformationViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private String email;
    private UserSession userSession;

    // LiveData để theo dõi trạng thái
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    public SetupInformationViewModel(Application application) {
        super(application);

        userSession = new UserSession(application);
        if (userSession != null) {
            email = userSession.getUserEmail();
        }



        // Khởi tạo ApiService với token
        apiService = ApiClient.getApiServiceWithToken(application);

    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public void updateUserInfo(Context context, UserUpdateRequest request, Uri profilePictureUri) {
        if (!isNetworkAvailable()) {
            toastMessage.setValue("No internet connection. Please check your network settings.");
            return;
        }

        isLoading.setValue(true);

        MultipartBody.Part profilePicturePart = null;
        if (profilePictureUri != null) {
            profilePicturePart = prepareFilePart("profilePicture", profilePictureUri);
        }
        else {
            profilePicturePart = MultipartBody.Part.createFormData("profilePicture", "");
        }

        Log.d("UpdateUser", "Name: " + request.getName());
        Log.d("UpdateUser", "Address: " + request.getAddress());
        Log.d("UpdateUser", "Phone: " + request.getPhoneNumber());
        Log.d("UpdateUser", "Birthday: " + request.getBirthday());
        Log.d("UpdateUser", "Sex: " + request.getSex());

        UserSession userSession = new UserSession(context);


        Call<ResponseBody> call = apiService.updateUser(
                profilePicturePart,
                RequestBody.create(MediaType.parse("text/plain"), request.getName()),
                RequestBody.create(MediaType.parse("text/plain"), request.getAddress()),
                RequestBody.create(MediaType.parse("text/plain"), request.getSex()),
                RequestBody.create(MediaType.parse("text/plain"), request.getBio()),
                RequestBody.create(MediaType.parse("text/plain"), request.getBirthday()),
                RequestBody.create(MediaType.parse("text/plain"), request.getPhoneNumber()),
                null,
                this.email


        );


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    updateSuccess.setValue(true);
                    toastMessage.setValue("User updated successfully");
                    Log.d("UpdateUser", "Response Body: " + response.body());





                } else {
                    toastMessage.setValue("Failed to update user");
                    Log.e("UpdateUser", "Error: " + response.code() + " - " + response.message());
                    Log.e("UpdateUser", "Response: " + response.errorBody());
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("UpdateUser", "Error response: " + errorResponse);
                    } catch (IOException e) {
                        Log.e("UpdateUser", "Error parsing error response: " + e.getMessage());
                    }
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isLoading.setValue(false);
                toastMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            // Sử dụng ContentResolver để lấy InputStream từ Uri
            File tempFile = new File(getApplication().getCacheDir(), "temp_image.jpg");
            try (InputStream inputStream = getApplication().getContentResolver().openInputStream(fileUri);
                 OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            // Tạo RequestBody từ tệp tạm
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication()
                .getSystemService(ConnectivityManager.class);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getToken(Application application) {
        // Lấy SharedPreferences
        SharedPreferences sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Trả về token hoặc null nếu không tồn tại
        return sharedPreferences.getString("JWT_TOKEN", null);
    }
}
