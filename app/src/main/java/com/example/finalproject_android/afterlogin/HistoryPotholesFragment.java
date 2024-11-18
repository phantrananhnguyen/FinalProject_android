package com.example.finalproject_android.afterlogin;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalproject_android.HistoryPointPlusActivity;
import com.example.finalproject_android.R;
import com.example.finalproject_android.ViewPagerAdapter;
import com.example.finalproject_android.models.MonthAdapter;
import com.example.finalproject_android.models.MonthItem;
import com.example.finalproject_android.models.Pothole;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
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
        potholeListOctober.add(new Pothole("101", "101", "KTX Dorm B, HCM", "10.7742, 106.7032", "ID001", "2024-10-10", "Caution", "Accept"));
        potholeListOctober.add(new Pothole("102", "102", "Main Street", "10.7743, 106.7033", "ID002", "2024-10-15", "Warning", "Pending"));
        potholeListOctober.add(new Pothole("103", "103", "Elm Road", "10.7744, 106.7034", "ID003", "2024-10-18", "Danger", "Reject"));

        // Create a MonthItem for October with Potholes
        MonthItem<Pothole> monthItemOctober = new MonthItem<>("October", potholeListOctober);
        monthItemList.add(monthItemOctober);

        List<Pothole> potholeListNovember = new ArrayList<>();
        potholeListNovember.add(new Pothole("104", "105", "Oak Lane", "10.7745, 106.7035", "ID004", "2024-11-05", "Caution", "Accept"));
        potholeListNovember.add(new Pothole("105", "106", "Pine Road", "10.7746, 106.7036", "ID005", "2024-11-12", "Warning", "Pending"));
        potholeListNovember.add(new Pothole("106", "107", "Maple Avenue", "10.7747, 106.7037", "ID006", "2024-11-20", "Danger", "Reject"));

        // Create a MonthItem for November with Potholes
        MonthItem<Pothole> monthItemNovember = new MonthItem<>("November", potholeListNovember);
        monthItemList.add(monthItemNovember);

        List<Pothole> potholeListDecember = new ArrayList<>();
        potholeListDecember.add(new Pothole("107", "108", "Cherry Street", "10.7748, 106.7038", "ID007", "2024-12-01", "Caution", "Accept"));
        potholeListDecember.add(new Pothole("108", "109", "Willow Lane", "10.7749, 106.7039", "ID008", "2024-12-10", "Warning", "Pending"));
        potholeListDecember.add(new Pothole("109", "110", "Birch Road", "10.7750, 106.7040", "ID009", "2024-12-15", "Danger", "Reject"));

        // Create a MonthItem for December with Potholes
        MonthItem<Pothole> monthItemDecember = new MonthItem<>("December", potholeListDecember);
        monthItemList.add(monthItemDecember);

        // Set up the adapter with the MonthItem list
        monthAdapter = new MonthAdapter(monthItemList);
        recyclerViewMonths.setAdapter(monthAdapter);


        return view;
    }

    // Method to open the PointPlusActivity
    private void openPointPlusActivity() {
        Intent intent = new Intent(getActivity(), HistoryPointPlusActivity.class);
        startActivity(intent);
    }
}
