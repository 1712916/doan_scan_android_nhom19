package com.example.mayscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import az.rasul.triangleseekbar.TriangleSeekbar;

public class DrawActivity extends Activity {
    MyCustomView myCustomView;
    Button btnUndo, btnSave, btnColor;
    Uri uri;
    String fileName = "";
    Bitmap bitmap;
    Bitmap mBitmap;

    TriangleSeekbar triangleSeekbar;
    TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_activity);

        myCustomView = new MyCustomView(this);
        myCustomView = (MyCustomView) findViewById(R.id.myview);

        triangleSeekbar = findViewById(R.id.triangleSeekbar);
        progressText = findViewById(R.id.progressText);
        triangleSeekbar.setProgressListener(new TriangleSeekbar.ProgressListener() {
            @Override
            public void onProgressChange(float progress) {
                progressText.setText(String.valueOf(progress));
                myCustomView.increaseStrokeWidth(triangleSeekbar.getPercentage());
            }
        });

        btnUndo = (Button) findViewById(R.id.btnUndo);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnColor = (Button) findViewById(R.id.btnColor);
        //bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.win);
        // myCustomView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.win));
        recvData();


        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCustomView.onClickUndo();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // myCustomView.setDrawingCacheEnabled(true);

                mBitmap = Bitmap.createBitmap(myCustomView.getNewImage());

                //  test.setImageBitmap(myCustomView.getNewImage());
                saveBitmap(mBitmap, fileName);
                //myCustomView.setDrawingCacheEnabled(true);
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCustomView.openColorPicker();
            }
        });


    }
/*    public static Bitmap mergeToPin(Bitmap back, Bitmap front) {
        Bitmap result = Bitmap.createBitmap(back.getWidth(), back.getHeight(), back.getConfig());
        Canvas canvas = new Canvas(result);
        int widthBack = back.getWidth();
        int widthFront = front.getWidth();
        float move = (widthBack - widthFront) / 2;
        canvas.drawBitmap(back, 0f, 0f, null);
        canvas.drawBitmap(front, 0f, 0f, null);
        return result;
    }*/


    private void saveBitmap(Bitmap bm, String fileName) {
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = Environment.getExternalStorageDirectory();

        File directory = new File(file.getAbsolutePath() + "/ScanPDF/Images");
        directory.mkdirs();
        if (fileName.equals("")) {
            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        File newFile = new File(directory, fileName);


        try {
            //MediaScannerConnection.scanFile(getApplicationContext(), new String[]  {directory.getPath()} , new String[]{"image/*"}, null);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(getApplication(),
                    "ScanPDF/Images/"+fileName,
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

    public void recvData() {
        Intent intent = getIntent();
        if (intent != null) {
            uri = Uri.parse(intent.getStringExtra("URI"));
            fileName = intent.getStringExtra("FILENAME");
            mBitmap = uriToBitmap(uri);
            myCustomView.setmBitmap(mBitmap);
        }


    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image = null;
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
