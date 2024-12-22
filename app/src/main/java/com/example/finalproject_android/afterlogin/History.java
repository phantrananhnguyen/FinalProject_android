package com.example.finalproject_android.afterlogin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_android.R;
import com.example.finalproject_android.models.HistoryItem;
import com.example.finalproject_android.models.UserSession;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class History extends Fragment {
    private RecyclerView recyclerView;
    private Spinner filterSpinner;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList = new ArrayList<>();
    private ApiService apiService;
    String username;
    UserSession userSession;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userSession = new UserSession(getContext());
        if (userSession != null) {username = userSession.getUsername();
        } else {
            Log.e("MapFragment", "UserSession is null");
        }
        filterSpinner = view.findViewById(R.id.filter);


        adapter = new HistoryAdapter(historyList, getContext());
        recyclerView.setAdapter(adapter);


        apiService = ApiClient.getClient(getContext()).create(ApiService.class);


        fetchHistoryData();
        setupSpinner();

        return view;
    }

    private void fetchHistoryData() {
        if (username.isEmpty()) {
            return;
        }
        apiService.history(username).enqueue(new Callback<List<HistoryItem>>() {
            @Override
            public void onResponse(Call<List<HistoryItem>> call, Response<List<HistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    historyList.clear();
                    historyList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    filterHistoryList("All");
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<HistoryItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setupSpinner() {
        // Danh sách các loại ổ gà
        String[] types = {"All", "Caution", "Warning", "Danger"};

        // Tạo adapter cho Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setSelection(0);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = types[position];
                filterHistoryList(selectedType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterHistoryList("All");
            }
        });
    }
    private void filterHistoryList(String type) {
        adapter.filter(type);
    }
}
