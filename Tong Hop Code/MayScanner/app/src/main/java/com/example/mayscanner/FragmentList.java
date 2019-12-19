package com.example.mayscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FragmentList  extends Fragment {
    private ListView listView;
    private ArrayList<ItemRow> array_view_pdf = new ArrayList<>();
    ListViewAdapter listViewAdapter;
    private static final int REQUEST_ID_READ_PERMISSION = 200;
    String fileName;
    /*    AsyncLoadPdfs asyncLoadPdfs=new AsyncLoadPdfs();*/

    public FragmentList(ArrayList<ItemRow> array_view_pdf) {
        this.array_view_pdf = array_view_pdf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_pdf, container, false);
        listView = (ListView) view.findViewById(R.id.list_view_pdf);


        listViewAdapter = new ListViewAdapter(getContext(), R.layout.list_view_item, array_view_pdf);

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("viettt", "aaaa");
                fileName = array_view_pdf.get(position).getText();
                Log.i("viettt", fileName);
                Intent intent1 = new Intent(getContext(), PDFViewActivity.class);
                Log.i("viettt", "onItemClick: ###");
                StringTokenizer tokens = new StringTokenizer(fileName, ".");
                fileName = tokens.nextToken();
                intent1.putExtra("filename", fileName);
                startActivity(intent1);
            }
        });

        return view;

    }
    public void notifyDataSetChanged(){

        listViewAdapter.notifyDataSetChanged();
    }
}

