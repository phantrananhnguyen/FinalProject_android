package com.example.finalproject_android.afterlogin;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalproject_android.HistoryPointPlusActivity;
import com.example.finalproject_android.R;
import com.example.finalproject_android.models.MonthAdapter;
import com.example.finalproject_android.models.MonthItem;
import com.example.finalproject_android.models.Pothole;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryPotholesFragment extends Fragment {

    private RecyclerView recyclerViewMonths;
    private MonthAdapter monthAdapter;
    private List<MonthItem<?>> monthItemList;

    public HistoryPotholesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_potholes, container, false);

        recyclerViewMonths = view.findViewById(R.id.recyclerViewPotholes);
        recyclerViewMonths.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the month item list
        monthItemList = new ArrayList<>();

        // Create Pothole lists for October, November, and December
        List<Pothole> potholeListOctober = new ArrayList<>();
        potholeListOctober.add(createPothole("101", "journey101", "101", "10.7742", "106.7032", "Caution", "Accept", "url_or_filepath_1", "2024-10-10", "2024-10-10"));
        potholeListOctober.add(createPothole("102", "journey102", "102", "10.7743", "106.7033", "Warning", "Pending", "url_or_filepath_2", "2024-10-15", "2024-10-15"));
        potholeListOctober.add(createPothole("103", "journey103", "103", "10.7744", "106.7034", "Danger", "Reject", "url_or_filepath_3", "2024-10-18", "2024-10-18"));

        // Create a MonthItem for October with Potholes
        MonthItem<Pothole> monthItemOctober = new MonthItem<>("October", potholeListOctober);
        monthItemList.add(monthItemOctober);

        List<Pothole> potholeListNovember = new ArrayList<>();
        potholeListNovember.add(createPothole("104", "journey104", "105", "10.7745", "106.7035", "Caution", "Accept", "url_or_filepath_4", "2024-11-05", "2024-11-05"));
        potholeListNovember.add(createPothole("105", "journey105", "106", "10.7746", "106.7036", "Warning", "Pending", "url_or_filepath_5", "2024-11-12", "2024-11-12"));
        potholeListNovember.add(createPothole("106", "journey106", "107", "10.7747", "106.7037", "Danger", "Reject", "url_or_filepath_6", "2024-11-20", "2024-11-20"));

        // Create a MonthItem for November with Potholes
        MonthItem<Pothole> monthItemNovember = new MonthItem<>("November", potholeListNovember);
        monthItemList.add(monthItemNovember);

        List<Pothole> potholeListDecember = new ArrayList<>();
        potholeListDecember.add(createPothole("107", "journey107", "108", "10.7748", "106.7038", "Caution", "Accept", "url_or_filepath_7", "2024-12-01", "2024-12-01"));
        potholeListDecember.add(createPothole("108", "journey108", "109", "10.7749", "106.7039", "Warning", "Pending", "url_or_filepath_8", "2024-12-10", "2024-12-10"));
        potholeListDecember.add(createPothole("109", "journey109", "110", "10.7750", "106.7040", "Danger", "Reject", "url_or_filepath_9", "2024-12-15", "2024-12-15"));

        // Create a MonthItem for December with Potholes
        MonthItem<Pothole> monthItemDecember = new MonthItem<>("December", potholeListDecember);
        monthItemList.add(monthItemDecember);

        // Set up the adapter with the MonthItem list
        monthAdapter = new MonthAdapter(monthItemList);
        recyclerViewMonths.setAdapter(monthAdapter);

        return view;
    }

    // Helper method to create Pothole objects with the new constructor (including journey_id)
    private Pothole createPothole(String id, String journey_id, String user_id, String lat, String lon, String type, String state, String img, String createdAt, String updatedAt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date createdDate = dateFormat.parse(createdAt);
            Date updatedDate = dateFormat.parse(updatedAt);
            return new Pothole(id, journey_id, user_id, lat, lon, type, state, img, createdDate, updatedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to open the PointPlusActivity
    private void openPointPlusActivity() {
        Intent intent = new Intent(getActivity(), HistoryPointPlusActivity.class);
        startActivity(intent);
    }
}
