package com.example.finalproject_android.afterlogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject_android.R;
import com.example.finalproject_android.models.MonthAdapter;
import com.example.finalproject_android.models.MonthItem;
import com.example.finalproject_android.models.PointPlusAdapter;
import com.example.finalproject_android.models.PointPlusItem;
import java.util.ArrayList;
import java.util.List;

public class HistoryPointPlusFragment extends Fragment {

    public HistoryPointPlusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fragment sẽ xử lý dữ liệu và hiển thị RecyclerView khi nó được tạo
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View rootView = inflater.inflate(R.layout.fragment_history_point_plus, container, false);

        // Tạo RecyclerView và set Adapter
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewPointPlus);
        List<MonthItem<?>> monthItems = createFakeData();
        MonthAdapter monthAdapter = new MonthAdapter(monthItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(monthAdapter);

        return rootView;
    }

    private List<MonthItem<?>> createFakeData() {
        List<MonthItem<?>> months = new ArrayList<>();

        // Tạo dữ liệu giả cho các tháng
        List<PointPlusItem> octoberItems = new ArrayList<>();
        octoberItems.add(new PointPlusItem("Quite good!", "Thanks for your contribution", "Your report was accepted", "101", "Caution", "26/10/2024", 30));
        octoberItems.add(new PointPlusItem("Very good!", "Keep it up!", "Your report was accepted", "102", "Warning", "27/10/2024", 75));
        octoberItems.add(new PointPlusItem("Excellent!", "Amazing work!", "Your report was accepted", "103", "Danger", "28/10/2024", 120));
        months.add(new MonthItem("October 2024", octoberItems));

        List<PointPlusItem> septemberItems = new ArrayList<>();
        septemberItems.add(new PointPlusItem("Quite good!", "Thanks for your effort", "Your report was accepted", "201", "Caution", "15/09/2024", 40));
        septemberItems.add(new PointPlusItem("Very good!", "Good job!", "Your report was accepted", "202", "Warning", "16/09/2024", 85));
        septemberItems.add(new PointPlusItem("Excellent!", "Great work!", "Your report was accepted", "203", "Danger", "17/09/2024", 130));
        months.add(new MonthItem("September 2024", septemberItems));

        List<PointPlusItem> augustItems = new ArrayList<>();
        augustItems.add(new PointPlusItem("Quite good!", "Thanks for participating", "Your report was accepted", "301", "Caution", "20/08/2024", 20));
        augustItems.add(new PointPlusItem("Very good!", "Keep contributing!", "Your report was accepted", "302", "Warning", "21/08/2024", 65));
        augustItems.add(new PointPlusItem("Excellent!", "Outstanding effort!", "Your report was accepted", "303", "Danger", "22/08/2024", 110));
        months.add(new MonthItem("August 2024", augustItems));

        return months;
    }
}
