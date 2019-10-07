package com.example.aircontrol.Utility;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.aircontrol.Fragment.AirQualityFragment;
import com.example.aircontrol.Fragment.TemperatureFragment;

public class MyPagerAdapter  extends FragmentPagerAdapter {
    private final int PAGE_NUM = 2;
    public MyPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public int getCount() {
        return PAGE_NUM;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new AirQualityFragment();
        else if(position == 1)
            return new TemperatureFragment();
        return null;
    }
}