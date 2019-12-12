package com.example.mayscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.FileNotFoundException;
import java.util.ArrayList;

//extands Activity ko su dung duoc ham getSupportFragmentManager
public class MainActivity extends FragmentActivity {
    ViewPager viewPager;
    ImageButton btnCamera,btnLoadImage;
    final int PICK_IMAGES = 1;
    private ArrayList<Integer> arr;
    Uri source;
    Bitmap bitmapFromGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       viewPager = (ViewPager) findViewById(R.id.viewpager);
       // MyCustomPagerAdapter myCustomPagerAdapter=new MyCustomPagerAdapter(this,arr);
        MyPagerAdapter myPagerAdapter=new MyPagerAdapter( getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        btnLoadImage=(ImageButton)findViewById(R.id.btn_load_images);

        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();


            }
        });
    }



    private void openGallery(){
        Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGES);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK ){
            switch (requestCode) {
                case PICK_IMAGES: {
                    source = data.getData();


/*
                    try { //lay bitmap from uri
                        bitmapFromGallery = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }*/

                    //add bitmap mới vào gridview image
                Intent intent=new Intent(getApplicationContext(),EditActivity.class);
                intent.putExtra("URI", source.toString());
                intent.putExtra("POSITION",-1);
                startActivity(intent);

                    break;
                }
            }



        }


    }
}


