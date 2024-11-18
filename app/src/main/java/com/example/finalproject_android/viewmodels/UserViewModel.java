package com.example.finalproject_android.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject_android.models.User;

public class UserViewModel extends ViewModel {

    // MutableLiveData cho phép thay đổi giá trị dữ liệu
    private MutableLiveData<User> userLiveData;

    public UserViewModel() {
        userLiveData = new MutableLiveData<>();
    }

    // Getter để lấy dữ liệu User
    public LiveData<User> getUser() {
        return userLiveData;
    }

    // Setter để cập nhật thông tin User
    public void setUser(User user) {
        userLiveData.setValue(user);
    }

    // Các phương thức khác để cập nhật dữ liệu người dùng (nếu cần thiết)
    public void updateUserInfo(String name, String email, String profilePicture, String phoneNumber) {
        User currentUser = userLiveData.getValue();
        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setProfilePicture(profilePicture);
            currentUser.setPhoneNumber(phoneNumber);
            // Cập nhật lại LiveData
            userLiveData.setValue(currentUser);
        }
    }
}
