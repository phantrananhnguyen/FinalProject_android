package com.example.finalproject_android.afterlogin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject_android.R;
import com.example.finalproject_android.models.Feature;
import com.example.finalproject_android.models.Places;
import com.example.finalproject_android.models.Potholemodel;
import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.map.util.MapViewProjection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map extends Fragment {
    private static String NAME;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private MapView mapView;
    private BoundingBox boundingBox;
    private FusedLocationProviderClient fusedLocationClient;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LatLong currentLatLng;
    private ApiService apiService;
    private ImageButton current_back, alert, route;
    private List<Potholemodel> potholesList = new ArrayList<>();
    private Marker currentLocationMarker;
    private Feature feature;
    private EditText search_input;
    private BottomSheetDialog bottomSheetDialog;
    private LatLong lastPotholeLocation = null;
    private List<Potholemodel> potholesOnRoute = new ArrayList<>();

    UserSession userSession;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (feature != null) {
            outState.putSerializable("feature", feature);
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            feature = (Feature) savedInstanceState.getSerializable("feature");
        } else if (getArguments() != null) {
            feature = (Feature) getArguments().getSerializable("feature");
        }
        // Khởi tạo ApiService và Sensor
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        userSession = new UserSession(getContext());
        if (userSession != null) {
            NAME = userSession.getUsername();
        } else {
            Log.e("MapFragment", "UserSession is null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        current_back = view.findViewById(R.id.current_loca);
        alert = view.findViewById(R.id.alert);
        search_input = view.findViewById(R.id.autocomplete_query);
        route = view.findViewById(R.id.routeMap);
        search_input.setOnClickListener(view1 -> startActivity(new Intent(getContext(), Search.class)));

        alert.setImageDrawable(getResizedDrawable(R.drawable.bell, 50, 50));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMap();
        current_back.setOnClickListener(view1 -> {
            if (currentLatLng == null) {
                Toast.makeText(requireContext(), "Current location not available", Toast.LENGTH_SHORT).show();
                return;
            }
            mapView.setCenter(currentLatLng);
        });
        if (feature != null) {
            Potholemodel potholemodel = new Potholemodel(feature.getLat(), feature.getLon(), "search", "");
            showSearchDestination(feature);
            addBumpMarker(potholemodel);
            LatLong latLong = new LatLong(feature.getLat(), feature.getLon());
            mapView.setCenter(latLong);
        }
        route.setOnClickListener(view1 -> {
            if (currentLatLng == null) {
                Toast.makeText(requireContext(), "Current location not available", Toast.LENGTH_SHORT).show();
                return;
            }

            if (feature != null) {
                LatLong latLong = new LatLong(feature.getLat(), feature.getLon());
                performRoute(latLong);
            } else {
                Toast.makeText(requireContext(), "No destination selected", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setOnClickListener(view1 -> toggleBottomSheetDialog());
        fetchDataFromServer();
        startLocationUpdates();

    }
    public void updateFeature(Feature newFeature) {
        if (newFeature != null) {
            this.feature = newFeature;
            // Update the map view based on the new feature data if necessary
            if (mapView != null && feature != null) {
                LatLong center = new LatLong(feature.getLat(), feature.getLon());
                mapView.setCenter(center);
            }
        }
    }
    public void showSearchDestination(Feature feature) {
        if (feature == null) return; // Ensure the feature is not null before proceeding
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.search_infor, null);
        TextView tvName = dialogView.findViewById(R.id.name);
        TextView tvAmenity = dialogView.findViewById(R.id.amenity);
        TextView tvCoordinates = dialogView.findViewById(R.id.coordinates);
        BottomSheetDialog newbottomSheetDialog = new BottomSheetDialog(getContext());
        newbottomSheetDialog.setContentView(dialogView);
        newbottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        tvName.setText(feature.getName());
        tvAmenity.setText(feature.getAmenity());
        tvCoordinates.setText(String.format("%s, %s", feature.getLon(), feature.getLat()));
                newbottomSheetDialog.show();
    }

    private void performRoute(LatLong latLong) {
        String start = currentLatLng.longitude + "," + currentLatLng.latitude;
        String end = latLong.longitude + "," + latLong.latitude;
        apiService.route(start, end).enqueue(new Callback<Places>() {
            @Override
            public void onResponse(Call<Places> call, Response<Places> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Places places = response.body();
                    // Tạo danh sách GeoPoint từ danh sách LatLong
                    List<LatLong> routePoints = new ArrayList<>();
                    for (LatLong latlon : places.getCoordinates()) {
                        routePoints.add(new LatLong(latlon.getLatitude(), latlon.getLongitude()));
                        Log.e("mapsearch", latlon.toString());
                    }
                    filterPotholesOnRoute(routePoints);

                    startNavigation(routePoints);

                    drawRouteOnMap(routePoints);
                }
            }
            @Override
            public void onFailure(Call<Places> call, Throwable t) {
                Log.e("map", "Failed to fetch route");
            }
        });
    }

    private Polyline currentRoutePolyline;
    private void drawRouteOnMap(List<LatLong> latLongList) {
        if (mapView == null) return;
        if (currentRoutePolyline != null) {
            mapView.getLayerManager().getLayers().remove(currentRoutePolyline);
        }
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(Color.parseColor("#87CEEB"));
        paint.setStrokeWidth(15f);
        paint.setStyle(Style.STROKE);
        currentRoutePolyline = new Polyline(paint, AndroidGraphicFactory.INSTANCE);
        currentRoutePolyline.getLatLongs().addAll(latLongList);
        mapView.getLayerManager().getLayers().add(currentRoutePolyline);
    }

    private void startNavigation(List<LatLong> routePoints) {
        filterPotholesOnRoute(routePoints);
        startLocationUpdates();
    }

    private void setupMap() {
        AndroidGraphicFactory.createInstance(getActivity().getApplication());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        File mapFile = new File(getContext().getFilesDir(), "langdaihoc.map");
        if (mapFile.exists()) {
            setupMapWithFile(mapFile);
        } else {
            downloadMapAndSetup();
        }
    }
    private void setupMapWithFile(File mapFile) {
        TileCache tileCache = AndroidUtil.createTileCache(getActivity(), "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(mapFile),
                mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        LatLong center = (feature != null) ? new LatLong(feature.getLat(), feature.getLon()) : new LatLong(10.882, 106.794);
        mapView.post(() -> {
            mapView.setCenter(center);
            mapView.setZoomLevel((byte) 17);
        });
        LatLong topLeft = new LatLong(10.903 - 0.02, 106.743 + 0.008);
        LatLong bottomRight = new LatLong(10.845 + 0.02, 106.834 - 0.008);
        boundingBox = new BoundingBox(bottomRight.latitude, topLeft.longitude, topLeft.latitude, bottomRight.longitude);
        mapView.getModel().mapViewPosition.addObserver(this::limitMapPanning);
    }

    private void downloadMapAndSetup() {
        String userEmail = userSession.getUserEmail();
        apiService.downloadMap(userEmail).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lưu bản đồ vào thư mục ứng dụng
                    boolean isSaved = saveMapFile(response.body());
                    if (isSaved) {
                        // Sau khi lưu thành công, thiết lập bản đồ
                        File downloadedMapFile = new File(getContext().getFilesDir(), "langdaihoc.map");
                        setupMapWithFile(downloadedMapFile);
                        Toast.makeText(getContext(), "Map downloaded and set up successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to save map.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error downloading map.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean saveMapFile(ResponseBody responseBody) {
        try (InputStream inputStream = responseBody.byteStream();
             OutputStream outputStream = new FileOutputStream(new File(getContext().getFilesDir(), "langdaihoc.map"))) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void toggleBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setContentView(R.layout.pothole_alert);
            bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            ImageButton btnClose = bottomSheetDialog.findViewById(R.id.alert_close);
            if (btnClose != null) {
                btnClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
            }
        }
        if (bottomSheetDialog.isShowing()) {
            alert.setImageDrawable(getResizedDrawable(R.drawable.unbell, 50, 50));
            bottomSheetDialog.dismiss();
        } else {
            alert.setImageDrawable(getResizedDrawable(R.drawable.bell, 50, 50));
            bottomSheetDialog.show();
        }
        alert.invalidate();
        alert.requestLayout();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void filterPotholesOnRoute(List<LatLong> routePoints) {
        final double ROUTE_THRESHOLD = 20.0;
        potholesOnRoute.clear();
        for (Potholemodel pothole : potholesList) {
            LatLong potholeLocation = new LatLong(pothole.getLatitude(), pothole.getLongitude());
            for (LatLong routePoint : routePoints) {
                double distance = calculateDistance(routePoint, potholeLocation);
                if (distance <= ROUTE_THRESHOLD) {
                    potholesOnRoute.add(pothole);
                    break;
                }
            }
        }
    }


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) return;
            for (Location location : locationResult.getLocations()) {
                currentLatLng = new LatLong(location.getLatitude(), location.getLongitude());
                updateCurrentLocationMarker();
                checkProximityToPotholes(currentLatLng);
            }
        }
    };
    private void checkProximityToPotholes(LatLong currentLocation) {
        if (potholesOnRoute != null && !potholesOnRoute.isEmpty()) {
            for (Potholemodel pothole : potholesOnRoute) {
                LatLong potholeLocation = new LatLong(pothole.getLatitude(), pothole.getLongitude());
                double distance = calculateDistance(currentLocation, potholeLocation);

                if (distance <= 50) {
                    // Check if this pothole's location is different from the last shown one
                    if (lastPotholeLocation == null ||
                            lastPotholeLocation.getLatitude() != pothole.getLatitude() ||
                            lastPotholeLocation.getLongitude() != pothole.getLongitude()) {

                        int distanceInt = (int) Math.round(distance);
                        showProximityNotification(pothole, distanceInt);
                        lastPotholeLocation = potholeLocation;  // Update the last shown location
                    }
                    break;
                }
            }
        }
    }
    private void showProximityNotification(Potholemodel pothole, double distance) {
        if (pothole == null) return; // Bảo vệ khi pothole bị null

        if (bottomSheetDialog == null) {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.pothole_alert, null);

            TextView tvDistance = dialogView.findViewById(R.id.alert_distance);
            TextView tvLocation = dialogView.findViewById(R.id.alert_location);
            TextView tvType = dialogView.findViewById(R.id.alert_type);
            ImageButton btnClose = dialogView.findViewById(R.id.alert_close);
            ImageView imageView = dialogView.findViewById(R.id.image_type);

            bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            btnClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
        }

        TextView tvDistance = bottomSheetDialog.findViewById(R.id.alert_distance);
        TextView tvLocation = bottomSheetDialog.findViewById(R.id.alert_location);
        TextView tvType = bottomSheetDialog.findViewById(R.id.alert_type);
        ImageView imageView = bottomSheetDialog.findViewById(R.id.image_type);
        if (tvDistance != null) {
            int roundedDistance = (int) Math.round(distance);
            tvDistance.setText(String.format("%d meters", roundedDistance));
        }
        if (tvLocation != null) {
            tvLocation.setText(String.format("%s, %s", pothole.getLatitude(), pothole.getLongitude()));
        }
        if (tvType != null) {
            tvType.setText(pothole.getType());
        }
        if (imageView != null) {
            Drawable resizedMarkerDrawable = getResizedDrawable(getImagefromType(pothole.getType()), 30, 30);
            imageView.setImageDrawable(resizedMarkerDrawable);
        }

        if (!bottomSheetDialog.isShowing() && feature == null) {
            bottomSheetDialog.show();
        }
    }

    private double calculateDistance(LatLong point1, LatLong point2) {
        if (point1 == null || point2 == null) return 0; // Bảo vệ khi tọa độ bị null
        final double R = 6371.0088; // Bán kính Trái Đất (km)
        double lat1 = point1.latitude;
        double lon1 = point1.longitude;
        double lat2 = point2.latitude;
        double lon2 = point2.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Khoảng cách tính bằng km
        return distance * 1000;  // Chuyển đổi sang mét
    }

    private void updateCurrentLocationMarker() {
        if (currentLocationMarker != null) {
            mapView.getLayerManager().getLayers().remove(currentLocationMarker);
        }
        Drawable resizedMarkerDrawable = getResizedDrawable(getImagefromType("current"), 30, 30);
        Bitmap markerBitmap = AndroidGraphicFactory.convertToBitmap(resizedMarkerDrawable);
        currentLocationMarker = new Marker(currentLatLng, markerBitmap, 0, -markerBitmap.getHeight() / 2);
        mapView.getLayerManager().getLayers().add(currentLocationMarker);
    }

    private void fetchDataFromServer() {
        apiService.getPotholeData().enqueue(new Callback<List<Potholemodel>>() {
            @Override
            public void onResponse(Call<List<Potholemodel>> call, Response<List<Potholemodel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    potholesList.clear();
                    potholesList.addAll(response.body());
                    for (Potholemodel pothole : potholesList) {
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

    private final Set<String> markerPositions = new HashSet<>(); // Lưu vị trí marker đã thêm

    @SuppressLint("ClickableViewAccessibility")
    private void addBumpMarker(Potholemodel potholemodel) {
        LatLong location = new LatLong(potholemodel.getLatitude(), potholemodel.getLongitude());
        Drawable resizedMarkerDrawable = getResizedDrawable(getImagefromType(potholemodel.getType()), 40, 40);
        Bitmap markerBitmap = AndroidGraphicFactory.convertToBitmap(resizedMarkerDrawable);

        Marker marker = new Marker(location, markerBitmap, 0, -markerBitmap.getHeight() / 2);

        if (!isMarkerExist(marker)) {
            mapView.getLayerManager().getLayers().add(marker);
        }

        mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int touchRadius = 50; // Bán kính chạm (pixel)
                float x = event.getX();
                float y = event.getY();
                MapViewProjection projection = mapView.getMapViewProjection();
                Point markerScreenPoint = projection.toPixels(location);
                float distance = (float) Math.sqrt(Math.pow(x - markerScreenPoint.x, 2) + Math.pow(y - markerScreenPoint.y, 2));
                if (distance <= touchRadius) {
                    showInfoPopup(potholemodel); // Hiển thị thông tin popup
                    return true;
                }
            }
            return false;
        });
    }


    // Hàm kiểm tra sự tồn tại của marker
    private boolean isMarkerExist(Marker newMarker) {
        for (Layer layer : mapView.getLayerManager().getLayers()) {
            if (layer instanceof Marker) {
                Marker existingMarker = (Marker) layer;
                if (existingMarker.getPosition().equals(newMarker.getPosition())) {
                    return true;  // Marker đã tồn tại
                }
            }
        }
        return false;
    }


    private void showInfoPopup(Potholemodel pothole) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pothole_infor, null);

        TextView tvAuthor = dialogView.findViewById(R.id.name);
        TextView tvLocation = dialogView.findViewById(R.id.location);
        TextView tvType = dialogView.findViewById(R.id.type);
        ImageButton btnClose = dialogView.findViewById(R.id.close_button);
        ImageView imageView = dialogView.findViewById(R.id.image_type);

        tvLocation.setText(String.format("%s, %s", pothole.getLatitude(), pothole.getLongitude()));
        tvType.setText(pothole.getType());
        tvAuthor.setText(pothole.getAuthor());
        imageView.setImageDrawable(getResizedDrawable(getImagefromType(pothole.getType()), 40, 40));

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }



    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(accelerometerListener);
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                detectBump(event.values);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private long lastAlertTime = 0;  // Thời gian hiển thị thông báo lần cuối

    private void detectBump(float[] accelerationValues) {
        float thresholdLight = 18.5f;
        float thresholdMedium = 22.0f;
        float thresholdHeavy = 28.5f;
        String type;
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastAlertTime;  // Tính thời gian chênh lệch từ lần thông báo cuối

        float magnitude = (float) Math.sqrt(accelerationValues[0] * accelerationValues[0] +
                accelerationValues[1] * accelerationValues[1] +
                accelerationValues[2] * accelerationValues[2]);

        if (currentLatLng != null) {
            if (timeDifference > 1000) {
                if (magnitude >= thresholdHeavy) {
                    type = "Danger";
                    showConfirmationDialog(type, currentLatLng);
                    lastAlertTime = currentTime;  // Cập nhật thời gian khi thông báo được hiển thị
                } else if (magnitude >= thresholdMedium) {
                    type = "Warning";
                    showConfirmationDialog(type, currentLatLng);
                    lastAlertTime = currentTime;
                } else if (magnitude >= thresholdLight) {
                    type = "Caution";
                    showConfirmationDialog(type, currentLatLng);
                    lastAlertTime = currentTime;
                }
            }
        }
    }


    private void showConfirmationDialog(String type, LatLong location) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pothole_confirm, null);
        Spinner spinner = dialogView.findViewById(R.id.type_spin);
        ImageView imageView = dialogView.findViewById(R.id.image_confirm_type);
        Button btnConfirm_no = dialogView.findViewById(R.id.confirm_no);
        Button btnConfirm_yes = dialogView.findViewById(R.id.confirm_yes);
        String[] potholeTypes = getResources().getStringArray(R.array.pothole_types);
        int defaultPosition = java.util.Arrays.asList(potholeTypes).indexOf(type);
        if (defaultPosition != -1) {
            spinner.setSelection(defaultPosition);
        }
        imageView.setImageDrawable(getResizedDrawable(getImagefromType(type), 40, 40));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = potholeTypes[position];
                imageView.setImageDrawable(getResizedDrawable(getImagefromType(selectedType), 40, 40));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnConfirm_yes.setOnClickListener(v -> {
            String selectedType = spinner.getSelectedItem().toString();
            Potholemodel pothole = new Potholemodel(location.latitude, location.longitude, selectedType, NAME);
            sendBumpDataToServer(pothole);
            dialog.dismiss();
        });
        btnConfirm_no.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    private void sendBumpDataToServer(Potholemodel potholemodel) {
        apiService.sendBumpData(potholemodel).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Bump data sent to server!", Toast.LENGTH_SHORT).show();
                    addBumpMarker(potholemodel);
                } else {
                    Log.e("API Error", "Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(getContext(), "Failed to send bump data.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Network Error", t.getMessage(), t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getImagefromType(String type){
        switch (type) {
            case "Danger":
                return R.drawable.danger_pot;
            case "Warning":
                return R.drawable.warning_pot;
            case "Caution":
                return R.drawable.caution_pot;
            case "current":
                return R.drawable.location;
            case "search":
                return R.drawable.placeholder;
            default:
                return R.drawable.location;
        }
    }

    private Drawable getResizedDrawable(int drawableResId, int width, int height) {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(drawableResId);
        android.graphics.Bitmap originalBitmap = drawable.getBitmap();
        android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        return new BitmapDrawable(getResources(), scaledBitmap);
    }

    private void limitMapPanning() {
        LatLong currentCenter = mapView.getModel().mapViewPosition.getCenter();
        byte currentZoom = mapView.getModel().mapViewPosition.getZoomLevel();

        byte minZoomLevel = 16;
        byte maxZoomLevel = 19;

        if (currentZoom < minZoomLevel) {
            mapView.getModel().mapViewPosition.setZoomLevel(minZoomLevel);
        } else if (currentZoom > maxZoomLevel) {
            mapView.getModel().mapViewPosition.setZoomLevel(maxZoomLevel);
        }

        double latMargin = 0.005;
        double lonMargin = 0.005;

        BoundingBox dynamicBoundingBox = new BoundingBox(
                boundingBox.minLatitude - latMargin,
                boundingBox.minLongitude - lonMargin,
                boundingBox.maxLatitude + latMargin,
                boundingBox.maxLongitude + lonMargin
        );

        if (!dynamicBoundingBox.contains(currentCenter)) {
            double clampedLat = Math.max(dynamicBoundingBox.minLatitude, Math.min(dynamicBoundingBox.maxLatitude, currentCenter.latitude));
            double clampedLon = Math.max(dynamicBoundingBox.minLongitude, Math.min(dynamicBoundingBox.maxLongitude, currentCenter.longitude));
            mapView.getModel().mapViewPosition.setCenter(new LatLong(clampedLat, clampedLon));
        }
    }
}
