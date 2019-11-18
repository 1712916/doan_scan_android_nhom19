package com.example.mayscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileDescriptor;
import java.io.IOException;

public class EditActivity extends Activity {

    ImageView imgvEdit;
    Button btnToEdit,btnOcr;
    Uri uri;
    Bitmap bitmap;
    int postition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        btnToEdit=(Button)findViewById(R.id.btnToEdit);
        btnOcr=(Button)findViewById(R.id.btnOcr);


        btnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getApplicationContext();
                // TODO: Create the TextRecognizer
                Detector textRecognizer = new TextRecognizer.Builder(context).build();
                // TODO: Set the TextRecognizer's Processor.

                // TODO: Check if the TextRecognizer is operational.

                if (!textRecognizer.isOperational()) {
                    Toast.makeText(getBaseContext(),"Lấy Text thất bại",Toast.LENGTH_LONG).show();

                }
                else{

                    Frame frame= new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items=textRecognizer.detect(frame);
                    StringBuilder sb=new StringBuilder();
                    //get text from sb until there is no text
                    for(int i=0;i<items.size();i++){
                        TextBlock myItem=items.valueAt(i);
                        sb.append(myItem.getValue());
                        // sb.append(" ");
                    }


                    Intent intent=new Intent(getApplicationContext(),OcrActivity.class);
                    intent.putExtra("CONTENT",sb.toString());
                    Toast.makeText(getBaseContext(),"Lấy Text thành công",Toast.LENGTH_LONG).show();
                    startActivity(intent);



                }

            }
        });

        btnToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),DrawActivity.class);
                intent.putExtra("URI", uri.toString());
                intent.putExtra("POSITION",postition);
                startActivity(intent);
            }
        });

        imgvEdit=(ImageView) findViewById(R.id.imgv_edit);
        recvData();
        bitmap=uriToBitmap(uri);



    }
    public void recvData(){

        Intent intent=getIntent();
        if(intent!=null)
        {
            uri = Uri.parse(intent.getStringExtra("URI"));
            postition=intent.getIntExtra("POSITION",0);
            imgvEdit.setImageURI(uri);
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
