package com.example.mayscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UploadAsyncTask extends AsyncTask<Void, Boolean, Void> {
    Activity contextCha;
    String[] uploadedDirectoryName = {"/Images", "/TextOCR", "/PDFs"};
    File file;
    boolean flag;
    private FirebaseAuth mAuth;

    public UploadAsyncTask(Activity ctx) {
        contextCha = ctx;
    }

    @Override
    protected void onPreExecute() {
        file = Environment.getExternalStorageDirectory();
        mAuth = FirebaseAuth.getInstance();
        showProgressDialog("Đang upload dữ liệu lên cloud");
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... objects) {
        for (int idx = 0; idx < 3; idx++) {
            File directory = new File(file.getPath() + "/ScanPDF" + uploadedDirectoryName[idx]);
            if (directory.exists()) {
                StorageReference mStorageRef;
                mStorageRef = FirebaseStorage.getInstance()
                        .getReferenceFromUrl("gs://scanpdf-92556.appspot.com/"
                                + mAuth.getCurrentUser().getEmail() + uploadedDirectoryName[idx]);
                File[] files = directory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File z = files[i];
                    Uri mFileUri = Uri.fromFile(z);
                    StorageReference mIslandRef = mStorageRef.child(mFileUri.getLastPathSegment());
                    mIslandRef.putFile(mFileUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    flag = true;
                                    //publishProgress(flag);
                                    Log.d("UPLOAD_SUCCESSFUL", "SUCCESS");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            flag = false;
                            //publishProgress(flag);
                            Log.d("EXCEPTION_UPLOAD", exception.getMessage());
                        }
                    });
                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if (values[0]) {
            Toast.makeText(contextCha, "Upload thành công", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(contextCha, "Upload thất bại", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
//        if (result) {
//            Toast.makeText(contextCha, "Upload thành công", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(contextCha, "Upload thất bại", Toast.LENGTH_LONG).show();
//        }

        hideProgressDialog();
        Toast.makeText(contextCha, "Thực hiện Upload đã xong!", Toast.LENGTH_LONG).show();
    }

    public static ProgressDialog mProgressDialog;

    public void showProgressDialog(String message) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(contextCha);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
            }
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } catch (Exception e) {
            Log.d("SHOWDIALOG", e.getMessage());
        }
    }

    public void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.d("HIDEDIALOG", e.getMessage());
        }
    }
}
