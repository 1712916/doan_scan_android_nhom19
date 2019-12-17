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
    private ArrayList<ItemRow> array_view_images=new ArrayList<>();
    GridViewAdapter gridViewAdapter;
    private static final int REQUEST_ID_READ_PERMISSION = 200;
    private static final int  REQUEST_ID_WRITE_PERMISSION=201;
    AsyncLoadImages asyncLoadImages=new AsyncLoadImages();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermission();

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
        asyncLoadImages.execute();

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
    public void getFilePaths() {
        array_view_images.clear();
        //File dowloadsFolder= getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = Environment.getExternalStorageDirectory();
        File directory=new File(file.getAbsolutePath()+"/ScanPDF/Images");
        directory.mkdirs();


        if(directory.exists())
        {
            //Toast.makeText(getActivity(),"Load dữ Images liệu thành công!",Toast.LENGTH_LONG).show();
            File[] files=directory.listFiles();
            for(int i=0;i<files.length;i++){
                File z=files[i];
                ItemRow item=new ItemRow();


                item.setText(z.getName()); //lay ten cua tep
                item.setUri(Uri.fromFile(z)); //lay uri cua tep

                array_view_images.add(item);
            }

        }


    }
  /*  @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     //   array_view_images=getFilePaths();
                    }
                }
            }
        } else {
            Toast.makeText(getActivity(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }*/
    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
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

    private void askPermission() {
        boolean canRead = this.askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }




    public  void removeFirst(){
        array_view_images.remove(0);
        gridViewAdapter.notifyDataSetChanged();
    }
    private class AsyncLoadImages extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getFilePaths();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            gridViewAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }
}
