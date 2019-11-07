package com.example.mycustomimageview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    MyCustomView myCustomView;
    ImageView test;
    Button btnUndo,btnSave;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myCustomView=new MyCustomView(this);
        myCustomView=(MyCustomView)findViewById(R.id.myview);

        btnUndo=(Button)findViewById(R.id.btnUndo);
        btnSave=(Button)findViewById(R.id.btnSave);
        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.win);
        myCustomView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.win));
        test=(ImageView)findViewById(R.id.test);
        test.setImageBitmap(bitmap);
        myCustomView.setmBitmap(bitmap);

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCustomView.onClickUndo();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.setImageBitmap(mergeToPin(bitmap,myCustomView.getmBitmap()));
            }
        });



    }
    public static Bitmap mergeToPin(Bitmap back, Bitmap front) {
        Bitmap result = Bitmap.createBitmap(back.getWidth(), back.getWidth(), back.getConfig());
        Canvas canvas = new Canvas(result);
        int widthBack = back.getWidth();
        int widthFront = front.getWidth();
        float move = (widthBack - widthFront) / 2;

        canvas.drawBitmap(back, 0,0, null);
        canvas.drawBitmap(front, 0, 0, null);
        return result;
    }
}
