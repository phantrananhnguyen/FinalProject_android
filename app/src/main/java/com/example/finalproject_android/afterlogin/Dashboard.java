package com.example.finalproject_android.afterlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.finalproject_android.R;
import com.example.finalproject_android.SetupInformationActivity;
import com.example.finalproject_android.models.Journey;
import com.example.finalproject_android.models.ListJourneyResponse;
import com.example.finalproject_android.models.ListPotholeResponse;
import com.example.finalproject_android.models.Pothole;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Dashboard extends Fragment {
    private WebSocket webSocket;

    private PieChart potholesChart;
    private LineChart potholeStateChart;
    private BarChart weeklyChart;
    private TextView kilometresTextView;
    private TextView potholeNumberTextView;
    private TextView hoursTextView;
    private ImageView profileImage;


    private List<Pothole> potholes = new ArrayList<>();
    private List<Journey> journeys = new ArrayList<>();

    private ApiService apiService;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Retrofit API service

        apiService = ApiClient.getApiServiceWithToken(getContext());


        // Fetch data from APIs
        fetchPotholesData();
        fetchJourneysData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Lấy tuần hiện tại và năm hiện tại
        Calendar currentCalendar = Calendar.getInstance();
        int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        // Tính toán tuần trước
        currentCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        int previousWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int previousYear = currentCalendar.get(Calendar.YEAR);

        // Lưu tuần trước và tuần hiện tại để sử dụng khi thay đổi biểu đồ
        final int finalCurrentWeek = currentWeek;
        final int finalCurrentYear = currentYear;
        final int finalPreviousWeek = previousWeek;
        final int finalPreviousYear = previousYear;

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Gán nút back
        AppCompatImageButton previousButton = view.findViewById(R.id.previousButton);
        AppCompatImageButton nextButton = view.findViewById(R.id.nextButton);
        TextView weekName = view.findViewById(R.id.week_name);

        previousButton.setOnClickListener(v -> {
            // Tính toán lại dữ liệu của tuần trước
            Calendar previousCalendar = Calendar.getInstance();
            previousCalendar.set(Calendar.WEEK_OF_YEAR, finalPreviousWeek);
            previousCalendar.set(Calendar.YEAR, finalPreviousYear);

            // Cập nhật biểu đồ với dữ liệu tuần trước
            updateBarChart(potholes, finalPreviousWeek, finalPreviousYear, "Last Week");
            weekName.setText("Last Week");
        });

        nextButton.setOnClickListener(v -> {


            currentCalendar.set(Calendar.WEEK_OF_YEAR, finalCurrentWeek);
            currentCalendar.set(Calendar.YEAR, finalCurrentYear);


            updateBarChart(potholes, finalCurrentWeek, finalCurrentYear, "This Week");
            weekName.setText("This Week");
        });


        // Initialize the charts
        potholesChart = view.findViewById(R.id.potholesChart);
        potholeStateChart = view.findViewById(R.id.potholeStateChart);
        weeklyChart = view.findViewById(R.id.weeklyChart);
        kilometresTextView = view.findViewById(R.id.kilometres_number);
        potholeNumberTextView = view.findViewById(R.id.potholes_number);
        hoursTextView = view.findViewById(R.id.hours_number);
        profileImage = view.findViewById(R.id.profileImage);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("USER_ID", null);









        loadProfilePicture(userId);


        return view;
    }



    private void loadProfilePicture(String userId) {
        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.getProfilePicture(userId);

        Log.d("Dashboard", "Loading profile picture for user: " + userId);

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
                                profileImage.setImageBitmap(bitmap);
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


    private void fetchPotholesData() {
        apiService.getPotholes().enqueue(new Callback<ListPotholeResponse>() {
            @Override
            public void onResponse(Call<ListPotholeResponse> call, Response<ListPotholeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Dashboard", "Response body: " + response.body());
                    potholes = response.body().getPotholes();  // Lấy danh sách potholes từ response
                    updateDashboardData();
                    setupPieChart();
                    setupLineChart();

                    // Lấy tuần hiện tại và năm hiện tại
                    Calendar currentCalendar = Calendar.getInstance();
                    int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
                    int currentYear = currentCalendar.get(Calendar.YEAR);


                    updateBarChart(potholes, currentWeek, currentYear, "This Week");
                } else {
                    Log.e("Dashboard", "Failed to fetch potholes");
                }
            }

            @Override
            public void onFailure(Call<ListPotholeResponse> call, Throwable t) {
                Log.e("Dashboard", "Error fetching potholes", t);
            }
        });
    }


    private void fetchJourneysData() {
        apiService.getJourneys().enqueue(new Callback<ListJourneyResponse>() {
            @Override
            public void onResponse(Call<ListJourneyResponse> call, Response<ListJourneyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Dashboard", "Response body: " + response.body());
                    journeys = response.body().getJourneys();  // Lấy danh sách journeys từ response
                    updateDashboardData();
                } else {
                    Log.e("Dashboard", "Failed to fetch journeys");
                }
            }

            @Override
            public void onFailure(Call<ListJourneyResponse> call, Throwable t) {
                Log.e("Dashboard", "Error fetching journeys", t);
            }
        });
    }


    private void updateDashboardData() {
        // Tính tổng quãng đường
        double totalDistance = 0;
        for (Journey journey : journeys) {
            totalDistance += journey.getDistance();
        }
        kilometresTextView.setText(String.format("%.2f", totalDistance));

        // Đếm số lượng ổ gà
        int potholeCount = potholes.size();
        potholeNumberTextView.setText(String.valueOf(potholeCount));

        // Tính tổng số giờ từ journeys
        double totalHours = calculateTotalHours(journeys);
        hoursTextView.setText(String.format("%.2f", totalHours));
    }

    private double calculateTotalHours(List<Journey> journeys) {
        double totalHours = 0;

        for (Journey journey : journeys) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date startTime = sdf.parse(journey.getStart_time());
                Date endTime = sdf.parse(journey.getEnd_time());

                if (startTime != null && endTime != null) {
                    // Tính thời gian chạy (giờ)
                    long durationInMillis = endTime.getTime() - startTime.getTime();
                    totalHours += (double) durationInMillis / (1000 * 60 * 60); // Chuyển đổi từ milliseconds sang giờ
                }
            } catch (Exception e) {
                Log.e("Dashboard", "Error parsing journey time", e);
            }
        }

        return totalHours;
    }



    private void setupPieChart() {
        int cautionCount = 0, warningCount = 0, dangerCount = 0;

        // Đếm số lượng pothole theo loại
        for (Pothole pothole : potholes) {
            switch (pothole.getType()) {
                case "Caution":
                    cautionCount++;
                    break;
                case "Warning":
                    warningCount++;
                    break;
                case "Danger":
                    dangerCount++;
                    break;
            }
        }

        // Chuẩn bị dữ liệu
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(cautionCount, "Caution"));
        entries.add(new PieEntry(warningCount, "Warning"));
        entries.add(new PieEntry(dangerCount, "Danger"));

        // Cấu hình DataSet
        PieDataSet dataSet = new PieDataSet(entries, "Pothole Types");
        dataSet.setColors(new int[]{
                Color.parseColor("#FFC107"), // Vàng
                Color.parseColor("#FF9800"), // Cam
                Color.parseColor("#F44336")  // Đỏ
        });
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);
        dataSet.setSliceSpace(2f); // Khoảng cách giữa các phần
        dataSet.setSelectionShift(10f); // Hiệu ứng khi chọn

        // Gán dữ liệu và cấu hình biểu đồ
        PieData data = new PieData(dataSet);
        potholesChart.setData(data);
        potholesChart.setUsePercentValues(true); // Hiển thị phần trăm
        potholesChart.setDrawHoleEnabled(true); // Lỗ ở giữa
        potholesChart.setHoleColor(Color.TRANSPARENT);
        potholesChart.setHoleRadius(40f);
        potholesChart.setTransparentCircleRadius(45f);
        potholesChart.setEntryLabelTextSize(12f);
        potholesChart.setEntryLabelColor(Color.BLACK);

        // Ẩn mô tả và thêm hiệu ứng
        potholesChart.getDescription().setEnabled(false);
        potholesChart.animateY(1000, Easing.EaseInOutCubic);

        // Thêm chú thích
        Legend legend = potholesChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(Color.BLACK);

        potholesChart.invalidate(); // Làm mới biểu đồ
    }


    private void setupLineChart() {
        // Dữ liệu cho từng loại
        LinkedHashMap<String, Float> cautionScores = new LinkedHashMap<>();
        LinkedHashMap<String, Float> warningScores = new LinkedHashMap<>();
        LinkedHashMap<String, Float> dangerScores = new LinkedHashMap<>();

        // Định dạng ngày (yyyy-MM-dd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Nhóm pothole theo ngày và loại
        for (Pothole pothole : potholes) {
            String date = dateFormat.format(pothole.getCreatedAt()); // Lấy ngày
            switch (pothole.getType()) {
                case "Caution":
                    cautionScores.put(date, cautionScores.getOrDefault(date, 0f) + 1f);
                    break;
                case "Warning":
                    warningScores.put(date, warningScores.getOrDefault(date, 0f) + 2f);
                    break;
                case "Danger":
                    dangerScores.put(date, dangerScores.getOrDefault(date, 0f) + 3f);
                    break;
            }
        }

        // Chuyển dữ liệu từng loại thành Entry
        ArrayList<Entry> cautionEntries = mapToEntries(cautionScores);
        ArrayList<Entry> warningEntries = mapToEntries(warningScores);
        ArrayList<Entry> dangerEntries = mapToEntries(dangerScores);

        // Tạo DataSet cho từng loại
        LineDataSet cautionDataSet = createLineDataSet(cautionEntries, "Caution", Color.parseColor("#FFC107"));
        LineDataSet warningDataSet = createLineDataSet(warningEntries, "Warning", Color.parseColor("#FF9800"));
        LineDataSet dangerDataSet = createLineDataSet(dangerEntries, "Danger", Color.parseColor("#F44336"));

        // Thêm tất cả DataSet vào LineData
        LineData lineData = new LineData(cautionDataSet, warningDataSet, dangerDataSet);

        // Cấu hình biểu đồ
        potholeStateChart.setData(lineData);
        potholeStateChart.getDescription().setEnabled(false);
        potholeStateChart.setDrawGridBackground(false);
        potholeStateChart.getXAxis().setDrawGridLines(false);
        potholeStateChart.getAxisLeft().setDrawGridLines(true);
        potholeStateChart.getAxisRight().setEnabled(false);
        potholeStateChart.animateX(1500, Easing.EaseInOutQuart);

        potholeStateChart.invalidate(); // Làm mới biểu đồ
    }

    // Hàm chuyển Map thành danh sách Entry
    private ArrayList<Entry> mapToEntries(LinkedHashMap<String, Float> scores) {
        ArrayList<Entry> entries = new ArrayList<>();
        int index = 1;
        for (Map.Entry<String, Float> entry : scores.entrySet()) {
            entries.add(new Entry(index++, entry.getValue()));
        }
        return entries;
    }

    // Hàm tạo LineDataSet
    private LineDataSet createLineDataSet(ArrayList<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color); // Màu của đường
        dataSet.setCircleColor(color); // Màu của vòng tròn
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Đường cong mượt
        return dataSet;
    }


    // Hàm hỗ trợ lấy màu cho từng điểm
    private List<Integer> getColors(ArrayList<Entry> entries) {
        List<Integer> colors = new ArrayList<>();
        for (Entry entry : entries) {
            colors.add((Integer) entry.getData());
        }
        return colors;
    }


    private void updateBarChart(List<Pothole> potholes, int week, int year, String weekLabel) {
        // Mảng lưu trữ số lượng pothole của từng ngày trong tuần (từ thứ 2 đến chủ nhật)
        int[] dailyPotholes = new int[7]; // 0: Monday, 1: Tuesday, ..., 6: Sunday
        int[] cautionCount = new int[7];
        int[] warningCount = new int[7];
        int[] dangerCount = new int[7];

        // Phân loại potholes theo ngày và loại
        for (Pothole pothole : potholes) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pothole.getCreatedAt());  // Giả sử Pothole có một thuộc tính `getDate()` trả về ngày xảy ra
            int potholeWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            int potholeYear = calendar.get(Calendar.YEAR);

            if (potholeYear == year && potholeWeek == week) {
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;  // Calendar.DAY_OF_WEEK trả về từ 1 (Sunday) tới 7 (Saturday), ta cần trừ đi 1 để vào đúng chỉ số của mảng

                // Phân loại pothole theo trạng thái
                switch (pothole.getType()) {
                    case "Caution":
                        cautionCount[dayOfWeek]++;
                        break;
                    case "Warning":
                        warningCount[dayOfWeek]++;
                        break;
                    case "Danger":
                        dangerCount[dayOfWeek]++;
                        break;
                }
            }
        }

        // Chuẩn bị dữ liệu cho biểu đồ
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            // Tạo một đối tượng BarEntry cho mỗi ngày trong tuần
            float caution = cautionCount[i];
            float warning = warningCount[i];
            float danger = dangerCount[i];
            // Các cột sẽ chồng lên nhau, vì vậy cần có 3 giá trị cho mỗi ngày
            entries.add(new BarEntry(i, new float[]{caution, warning, danger}));
        }

        // Cấu hình dữ liệu và màu sắc cho biểu đồ
        BarDataSet dataSet = new BarDataSet(entries, "Potholes " + weekLabel);
        dataSet.setStackLabels(new String[]{"Caution", "Warning", "Danger"});
        dataSet.setColors(new int[]{
                Color.parseColor("#FFC107"), // Màu vàng cho Caution
                Color.parseColor("#FF9800"), // Màu cam cho Warning
                Color.parseColor("#F44336")  // Màu đỏ cho Danger
        });

        // Cấu hình biểu đồ
        BarData data = new BarData(dataSet);
        weeklyChart.setData(data);

        // Cấu hình hiển thị và vẽ biểu đồ
        XAxis xAxis = weeklyChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        weeklyChart.getDescription().setEnabled(false); // Tắt mô tả
        weeklyChart.animateY(1000, Easing.EaseInOutCubic);
        weeklyChart.invalidate(); // Làm mới biểu đồ
    }

    private int getPotholesByWeek(List<Pothole> potholes, int week, int year) {
        int count = 0;

        try {
            // Kiểm tra số lượng potholes
            Log.e("Potholes", "Number of potholes: " + potholes.size());




            for (Pothole pothole : potholes) {
                Date potholeDate = pothole.getCreatedAt();
                Log.e("Pothole", "CreatedAt: " + pothole.getCreatedAt());

                if (potholeDate != null) {
                    Calendar potholeCalendar = Calendar.getInstance();
                    potholeCalendar.setTime(potholeDate);

                    int potholeWeek = potholeCalendar.get(Calendar.WEEK_OF_YEAR);
                    int potholeYear = potholeCalendar.get(Calendar.YEAR);

                    // Kiểm tra nếu tuần và năm của pothole là tuần và năm trước
                    if (potholeWeek == week && potholeYear == year) {
                        count++;
                    }
                }
            }

            Log.e("PotholesCount", "Number of potholes in current week: " + count);
        } catch (Exception e) {
            Log.e("Dashboard", "Error calculating potholes by week", e);
        }
        return count;
    }
}
