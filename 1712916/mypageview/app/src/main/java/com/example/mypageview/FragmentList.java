package com.example.mypageview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;

public class FragmentList  extends Fragment {
    private ListView listView;
    private ArrayList<ItemRow> array_view_pdf;
    ListViewAdapter listViewAdapter;
    private static final int REQUEST_ID_READ_PERMISSION = 200;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.list_pdf,container,false);
        listView=(ListView) view.findViewById(R.id.list_view_pdf);

        //load du lieu cho array_view_pdf
        array_view_pdf=new ArrayList<>();
        askPermissionAndWriteFile();


        listViewAdapter=new ListViewAdapter(getContext(),R.layout.list_view_item,array_view_pdf);

        listView.setAdapter(listViewAdapter);

        return  view;

    }

    public ArrayList<ItemRow> getFilePaths() {
        ArrayList<ItemRow> listItem=new ArrayList<>();
        //File dowloadsFolder= getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = Environment.getExternalStorageDirectory();
        File directory=new File(file.getAbsolutePath()+"/Pictures/Ahihi");



        if(directory.exists())
        {
            Toast.makeText(getActivity(),"Mo duoc file de lay du lieu",Toast.LENGTH_LONG).show();
            File[] files=directory.listFiles();
            for(int i=0;i<files.length;i++){
                File z=files[i];
                ItemRow item=new ItemRow();


                item.setText(z.getName()); //lay ten cua tep
                item.setUri(Uri.fromFile(z)); //lay uri cua tep

                listItem.add(item);
            }

        }else
        {
           Toast.makeText(getActivity(),"KO mo duoc file de lay du lieu",Toast.LENGTH_LONG).show();
        }

        return listItem;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        array_view_pdf=getFilePaths();
                    }
                }
            }
        } else {
            Toast.makeText(getActivity(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }
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

    private void askPermissionAndWriteFile() {
        boolean canWrite = this.askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        //
        if (canWrite) {
            array_view_pdf=this.getFilePaths();
        }
    }
}
