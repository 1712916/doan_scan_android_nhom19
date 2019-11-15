package com.example.mypageview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentGrid extends Fragment {
    private GridView gridView;
    private ArrayList<ItemRow> array_view_images;
    GridViewAdapter gridViewAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.grid_images,container,false);
        gridView=(GridView)view.findViewById(R.id.grid_view_images);
        array_view_images=new ArrayList<>();
        array_view_images.add(new ItemRow("123123",R.drawable.icons_pdf));
        array_view_images.add(new ItemRow("123123123",R.drawable.icons_pdf));
        array_view_images.add(new ItemRow("123123",R.drawable.icons_pdf));
        array_view_images.add(new ItemRow("123123",R.drawable.icons_pdf));
        array_view_images.add(new ItemRow("554654",R.drawable.icons_pdf));
        array_view_images.add(new ItemRow("456456",R.drawable.icons_pdf));
        array_view_images.add(new ItemRow("456456",R.drawable.icons_pdf));
        array_view_images.add(new ItemRow("456456456",R.drawable.icons_pdf));

        gridViewAdapter=new GridViewAdapter(getContext(),R.layout.grid_item,array_view_images);

        gridView.setAdapter(gridViewAdapter);
        return  view;

    }
}
