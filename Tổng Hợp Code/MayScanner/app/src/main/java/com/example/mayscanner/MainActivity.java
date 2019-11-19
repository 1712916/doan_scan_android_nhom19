package com.example.mayscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//extands Activity ko su dung duoc ham getSupportFragmentManager
public class MainActivity extends FragmentActivity {
    ViewPager viewPager;
    ImageButton btnCamera,btnLoadImage;
    final int PICK_IMAGES = 1;
    final int TAKE_PHOTO=2;
    private ArrayList<Integer> arr;
    Uri source;
    Bitmap bitmapFromGallery;
    private String pathToFile;

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
        btnCamera=(ImageButton)findViewById(R.id.btn_camera);
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();


            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile=null;
            photoFile=createPhotoFile();

            if(photoFile!=null){
                pathToFile=photoFile.getAbsolutePath();
                Uri photoUri= FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID,photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                source=photoUri;
                startActivityForResult(takePictureIntent,TAKE_PHOTO);
            }


        }
    }
    private File createPhotoFile()
    {
        String name= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String name="AAAA,jpg";
        File storageDir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=null;
        try {
            image=File.createTempFile(name,".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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
                Intent intent=new Intent(getApplicationContext(),EditActivity.class);
                intent.putExtra("URI", source.toString());
                intent.putExtra("FILENAME","");
                startActivity(intent);

                    break;
                }
                case TAKE_PHOTO: {
                    //Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                    Intent intent=new Intent(getApplicationContext(),EditActivity.class);
                    intent.putExtra("URI", source.toString());
                    intent.putExtra("FILENAME","");
                    startActivity(intent);

                    break;
                }
            }



        }


    }
}


