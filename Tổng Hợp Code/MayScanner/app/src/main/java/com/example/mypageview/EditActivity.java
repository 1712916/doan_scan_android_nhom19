package com.example.mypageview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class EditActivity extends Activity {

    ImageView imgvEdit;
    Button btnToEdit;
    Uri uri;
    int postition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        btnToEdit=(Button)findViewById(R.id.btnToEdit);
        btnToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),DrawActivity.class);

                startActivity(intent);
            }
        });

        imgvEdit=(ImageView) findViewById(R.id.imgv_edit);
        recvData();



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
}
