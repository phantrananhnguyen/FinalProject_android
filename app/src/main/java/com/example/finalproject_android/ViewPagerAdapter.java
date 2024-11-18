package com.example.finalproject_android;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.finalproject_android.afterlogin.HistoryPointPlusFragment;
import com.example.finalproject_android.afterlogin.HistoryPotholesFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        // Tạo Fragment tương ứng với vị trí
        if (position == 0) {
            return new HistoryPotholesFragment();

        } else {
            return new HistoryPointPlusFragment();
        }
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    public String getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
