package com.example.mayscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FragmentGrid extends Fragment {

    private GridView gridView;
    private ArrayList<ItemRow> array_view_images;
    GridViewAdapter gridViewAdapter;
//    private static final int REQUEST_ID_READ_PERMISSION = 200;
//    private static final int  REQUEST_ID_WRITE_PERMISSION=201;


    public FragmentGrid(ArrayList<ItemRow> array_view_images) {
        this.array_view_images = array_view_images;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.grid_images,container,false);
        gridView=(GridView)view.findViewById(R.id.grid_view_images);

        //load du lieu cho array_view_images


        gridViewAdapter=new GridViewAdapter(getContext(),R.layout.grid_item,array_view_images);

        gridView.setAdapter(gridViewAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName=array_view_images.get(position).getText();
                Toast.makeText(getContext(),fileName,Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getActivity(),EditActivity.class);
                intent.putExtra("URI", array_view_images.get(position).getUri().toString());
                intent.putExtra("FILENAME",fileName);
                startActivity(intent);

            }
        });

        return  view;
    }

    public void notifyDataSetChanged(){
        gridView.deferNotifyDataSetChanged();
    }
}
