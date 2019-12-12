package com.example.mayscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OcrActivity extends Activity {
    TextRecognizer textRecognizer;
    String content;
    String fileName;
    EditText editText;
    Button btnSave,btnShare,btnClear;
    Bitmap bitmap;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_activity);


        editText=(EditText)findViewById(R.id.edt_text);
        btnSave=(Button)findViewById(R.id.btn_save_text);
        btnClear=(Button)findViewById(R.id.btn_clear_text);
        btnShare=(Button)findViewById(R.id.btn_share_text);

        recvData();
        bitmap=uriToBitmap(uri);
        AsyncToReadTextByOCR asyncToReadTextByOCR=new AsyncToReadTextByOCR();
        asyncToReadTextByOCR.execute();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content=editText.getText().toString();

                if(fileName.equals(""))
                {
                    fileName=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                }
                fileName=fileName+".TXT";
                saveText(fileName,content);

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SEND); intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, messageText);
                String chooserTitle = "Share Text From ScanPDF";
                Intent chosenIntent = Intent.createChooser(intent, chooserTitle);
                startActivity(chosenIntent);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });



    }

    public void recvData(){

        Intent intent=getIntent();

        if(intent!=null)
        {
            uri = Uri.parse(intent.getStringExtra("URI"));
            fileName=intent.getStringExtra("FILENAME");


        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults){
        if(requestCode==100)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getBaseContext(),"Cho phép lưu",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getBaseContext(),"Không cho phép lưu",Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    void saveText(String filename, String content)
    {


        //tạo file
        File file = Environment.getExternalStorageDirectory();
        File directory=new File(file.getAbsolutePath()+"/ScanPDF/TextOCR");
        directory.mkdirs();
        File outFile=new File(directory,filename);
        try{
            FileOutputStream fos=new FileOutputStream(outFile);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(getBaseContext(),"ScanPDF/TextOCR/"+filename,Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Không tìm thấy file",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Lưu file Text thất bại",Toast.LENGTH_LONG).show();
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

    private String OCR(){
        Context context = getApplicationContext();
        // TODO: Create the TextRecognizer
        Detector textRecognizer = new TextRecognizer.Builder(context).build();
        // TODO: Set the TextRecognizer's Processor.

        // TODO: Check if the TextRecognizer is operational.

        if (!textRecognizer.isOperational()) {
            Toast.makeText(getBaseContext(),"Lấy Text thất bại",Toast.LENGTH_LONG).show();

        }
        else {

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            //get text from sb until there is no text
            for (int i = 0; i < items.size(); i++) {
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                // sb.append(" ");
            }
            return sb.toString();
        }
        return "";
    }
    private class AsyncToReadTextByOCR extends AsyncTask<Void,Void,Void>{
        String res;

        @Override
        protected void onPreExecute() {
            res=new String();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            res=OCR();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            editText.setText(res);
            super.onPostExecute(aVoid);
        }
    }
}
