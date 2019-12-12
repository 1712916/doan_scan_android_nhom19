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
    private ArrayList<ItemRow> array_view_pdf=new ArrayList<>();
    ListViewAdapter listViewAdapter;
    private static final int REQUEST_ID_READ_PERMISSION = 200;
    String fileName;
    AsyncLoadPdfs asyncLoadPdfs=new AsyncLoadPdfs();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.list_pdf,container,false);
        listView=(ListView) view.findViewById(R.id.list_view_pdf);

        //load du lieu cho array_view_pdf
        askPermission();


        listViewAdapter=new ListViewAdapter(getContext(),R.layout.list_view_item,array_view_pdf);

        listView.setAdapter(listViewAdapter);
       asyncLoadPdfs.execute();

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

        return  view;

    }

    public void getFilePaths() {
        array_view_pdf.clear();
        //File dowloadsFolder= getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = Environment.getExternalStorageDirectory();
        File directory=new File(file.getAbsolutePath()+"/ScanPDF/PDFs");



        if(directory.exists())
        {

            File[] files=directory.listFiles();
            for(int i=0;i<files.length;i++){
                File z=files[i];
                ItemRow item=new ItemRow();


                item.setText(z.getName()); //lay ten cua tep
                item.setUri(Uri.fromFile(z)); //lay uri cua tep

                array_view_pdf.add(item);
            }

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
    }
    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
    private void askPermission() {
        boolean canRead = this.askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(getContext(), permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }



    private class AsyncLoadPdfs extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getFilePaths();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listViewAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }

    }
}
