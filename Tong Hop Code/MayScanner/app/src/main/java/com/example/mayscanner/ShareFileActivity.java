package com.example.mayscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class ShareFileActivity extends Activity {

    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    private Button btn;
    private ImageView iv;
    Uri uri;
    String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_file_activity);

        Intent intent = getIntent();
        uri = Uri.parse(intent.getStringExtra("URI"));
        flag=intent.getStringExtra("flag");
        btn = (Button) findViewById(R.id.btn1);
        iv = (ImageView) findViewById(R.id.imageview);
        if(!flag.equals("pdf"))
        {
            iv.setImageBitmap(uriToBitmap(uri));
        }

        Log.i("viettt", flag);
        btn.setOnClickListener(new View.OnClickListener() {
                                   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                   @Override
                                   public void onClick(View view) {
                                       askPermissionAndReadFile();
                                   }
                               }
        );
    }

    private void testShareFile_Feture() {
        String extStore = System.getenv("EXTERNAL_STORAGE");
        File f_exts = new File(extStore);
        String path = f_exts.getAbsolutePath();
        String myFilePath;

        if(flag.equals("pdf"))
        {
            myFilePath= path + "/ScanPDF/PDFs/" + uri.getLastPathSegment();
        }
        else {
            myFilePath = path + "/ScanPDF/Images/" + uri.getLastPathSegment();
        }

        File file = new File(myFilePath);

        Toast.makeText(getApplicationContext(), file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID, file);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void askPermissionAndReadFile() {
        boolean canRead = this.askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        //
        if (canRead) {
            testShareFile_Feture();
        }
    }


    // <không nên đụng vô>
    private boolean askPermission(int requestId, String permissionName) {
        if (Build.VERSION.SDK_INT >= 23) {

            // Kiểm tra quyền
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // Nếu không có quyền, cần nhắc người dùng cho phép.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }
    // </không nên đụng vô>


    // Khi yêu cầu hỏi người dùng được trả về (Chấp nhận hoặc không chấp nhận).
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //

        // Chú ý: Nếu yêu cầu bị hủy, mảng kết quả trả về là rỗng.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        testShareFile_Feture();
                    }
                }
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Cancelled!", Toast.LENGTH_SHORT).show();
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



