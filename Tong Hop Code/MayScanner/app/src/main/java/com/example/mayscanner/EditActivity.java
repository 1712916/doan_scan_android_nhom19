package com.example.mayscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends Activity {

    ZoomableImageView imgvEdit;
    Button btnToEdit, btnOcr, btnShare, btnPdf, btnToCrop, btnToRotate;
    Uri uri, desUri;
    String fileName;
    Bitmap bitmap, mBitmap;
    int postition;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        btnToEdit = (Button) findViewById(R.id.btnToEdit);
        btnOcr = (Button) findViewById(R.id.btnOcr);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnPdf = (Button) findViewById(R.id.btnPdf);
        btnToCrop = (Button) findViewById(R.id.btnToCrop);
        btnToRotate = (Button) findViewById(R.id.btnToRotate);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ShareFileActivity.class);
                intent.putExtra("URI",uri.toString());
                startActivity(intent);
            }
        });
        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConvertToPdf.class);
                intent.putExtra("URI",uri.toString());
                startActivity(intent);
            }
        });

        btnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),OcrActivity.class);
                intent.putExtra("URI",uri.toString());
                intent.putExtra("FILENAME",fileName);
                startActivity(intent);
            }
        });

        btnToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DrawActivity.class);
                intent.putExtra("URI", uri.toString());
                intent.putExtra("FILENAME", fileName);
                startActivity(intent);
            }
        });

        btnToCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openCropActivity(uri, uri);

                } catch (Exception e) {
                    Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("CROPERROR", e.getMessage());
                }
            }
        });

        btnToRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RotateActivity.class);
                intent.putExtra("URI", uri.toString());
                intent.putExtra("FILENAME",fileName);
                startActivity(intent);
            }
        });

        imgvEdit = (ZoomableImageView) findViewById(R.id.imgv_edit);
        recvData();
        bitmap = uriToBitmap(uri);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        imgvEdit.setImageBitmap(uriToBitmap(uri));
    }

    public void recvData() {

        Intent intent = getIntent();
        if (intent != null) {
            uri = Uri.parse(intent.getStringExtra("URI"));
            fileName = intent.getStringExtra("FILENAME");
            imgvEdit.setImageBitmap(uriToBitmap(uri));
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

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));

        try {
             UCrop.of(sourceUri, destinationUri)
                    .withMaxResultSize(500, 500)
                    .withAspectRatio(5f, 5f)
                     .start(this);
        } catch (Exception e) {
            Log.d("loi crop", e.getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("vietpro", "#######");
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            //if (data != null) {
                uri = UCrop.getOutput(data);
                Log.i("vietpro", uri.toString());
               // imgvEdit.setImageBitmap(uriToBitmap(desUri));//
                mBitmap = uriToBitmap(uri);
                askPermissionAndWriteFile();

            //}
        }
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
        File newFile = new File(directory, fileName);


        try {
            //MediaScannerConnection.scanFile(getApplicationContext(), new String[]  {directory.getPath()} , new String[]{"image/*"}, null);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
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





    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
