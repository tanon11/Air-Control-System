package com.example.aircontrol;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.aircontrol.Utility.MyPagerAdapter;

public class MainActivity extends FragmentActivity {
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(mPagerAdapter);

//        pager.setCurrentItem(pager.getCurrentItem() + 1);
    }


}
