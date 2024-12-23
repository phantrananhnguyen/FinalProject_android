package com.example.finalproject_android.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.finalproject_android.models.Journey;
import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JourneyService extends Service {
    private static final String CHANNEL_ID = "journey_service_channel";


    private final IBinder binder = new LocalBinder();
    private LocationManager locationManager;
    private LocationListener locationListener;
    ApiService apiService;

    private double totalDistance = 0.0; // Tổng khoảng cách
    private Location lastLocation; // Vị trí cuối cùng
    private double startLat, startLong, endLat, endLong; // Lưu lat/long start và end
    private long startTime, endTime; // Lưu thời gian start và end
    private boolean isTracking = false; // Cờ để kiểm tra trạng thái tracking
    UserSession userSession;
    String uname;
    public class LocalBinder extends Binder {
        public JourneyService getService() {
            return JourneyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Journey Service Channel";
            String description = "Channel for journey service notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        apiService = ApiClient.getClient(this).create(ApiService.class);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d("JourneyService", "Service Created");
        userSession = new UserSession(this);
        if(userSession != null) uname = userSession.getUsername();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location == null || !location.hasAccuracy()) {
                    Log.w("JourneyService", "Location update is null or does not have accuracy");
                    return;
                }

                if (location.getAccuracy() > 50) { // Độ chính xác trên 50 mét thì bỏ qua
                    Log.w("JourneyService", "Ignored location update due to low accuracy");
                    return;
                }

                // Log latitude, longitude và thời gian mỗi khi có vị trí mới
                Log.d("JourneyService", "Vị trí cập nhật: Lat = " + location.getLatitude() + ", Long = " + location.getLongitude());
                Log.d("JourneyService", "Thời gian cập nhật: " + convertMillisToDate(System.currentTimeMillis()));

                handleNewLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("JourneyService", "Service Started");
        // Create a notification for the foreground service
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Journey Service")
                .setContentText("Tracking your journey...")
                .build();

        // Start the service in the foreground
        startForeground(1, notification);

        startTracking();  // Add this line to start tracking


        // Return a value that keeps the service running
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("JourneyService", "Service Destroyed");

        stopTracking();  // Dừng theo dõi vị trí
        stopForeground(true); // Hủy notification của foreground service
        stopSelf();  // Dừng service
    }



    private void startTracking() {
        if (!isTracking) {
            isTracking = true;
            startTime = System.currentTimeMillis(); // Ghi lại thời gian bắt đầu
            Log.d("JourneyService", "Bắt đầu theo dõi hành trình. Thời gian bắt đầu: " + convertMillisToDate(startTime));

            try {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    // Lấy vị trí cuối cùng nếu có
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        startLat = lastKnownLocation.getLatitude();
                        startLong = lastKnownLocation.getLongitude();
                        lastLocation = lastKnownLocation;
                        Log.d("JourneyService", "Lấy vị trí bắt đầu từ lastKnownLocation: Lat = " + startLat + ", Long = " + startLong);
                    } else {
                        Log.d("JourneyService", "Không thể lấy vị trí bắt đầu từ lastKnownLocation");
                    }

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

                } else {
                    Log.e("JourneyService", "Permission not granted for GPS");
                }
            } catch (SecurityException e) {
                Log.e("JourneyService", "Permission not granted for GPS", e);
            }
        }
    }

    private void stopTracking() {
        if (isTracking) {
            isTracking = false;
            endTime = System.currentTimeMillis(); // Ghi lại thời gian kết thúc
            Log.d("JourneyService", "Hành trình kết thúc. Thời gian kết thúc: " + convertMillisToDate(endTime));

            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                Log.e("JourneyService", "Failed to remove location updates", e);
            }

            // Lưu thông tin endLat và endLong
            if (lastLocation != null) {
                endLat = lastLocation.getLatitude();
                endLong = lastLocation.getLongitude();
            }

            sendJourneyDataToServer();

            Log.d("JourneyService", "Journey ended. Total distance: " + totalDistance + " meters");

            // Reset dữ liệu
            totalDistance = 0.0;
            lastLocation = null;
            startLat = 0.0;
            startLong = 0.0;
            endLat = 0.0;
            endLong = 0.0;
        }
    }

    private void handleNewLocation(Location location) {
        if (location == null) return;

        if (lastLocation != null) {
            double distance = calculateHaversineDistance(
                    lastLocation.getLatitude(), lastLocation.getLongitude(),
                    location.getLatitude(), location.getLongitude()
            );
            totalDistance += distance; // Cộng dồn khoảng cách
            Log.d("JourneyService", "Distance added: " + distance + " meters. Total: " + totalDistance);
        } else {
            // Lưu tọa độ start nếu là lần đầu
            startLat = location.getLatitude();
            startLong = location.getLongitude();

        }

        lastLocation = location; // Cập nhật vị trí cuối cùng
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371e3; // Bán kính Trái Đất tính bằng mét
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Khoảng cách theo mét
    }

    private String convertMillisToDate(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(millis));
    }

    public void sendJourneyDataToServer() {
        String startDateTime = convertMillisToDate(startTime); //"yyyy-MM-dd HH:mm:ss"
        String endDateTime = convertMillisToDate(endTime); //"yyyy-MM-dd HH:mm:ss"

        Journey journey = new Journey();
        journey.setUsername(uname);
        journey.setStart_time(startDateTime);
        journey.setEnd_time(endDateTime);
        journey.setStart_latitude(startLat);
        journey.setStart_longitude(startLong);
        journey.setEnd_latitude(endLat);
        journey.setEnd_longitude(endLong);
        journey.setDistance(totalDistance);

        Log.d("JourneyService", "Dữ liệu hành trình sắp gửi: ");
        Log.d("JourneyService", "Bắt đầu: " + startLat + ", " + startLong + " vào " + startDateTime);
        Log.d("JourneyService", "Kết thúc: " + endLat + ", " + endLong + " vào " + endDateTime);
        Log.d("JourneyService", "Khoảng cách: " + totalDistance + " mét");


        Call<Void> call = apiService.sendJourney(journey);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("JourneyService", "Journey data sent successfully!");
                } else {
                    Log.e("JourneyService", "Failed to send journey: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("JourneyService", "Error sending journey data", t);
            }
        });
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
