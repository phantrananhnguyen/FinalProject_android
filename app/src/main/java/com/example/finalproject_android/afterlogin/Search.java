package com.example.finalproject_android.afterlogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.BottomNavigation;
import com.example.finalproject_android.R;
import com.example.finalproject_android.models.Feature;
import com.example.finalproject_android.models.Places;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity implements RecyclerViewInterface {

    ApiService apiService;
    EditText search_input;
    Button search;
    ImageButton back;
    ArrayList<Feature> features = new ArrayList<>();
    SearchAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        apiService = ApiClient.getClient().create(ApiService.class);
        search_input = findViewById(R.id.search_input);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new SearchAdapter(this, features, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        search_input.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 300;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timer.cancel();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String keyword = editable.toString().trim();
                        if(!keyword.isEmpty()){
                            runOnUiThread(()-> getPlaceinfor(keyword));
                        }
                    }
                }, DELAY);
            }
        });
    }

    private void setupSearchModel(Feature feature) {
        String name = feature.getName();
        String amenity = feature.getAmenity();
        double lat = feature.getLat();
        double lon = feature.getLon();
        features.add(new Feature(name, amenity, lat, lon));  // Add the feature to the list
    }

    private void getPlaceinfor(String query) {
        apiService.searchPlaces(query).enqueue(new Callback<Places>() {
            @Override
            public void onResponse(Call<Places> call, Response<Places> response) {
                Places places = response.body();
                if (response.isSuccessful() && places != null && places.getPlaces() != null) {
                    features.clear();  // Clear the old data
                    if (places.getPlaces().size() > 0) {
                        for (Feature feature : places.getPlaces()) {
                            if (feature != null) {
                                setupSearchModel(feature);
                            }
                        }
                        adapter.notifyDataSetChanged();  // Notify adapter to update the RecyclerView
                    } else {
                        // Show "No results found" message or placeholder
                        Log.e("Search", "No results found.");
                        // Optionally, you can show a "No results" TextView in the UI
                    }
                } else {
                    Log.e("Search", "No places or features available.");
                }
            }

            @Override
            public void onFailure(Call<Places> call, Throwable t) {
                Log.e("Search", "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(Feature feature) {
        Intent intent = new Intent(Search.this, BottomNavigation.class);
        intent.putExtra("feature", feature);
        startActivity(intent);
    }
}
