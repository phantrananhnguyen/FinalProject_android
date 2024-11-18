package com.example.finalproject_android.viewmodels;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.finalproject_android.models.UserUpdateRequest;
import com.example.finalproject_android.models.UserUpdateResponse;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetupInformationViewModel extends AndroidViewModel {

    private final ApiService apiService;

    // LiveData để theo dõi trạng thái
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    public SetupInformationViewModel(Application application) {
        super(application);
        apiService = ApiClient.getApiServiceWithToken(getToken());
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

    public void updateUserInfo(UserUpdateRequest request, Uri profilePictureUri) {
        if (!isNetworkAvailable()) {
            toastMessage.setValue("No internet connection. Please check your network settings.");
            return;
        }

        isLoading.setValue(true);

        MultipartBody.Part profilePicturePart = null;
        if (profilePictureUri != null) {
            profilePicturePart = prepareFilePart("profilePicture", profilePictureUri);
        }

        Call<UserUpdateResponse> call = apiService.updateUser(
                RequestBody.create(MediaType.parse("text/plain"), request.getName()),
                RequestBody.create(MediaType.parse("text/plain"), request.getAddress()),
                RequestBody.create(MediaType.parse("text/plain"), request.getPhoneNumber()),
                RequestBody.create(MediaType.parse("text/plain"), request.getBirthday()),
                RequestBody.create(MediaType.parse("text/plain"), request.getSex()),
                profilePicturePart
        );

        call.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    updateSuccess.setValue(true);
                    toastMessage.setValue("User updated successfully");
                } else {
                    toastMessage.setValue("Failed to update user");
                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                isLoading.setValue(false);
                toastMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication()
                .getSystemService(ConnectivityManager.class);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getToken() {
        return getApplication()
                .getSharedPreferences("MyAppPreferences", Application.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);
    }
}
