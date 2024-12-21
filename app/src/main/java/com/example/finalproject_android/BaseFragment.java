package com.example.finalproject_android;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    protected UsageTimer usageTimer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Sử dụng UsageTimer từ Activity
        if (context instanceof BaseActivity) {
            usageTimer = ((BaseActivity) context).usageTimer;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Bắt đầu session khi Fragment hiển thị
        if (usageTimer != null) {
            usageTimer.startSession();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Dừng session khi Fragment không còn hiển thị
        if (usageTimer != null) {
            usageTimer.stopSession();
        }
    }
}

/* Usage:
public class MainFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Hiển thị tổng thời gian sử dụng
        if (usageTimer != null) {
            long totalUsageTimeInSeconds = usageTimer.getTotalUsageTime() / 1000;
            Toast.makeText(requireContext(), "Tổng thời gian: " + totalUsageTimeInSeconds + " giây", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
 */