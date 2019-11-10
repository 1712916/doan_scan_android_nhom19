package com.example.ocr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {
    TextRecognizer textRecognizer;
    Bitmap bitmap;
    Button btnGetText;
    ImageView imgV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnGetText=(Button)findViewById(R.id.btn_get_text);
        imgV=(ImageView)findViewById(R.id.img_view);

        bitmap= BitmapFactory.decodeResource(getResources(),
                R.drawable.img_3);
        imgV.setImageBitmap(bitmap);
        btnGetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                // TODO: Create the TextRecognizer
                textRecognizer = new TextRecognizer.Builder(context).build();
                // TODO: Set the TextRecognizer's Processor.

                // TODO: Check if the TextRecognizer is operational.

                if (!textRecognizer.isOperational()) {
                    Toast.makeText(MainActivity.this,"LOI~~~",Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(MainActivity.this,"KO LOI~~~",Toast.LENGTH_LONG).show();
                    Frame frame= new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items=textRecognizer.detect(frame);
                    StringBuilder sb=new StringBuilder();
                    //get text from sb until there is no text
                    for(int i=0;i<items.size();i++){
                        TextBlock myItem=items.valueAt(i);
                        sb.append(myItem.getValue());
                       // sb.append(" ");
                    }


                    Intent intent=new Intent(MainActivity.this,NewActivity.class);
                    intent.putExtra("TEXT",sb.toString());
                    startActivity(intent);

                }

            }
        });


    }
}
