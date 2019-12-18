package com.example.mayscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class DownloadAsyncTask extends AsyncTask<Void, Boolean, Void> {
    Activity contextCha;
    String[] downloadedDirectoryName = {"/Images", "/TextOCR", "/PDFs"};
    File file;
    private FirebaseAuth mAuth;
    boolean flag = false;

    public DownloadAsyncTask(Activity ctx) {
        contextCha = ctx;
    }

    @Override
    protected void onPreExecute() {

        Log.d("ORDER", "1");
        file = Environment.getExternalStorageDirectory();
        mAuth = FirebaseAuth.getInstance();
        showProgressDialog("Đang download dữ liệu từ cloud");
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("ORDER", "2");
        for (int idx = 0; idx < 3; idx++) {
            File directory = new File(
                    file.getPath() + "/ScanPDF" + downloadedDirectoryName[idx]);
            directory.mkdirs();
            StorageReference listRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://scanpdf-92556.appspot.com/"
                            + mAuth.getCurrentUser().getEmail() + downloadedDirectoryName[idx]);
            listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        // All the prefixes under listRef.
                        // You may call listAll() recursively on them.
                    }
                    for (StorageReference item : listResult.getItems()) {
                        // All the items under listRef.
                        String fileName = item.getName();
                        String extension = "";
                        int indexDot = fileName.lastIndexOf(".");
                        if (indexDot != -1) {
                            extension = fileName.substring(indexDot);
                            fileName = fileName.substring(0, indexDot);
                        }
                        File[] files = directory.listFiles();
                        int demTrungTen = 0;
                        String newFileName = fileName + extension;
                        while (true) {
                            int i;
                            for (i = 0; i < files.length; i++) {
                                File z = files[i];
                                if (newFileName.equals(z.getName())) {
                                    break;
                                }
                            }
                            if (i < files.length) {
                                demTrungTen++;
                                newFileName = fileName + "(" + demTrungTen + ")" + extension;
                            }
                            else {
                                break;
                            }
                        }
                        File newFile = new File(directory, newFileName);
                        item.getFile(newFile).addOnSuccessListener(
                                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(
                                            FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        flag = true;
                                        publishProgress(flag);
                                        Log.d("DOWNLOAD_SUCCESSFUL", "SUCCESS");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //hideProgressDialog();
                                flag = false;
                                publishProgress(flag);
                                Log.d("EXCEPTION_DOWNLOAD", exception.getMessage());

                            }
                        });
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(newFile);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Uh-oh, an error occurred!
                    //flag = false;
                    Log.d("DOWNLOAD_DIR", e.getMessage());
                }
            });
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
//        if (values[0]) {
//            Toast.makeText(contextCha, "Download thành công", Toast.LENGTH_LONG).show();
//        }
//        else {
//            Toast.makeText(contextCha, "Download thất bại", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        Log.d("ORDER", "3");
        //Log.d("KQTAI", result.toString());
//        if (flag) {
//            Toast.makeText(contextCha, "Download thành công", Toast.LENGTH_LONG).show();
//        }
//        else {
//            Toast.makeText(contextCha, "Download thất bại", Toast.LENGTH_LONG).show();
//        }
        hideProgressDialog();
//        try {
//            Thread.sleep(3000);
//        } catch (Exception e) {
//
//        }
        //contextCha.finish();
        //contextCha.startActivity(contextCha.getIntent());
        Toast.makeText(contextCha, "Thực hiện Download đã xong!", Toast.LENGTH_LONG).show();


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
