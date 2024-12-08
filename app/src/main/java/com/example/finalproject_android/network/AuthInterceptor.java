package com.example.finalproject_android.network;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.finalproject_android.SignIn;
import com.example.finalproject_android.models.UserSession;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context context;
    private String token;

    // Khởi tạo AuthInterceptor với Context để có thể hiển thị Dialog
    public AuthInterceptor(Context context) {
        this.context = context;
        UserSession userSession = new UserSession(context);
        this.token = userSession.getUserToken(); // Lấy token từ UserSession
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = requestBuilder.build();
        Response response = chain.proceed(request);

        // Kiểm tra nếu mã lỗi là 401 (Unauthorized) - Token hết hạn
        if (response.code() == 401) {
            // Nếu token hết hạn, xử lý việc yêu cầu người dùng đăng nhập lại hoặc làm mới token
            // Bạn có thể gọi một API để làm mới token tại đây, hoặc hiển thị một thông báo cho người dùng
            showTokenExpiredDialog();
        }

        return response;
    }

    private void showTokenExpiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Session Expired")
                .setMessage("Your session has expired. Do you want to log in again or extend your session?")
                .setCancelable(false) // Không thể đóng dialog khi chưa chọn
                .setPositiveButton("Log in Again", (dialog, id) -> {
                    Intent intent = new Intent(context, SignIn.class);
                    context.startActivity(intent);
                })
                .setNegativeButton("Extend Session", (dialog, id) -> {
                    // Gọi API để làm mới token tại đây, nếu có (Cập nhật token)
                    refreshToken();
                    Toast.makeText(context, "Token extension not implemented.", Toast.LENGTH_SHORT).show();
                });

        // Hiển thị dialog
        builder.create().show();
    }
    private void refreshToken() {
        // Gọi API để làm mới token và cập nhật lại UserSession với token mới
        // Sau khi lấy token mới, bạn có thể lưu lại và retry yêu cầu API
        // Ví dụ: apiService.refreshToken()
    }
}
