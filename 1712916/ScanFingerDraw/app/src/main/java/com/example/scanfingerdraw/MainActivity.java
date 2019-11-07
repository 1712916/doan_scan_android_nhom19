package com.example.scanfingerdraw;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    com.example.draw.DrawingView dv ; //Nắm giữ các thuộc tính khi vẽ ở nơi được "chọn"
    ImageView imgView;
    Button btnChooseColor,btnUndo,btnThemAnh,btnSave;
    Bitmap myImage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgView=(ImageView)findViewById(R.id.img_view);
        myImage= BitmapFactory.decodeResource(getResources(), R.drawable.win);

        //Set cac thuoc tinh va noi de ve
        dv = new com.example.draw.DrawingView(this);
        final LinearLayout mDrawingPad=(LinearLayout)findViewById(R.id.abc); //xác định vị trí được "chọn" để vẽ
        mDrawingPad.addView(dv);
        //mDrawingPad.addView(imgView);
        //************
        btnThemAnh=(Button)findViewById(R.id.btnAddImg);
        btnThemAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingPad.setBackgroundResource(R.drawable.win);
                // imgView.setImageResource(R.drawable.win);
                imgView.setImageBitmap(myImage);
            }
        });
        btnChooseColor=(Button)findViewById(R.id.btnChooseColor);
        btnChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.openColorPicker();
            }
        });

        btnUndo=(Button)findViewById(R.id.btnUndo);
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.onClickUndo();
            }
        });
        btnSave=(Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgView.setImageBitmap(mergeToPin(myImage,dv.getmBitmap()));


            }
        });

    }
    public static Bitmap mergeToPin(Bitmap back, Bitmap front) {
        Bitmap result = Bitmap.createBitmap(back.getWidth(), back.getHeight(), back.getConfig());
        Canvas canvas = new Canvas(result);
        int widthBack = back.getWidth();
        int widthFront = front.getWidth();
        float move = (widthBack - widthFront) / 2;
        canvas.drawBitmap(back, 0f, 0f, null);
        canvas.drawBitmap(front, move, move, null);
        return result;
    }
}
