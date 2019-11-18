package com.example.listviewgiaodien1;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class Activity2 extends AppCompatActivity {

    ImageView imgView2;
    TextView txtViewTenContent2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        imgView2=(ImageView) findViewById(R.id.imgView_OnClick);
       txtViewTenContent2=(TextView)findViewById(R.id.aa);

    Intent intent=getIntent();
        imgView2.setImageURI(Uri.parse(intent.getStringExtra("URI")));

    }

    public void setDataBundle(){

        Intent intent=getIntent();

       Bundle bundle=intent.getExtras();
        if(bundle!=null)
        {

        }


    }
}
