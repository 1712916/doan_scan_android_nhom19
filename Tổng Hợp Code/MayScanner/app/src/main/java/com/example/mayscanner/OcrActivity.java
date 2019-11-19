package com.example.mayscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
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
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_activity);


        editText=(EditText)findViewById(R.id.edt_text);
        btnSave=(Button)findViewById(R.id.btn_save_text);

        recvData();

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




    }

    public void recvData(){

        Intent intent=getIntent();
        if(intent!=null)
        {
            String getContent;
            getContent=intent.getStringExtra("CONTENT");
            fileName=intent.getStringExtra("FILENAME");
            editText.setText(getContent);
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
        File directory=new File(file.getAbsolutePath()+"/MyScanPDF/TextOCR");
        directory.mkdirs();
        File outFile=new File(directory,filename);
        try{
            FileOutputStream fos=new FileOutputStream(outFile);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(getBaseContext(),"Lưu file Text thành công",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Không tìm thấy file",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Lưu file Text thất bại",Toast.LENGTH_LONG).show();
        }


    }

}
