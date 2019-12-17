package com.example.mayscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.DRAWING_CACHE_QUALITY_LOW;

public class RotateActivity extends Activity {

    Uri uri;
    int postition;
    Bitmap mBitmap;
    ImageView myCustomView;
    Button btnSave, btnCancel, btnRotate;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    String fileName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);

        myCustomView=new MyCustomView(this);
        myCustomView=(ImageView) findViewById(R.id.myviewRotate);
        btnSave=(Button)findViewById(R.id.btnSaveRotate);
        btnCancel=(Button)findViewById(R.id.btnCancelRotate);
        btnRotate=(Button)findViewById(R.id.btnRotate);
        recvData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // myCustomView.setDrawingCacheEnabled(true);
                myCustomView.setDrawingCacheEnabled(true);
                myCustomView.destroyDrawingCache();
                myCustomView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                mBitmap=myCustomView.getDrawingCache();
                //  test.setImageBitmap(myCustomView.getNewImage());
                askPermissionAndWriteFile();
                //myCustomView.setDrawingCacheEnabled(true);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
                myCustomView.setImageBitmap(mBitmap);
            }
        });
    }

    private void askPermissionAndWriteFile() {
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        if (canWrite) {
            this.saveBitmap(mBitmap, fileName);
        }
    }

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

    private void saveBitmap(Bitmap bm, String fileName) {
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = Environment.getExternalStorageDirectory();

        File directory = new File(file.getAbsolutePath() + "/ScanPDF/Images");
        directory.mkdirs();
        if (fileName.equals("")) {
            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        File newFile = new File(directory, fileName );


        try {
            //MediaScannerConnection.scanFile(getApplicationContext(), new String[]  {directory.getPath()} , new String[]{"image/*"}, null);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(getApplication(),
                    "Save Bitmap: " + fileOutputStream.toString(),
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),
                    "Something wrong: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),
                    "Something wrong: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void recvData(){

        Intent intent=getIntent();
        if(intent!=null)
        {
            uri = Uri.parse(intent.getStringExtra("URI"));
            fileName=intent.getStringExtra("FILENAME");
            mBitmap=uriToBitmap(uri);
            myCustomView.setImageBitmap(mBitmap);
        }
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image=null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
