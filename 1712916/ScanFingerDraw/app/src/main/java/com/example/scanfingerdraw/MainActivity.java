package com.example.scanfingerdraw;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends Activity {
    DrawingView dv ; //Nắm giữ các thuộc tính khi vẽ ở nơi được "chọn"

    Button btnChooseColor,btnUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set cac thuoc tinh va noi de ve
        dv = new DrawingView(this);
        LinearLayout mDrawingPad=(LinearLayout)findViewById(R.id.view_drawing_pad); //xác định vị trí được "chọn" để vẽ
        mDrawingPad.addView(dv);
        //************

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

    }
}
