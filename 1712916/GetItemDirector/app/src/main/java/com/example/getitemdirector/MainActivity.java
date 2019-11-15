package com.example.getitemdirector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ID_READ_PERMISSION = 200;
    ArrayList<String> pathArray;
    TextView txtMsg;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMsg = (TextView) findViewById(R.id.txtMsg);
      //  checkPermissions();
        askPermissionAndWriteFile();
       for(int i=0;i<pathArray.size();i++)
        {
            txtMsg.append("\n" + pathArray.get(i));


        }



    }

    public ArrayList<String> getFilePaths() {
        ArrayList<String> listName=new ArrayList<>();
        //File dowloadsFolder= getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = Environment.getExternalStorageDirectory();
        File directory=new File(file.getAbsolutePath()+"/Pictures/Ahihi");



        if(directory.exists())
        {
            Toast.makeText(this,"Mo duoc file de lay du lieu",Toast.LENGTH_LONG).show();
            File[] files=directory.listFiles();
            for(int i=0;i<files.length;i++){
                File z=files[i];
                String item=new String();


                item=z.getName(); //lay ten cua tep
                //uri=Uri.fromFile(z); //lay uri cua tep


                listName.add(item);
            }

        }else
        {
            Toast.makeText(this,"KO mo duoc file de lay du lieu",Toast.LENGTH_LONG).show();
        }

        return listName;
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
                        pathArray=getFilePaths();
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
        }
    }
    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


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
            pathArray=this.getFilePaths();
        }
    }
}
