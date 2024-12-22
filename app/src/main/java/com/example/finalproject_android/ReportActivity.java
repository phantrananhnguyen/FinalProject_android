package com.example.finalproject_android;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.models.ListPotholeResponse;
import com.example.finalproject_android.models.Pothole;

import com.example.finalproject_android.models.PotholeReportAdapter;
import com.example.finalproject_android.models.Potholemodel;
import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.Spinner;


public class ReportActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAccepted, recyclerViewRejected, recyclerViewPending;
    private PotholeReportAdapter potholeAdapterAccepted, potholeAdapterRejected, potholeAdapterPending;
    private List<Potholemodel> potholeAcceptedList, potholeRejectedList, potholePendingList;
    private UserSession userSession;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_0);

        // Khởi tạo danh sách
        potholeAcceptedList = new ArrayList<>();
        potholeRejectedList = new ArrayList<>();
        potholePendingList = new ArrayList<>();

        // Khởi tạo các View
        TextView pendingTitle = findViewById(R.id.pending_title);
        TextView rejectedTitle = findViewById(R.id.rejected_title);
        TextView acceptedTitle = findViewById(R.id.accepted_title);

        // Khởi tạo RecyclerView
        recyclerViewAccepted = findViewById(R.id.recycler_view_accepted);
        recyclerViewRejected = findViewById(R.id.recycler_view_rejected);
        recyclerViewPending = findViewById(R.id.recycler_view_pending);

        recyclerViewAccepted.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRejected.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this));

         userSession = new UserSession(this);



        getPotholesFromApi();




        potholeAdapterAccepted = new PotholeReportAdapter(potholeAcceptedList, this::onPotholeClick);
        potholeAdapterRejected = new PotholeReportAdapter(potholeRejectedList, this::onPotholeClick);
        potholeAdapterPending =  new PotholeReportAdapter(potholePendingList, this::onPotholeClick);

        recyclerViewAccepted.setAdapter(potholeAdapterAccepted);
        recyclerViewRejected.setAdapter(potholeAdapterRejected);
        recyclerViewPending.setAdapter(potholeAdapterPending);

        pendingTitle.setOnClickListener(v -> {
            if (recyclerViewPending.getVisibility() == View.GONE) {
                recyclerViewPending.setVisibility(View.VISIBLE); // Hiện phần Pending
            } else {
                recyclerViewPending.setVisibility(View.GONE); // Ẩn phần Pending
            }
        });

        rejectedTitle.setOnClickListener(v -> {
            if (recyclerViewRejected.getVisibility() == View.GONE) {
                recyclerViewRejected.setVisibility(View.VISIBLE); // Hiện phần Rejected
            } else {
                recyclerViewRejected.setVisibility(View.GONE); // Ẩn phần Rejected
            }
        });

        acceptedTitle.setOnClickListener(v -> {
            if (recyclerViewAccepted.getVisibility() == View.GONE) {
                recyclerViewAccepted.setVisibility(View.VISIBLE); // Hiện phần Accepted
            } else {
                recyclerViewAccepted.setVisibility(View.GONE); // Ẩn phần Accepted
            }
        });

        back = findViewById(R.id.report_back_btn);
        back.setOnClickListener(v -> {
            finish();
        });



    }

    // Hàm lấy dữ liệu từ API
    private void getPotholesFromApi() {
        ApiService apiService = ApiClient.getApiServiceWithToken(ReportActivity.this);
        Call<ListPotholeResponse> call = apiService.getPotholes(userSession.getUsername());

        call.enqueue(new Callback<ListPotholeResponse>() {
            @Override
            public void onResponse(Call<ListPotholeResponse> call, Response<ListPotholeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Potholemodel> potholeList = response.body().getPotholes();

                    // Phân loại pothole theo trạng thái
                    for (Potholemodel pothole : potholeList) {
                        switch (pothole.getState()) {
                            case "accepted":
                                potholeAcceptedList.add(pothole);
                                break;
                            case "rejected":
                                potholeRejectedList.add(pothole);
                                break;
                            case "pending":
                                potholePendingList.add(pothole);
                                break;
                        }
                    }


                    // Khởi tạo Adapter và kết nối với RecyclerView
                    potholeAdapterAccepted = new PotholeReportAdapter(potholeAcceptedList, ReportActivity.this::onPotholeClick);
                    potholeAdapterRejected = new PotholeReportAdapter(potholeRejectedList, ReportActivity.this::onPotholeClick);
                    potholeAdapterPending = new PotholeReportAdapter(potholePendingList, ReportActivity.this::onPotholeClick);

                    recyclerViewAccepted.setAdapter(potholeAdapterAccepted);
                    recyclerViewRejected.setAdapter(potholeAdapterRejected);
                    recyclerViewPending.setAdapter(potholeAdapterPending);

                    // Cập nhật Adapter
                    potholeAdapterAccepted.notifyDataSetChanged();
                    potholeAdapterRejected.notifyDataSetChanged();
                    potholeAdapterPending.notifyDataSetChanged();

                } else {
                    Toast.makeText(ReportActivity.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListPotholeResponse> call, Throwable t) {
                Log.e("RankingActivity", "API request failed: ", t);            }
        });
    }

    // Xử lý sự kiện click vào một pothole
    private void onPotholeClick(Potholemodel pothole) {
        Intent intent = new Intent(ReportActivity.this, PotholeDetailActivity.class);
        intent.putExtra("potholeId", pothole.getId());
        intent.putExtra("author", pothole.getAuthor());
        intent.putExtra("date", pothole.getDate());
        intent.putExtra("type", pothole.getType());
        intent.putExtra("state", pothole.getState());
        intent.putExtra("latitude", pothole.getLatitude());
        intent.putExtra("longitude", pothole.getLongitude());
        intent.putExtra("img", pothole.getImg());

        Log.d("PotholeDetailActivity", "Day la lat và lon: " + pothole.getLatitude() + ", " + pothole.getLongitude());

        startActivity(intent);
    }


    }
