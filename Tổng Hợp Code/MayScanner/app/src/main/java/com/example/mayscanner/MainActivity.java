package com.example.mayscanner;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

//extands Activity ko su dung duoc ham getSupportFragmentManager
public class MainActivity extends FragmentActivity {
    ViewPager viewPager;
    private ArrayList<Integer> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       viewPager = (ViewPager) findViewById(R.id.viewpager);
       // MyCustomPagerAdapter myCustomPagerAdapter=new MyCustomPagerAdapter(this,arr);
        MyPagerAdapter myPagerAdapter=new MyPagerAdapter( getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);



    }
}

