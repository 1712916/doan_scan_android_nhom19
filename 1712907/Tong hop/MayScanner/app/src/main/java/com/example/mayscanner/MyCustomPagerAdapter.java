package com.example.mayscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class MyCustomPagerAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private ArrayList<Integer> arr;


    public MyCustomPagerAdapter(Context context, ArrayList<Integer> arr) {
        this.inflater = LayoutInflater.from(context);
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //1. chuyen file xml thanh view
        View view=inflater.inflate(R.layout.grid_images,container,false);

        //2 them vao container
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
