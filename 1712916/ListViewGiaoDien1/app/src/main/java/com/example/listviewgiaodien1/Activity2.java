package com.example.listviewgiaodien1;


import android.content.Intent;
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

        imgView2=(ImageView) findViewById(R.id.imageViewHinh2);
        txtViewTenContent2=(TextView)findViewById(R.id.textViewTenContent2);


        setDataBundle();

    }

    public void setDataBundle(){

        Intent intent=getIntent();

        Bundle bundle=intent.getExtras();
        if(bundle!=null)
        {
            String ten=bundle.getString("TEN");
            int hinh=  bundle.getInt("HINH",0);

            txtViewTenContent2.setText(ten);
            imgView2.setImageResource(hinh);
        }


    }
}
