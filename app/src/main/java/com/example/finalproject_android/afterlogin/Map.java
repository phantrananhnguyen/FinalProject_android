package com.example.finalproject_android.afterlogin;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject_android.R;
import com.example.finalproject_android.models.Potholemodel;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import com.example.finalproject_android.services.JourneyService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Map extends Fragment implements GoogleMap.OnMapClickListener, OnMapReadyCallback, SensorEventListener, GoogleMap.OnMarkerClickListener{

    private static final String NAME = "Anh Nguyen";
    private GoogleMap mMap;
    boolean isCameraAdjusting = false;
    private LatLng currentLatLng;
    private boolean isBumpDetected = false;
    private ApiService apiService;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    ImageButton current_loca;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient().create(ApiService.class);
        Log.e("API Error", "aaaaa");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        sensorManager = (SensorManager) requireActivity().getSystemService(getContext().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        //fetchDataFromServer();
    }
    private void fetchDataFromServer() {
        apiService.getPotholeData().enqueue(new Callback<List<Potholemodel>>() {
            @Override
            public void onResponse(Call<List<Potholemodel>> call, Response<List<Potholemodel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Potholemodel> potholeModels = response.body();
                    for (Potholemodel pothole : potholeModels) {
                        addBumpMarker(pothole);
                    }
                } else {
                    Log.e("API Error", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Potholemodel>> call, Throwable t) {
                Log.e("Network Error", t.getMessage());
            }
        });
    }
    private void getCurrentLocation() {
        // Kiểm tra quyền truy cập vị trí
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Nếu quyền chưa được cấp, yêu cầu quyền từ người dùng
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Nếu quyền đã được cấp, lấy vị trí hiện tại
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        } else {
                            Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu quyền đã được cấp, gọi lại để lấy vị trí
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        current_loca = view.findViewById(R.id.current_loca);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectBump(sensorEvent.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private Marker previousMarker;
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        try {
            // Format the latitude and longitude
            DecimalFormat decimalFormat = new DecimalFormat("#.######");
            String coordsText = decimalFormat.format(latLng.latitude) +
                    ", " + decimalFormat.format(latLng.longitude);

            // Show coordinates in the bottom sheet
            showBottomSheetDialog(coordsText);

            // Remove the previous marker if it exists
            if (previousMarker != null) {
                previousMarker.remove();
            }

            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.location);
            Bitmap originalBitmap = bitmapDrawable.getBitmap();

// Calculate the new dimensions (70% of original size)
            int width = (int) (originalBitmap.getWidth() * 0.9);
            int height = (int) (originalBitmap.getHeight() * 0.9);

// Scale the bitmap
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);

            previousMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));

        } catch (Exception e) {
            Log.e("onMapClickError", "Error in onMapClick: ", e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void showBottomSheetDialog(String text) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);

        // Lấy reference tới CardView và nội dung ẩn
        CardView cardView = bottomSheetDialog.findViewById(R.id.cardview);
        if (cardView != null) {
            BottomSheetBehavior<CardView> behavior = BottomSheetBehavior.from(cardView);
            behavior.setPeekHeight(200); // Đặt chiều cao peek height
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Bắt đầu ở trạng thái collapsed
        }

        // Lấy TextView tọa độ và thiết lập văn bản
        TextView coordinates = bottomSheetDialog.findViewById(R.id.coordinates);
        if (coordinates != null) {
            coordinates.setText(text);
        }

        // Hiện Bottom Sheet Dialog
        bottomSheetDialog.show();
    }
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Potholemodel pothole = (Potholemodel) marker.getTag();
        if(pothole != null){
            showInfoPopup(pothole);
        }
        return true;
    }
    private void showInfoPopup(Potholemodel pothole) {
        LayoutInflater inflater = getLayoutInflater();


        View dialogView = inflater.inflate(R.layout.pothole_infor, null);


        String type = pothole.getType();
        // Lấy các view từ layout tùy chỉnh
        TextView tvAuthor = dialogView.findViewById(R.id.name);
        TextView tvLocation = dialogView.findViewById(R.id.location);
        TextView tvType = dialogView.findViewById(R.id.type);

        ImageButton btnClose = dialogView.findViewById(R.id.close_button);
        Log.e("MarkerClick", "Pothole is null");
        ImageView imageView = dialogView.findViewById(R.id.image_type);

        DecimalFormat decimalFormat = new DecimalFormat("#.######");
        String coordsText = decimalFormat.format(pothole.getLatitude()) +
                ", " + decimalFormat.format(pothole.getLongitude());
        tvLocation.setText(coordsText);
        tvType.setText(type);
        tvAuthor.setText(pothole.getAuthor());

        imageView.setImageBitmap(getImagefromType(type));

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Đặt nền trong suốt
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Đóng dialog khi nhấn nút
            }
        });

        // Hiện dialog
        dialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.mMap = map;

        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(10.895000, 106.825000))  // Expanded Northern point
                .include(new LatLng(10.855000, 106.770000))  // Expanded Southern point
                .build(); // Southern point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 15.0f));
        mMap.setLatLngBoundsForCameraTarget(latLngBounds);
        double minZoomLevel = 15.0;
        mMap.setMinZoomPreference((float) minZoomLevel);

        double maxZoomLevel = 18.5;
        mMap.setMaxZoomPreference((float) maxZoomLevel);
        mMap.setOnCameraMoveListener(() -> {
            if (!isCameraAdjusting && !latLngBounds.contains(mMap.getCameraPosition().target)) {
                isCameraAdjusting = true;

                double latitude = Math.max(latLngBounds.southwest.latitude,
                        Math.min(mMap.getCameraPosition().target.latitude, latLngBounds.northeast.latitude));
                double longitude = Math.max(latLngBounds.southwest.longitude,
                        Math.min(mMap.getCameraPosition().target.longitude, latLngBounds.northeast.longitude));
                LatLng boundedLatLng = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(boundedLatLng));
            }
        });

        mMap.setOnCameraIdleListener(() -> isCameraAdjusting = false);

        // Lắng nghe sự kiện nhấn vào nút
        current_loca.setOnClickListener(v -> {
            // Kiểm tra quyền và vị trí hiện tại
            if (mMap.isMyLocationEnabled() && mMap.getMyLocation() != null) {
                // Di chuyển camera đến vị trí hiện tại
                LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f));
            } else {
                Toast.makeText(getContext(), "Không thể xác định vị trí hiện tại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Bitmap getImagefromType(String type){
        int iconResource;
        switch (type) {
            case "Danger":
                iconResource = R.drawable.danger_pot;
                break;
            case "Warning":
                iconResource = R.drawable.warning_pot;
                break;
            case "Caution":
                iconResource = R.drawable.caution_pot;
                break;
            default:
                iconResource = R.drawable.location;
                break;
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(iconResource);
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 50, 50, false);
        return bitmap;
    }
    private long lastBumpTime = 0;
    private static final long BUMP_COOLDOWN_TIME = 2500; // 5 seconds
    List<Float> accelerationBuffer = new ArrayList<>();
    private static final int BUFFER_SIZE = 10;
    private void detectBump(float[] acceleration) {
        float thresholdLight = 14.5f; // Gia tốc cho mức độ 1
        float thresholdMedium = 16.0f; // Gia tốc cho mức độ 2
        float thresholdHeavy = 18.5f; // Gia tốc cho mức độ 3

        String type;

        long currentTime = System.currentTimeMillis();
        String author = NAME;
        accelerationBuffer.add(Math.abs(acceleration[2]));
        if (accelerationBuffer.size() > BUFFER_SIZE) {
            accelerationBuffer.remove(0);
        }

        // Tính giá trị trung bình của gia tốc
        float averageAcceleration = 0;
        for (Float value : accelerationBuffer) {
            averageAcceleration += value;
        }
        averageAcceleration /= accelerationBuffer.size();
        if (currentLatLng != null && (currentTime - lastBumpTime > BUMP_COOLDOWN_TIME)) {
            if (averageAcceleration >= thresholdHeavy) {
                type = "Danger";
                isBumpDetected = true;
                lastBumpTime = currentTime;
                showConfirmationDialog(type, currentLatLng);
            } else if (averageAcceleration >= thresholdMedium && averageAcceleration < thresholdHeavy) {
                type = "Warning";
                isBumpDetected = true;
                lastBumpTime = currentTime;
                showConfirmationDialog(type, currentLatLng);
            } else if (averageAcceleration >= thresholdLight && averageAcceleration < thresholdMedium) {
                type = "Caution";
                isBumpDetected = true;
                lastBumpTime = currentTime;
                showConfirmationDialog(type, currentLatLng);
            }
        }
        if (averageAcceleration <= thresholdLight) {
            isBumpDetected = false;
        }
    }
    private void showConfirmationDialog(String type, LatLng location) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pothole_confirm, null);

        // Lấy các view từ layout tùy chỉnh
        TextView tvHi_name = dialogView.findViewById(R.id.hi_name);
        TextView tvConfirm_type = dialogView.findViewById(R.id.confirm_type);
        ImageView imageView = dialogView.findViewById(R.id.image_confirm_type);

        Button btnConfirm_no = dialogView.findViewById(R.id.confirm_no);
        Button btnConfirm_yes = dialogView.findViewById(R.id.confirm_yes);

        tvHi_name.setText("Hi " + NAME + ", ");
        tvConfirm_type.setText("Type: " + type);
        imageView.setImageBitmap(getImagefromType(type));

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Đặt nền trong suốt
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        btnConfirm_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Potholemodel pothole = new Potholemodel(location.latitude, location.longitude, type, "YourName");
                sendBumpDataToServer(pothole);
                addBumpMarker(pothole);
                dialog.dismiss();
            }
        });
        btnConfirm_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private void addBumpMarker(Potholemodel pothole) {
        int iconResource;  // Declare iconResource variable
        String type = pothole.getType();

        LatLng position = new LatLng(pothole.getLatitude(), pothole.getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(getImagefromType(type)))
                .title("Bump Level: " + type));

        // Lưu đối tượng pothole vào marker
        marker.setTag(pothole);
    }
    private void sendBumpDataToServer(Potholemodel potholemodel) {
        apiService.sendBumpData(potholemodel).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Bump data sent to server!", Toast.LENGTH_SHORT).show();
                } else {
                    // Log response details
                    Log.e("API Error", "Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(getContext(), "Failed to send bump data. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Network Error", t.getMessage(), t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        startJourneyService(); //Quyen
        // Register the sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void startJourneyService() {//Quyen
        Intent serviceIntent = new Intent(requireContext(), JourneyService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(requireContext(), serviceIntent);
        } else {
            requireContext().startService(serviceIntent);
        }



            Log.d("MapFragment", "JourneyService started");

    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the sensor listener
        stopJourneyService(); //Quyen
        sensorManager.unregisterListener(this);
    }

    private void stopJourneyService() {
        Intent serviceIntent = new Intent(requireContext(), JourneyService.class);

        // Đảm bảo rằng bạn đang dừng service, không phải bắt đầu lại
        requireContext().stopService(serviceIntent);
        Log.d("MapFragment", "JourneyService đang bị dừng nha");
    }


}