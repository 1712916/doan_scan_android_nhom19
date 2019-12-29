package com.example.mayscanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DefaultFragment extends Fragment {

    private MainActivity mainActivity;
    public ViewPager viewPager;
    ImageButton btnCamera, btnLoadImage;


    private ArrayList<Integer> arr;
    Bitmap bitmapFromGallery;
    private String pathToFile;

    int mState = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("ORDER", "04");
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ORDER", "05");
//        //Toast.makeText(mainActivity, "kakaka", Toast.LENGTH_LONG).show();
        View view = inflater.inflate(R.layout.default_fragment, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        // MyCustomPagerAdapter myCustomPagerAdapter=new MyCustomPagerAdapter(this,arr);

        MainActivity.hideKeyboard(mainActivity);
        mainActivity.updateUI();

        try {
            MainActivity.getFilePaths(MainActivity.array_view_images, "Images");
            MainActivity.getFilePaths(MainActivity.array_view_pdfs, "PDFs");

            mainActivity.fragmentImages = new FragmentGrid(MainActivity.array_view_images);
            mainActivity.fragmentPdfs = new FragmentList(MainActivity.array_view_pdfs);

            MainActivity.myPagerAdapter = new MyPagerAdapter(mainActivity.getSupportFragmentManager(), mainActivity.fragmentImages, mainActivity.fragmentPdfs);
            viewPager.setAdapter(MainActivity.myPagerAdapter);


            //MainActivity.myPagerAdapter.notifyDataSetChanged();


            TabLayout tabLayout = view.findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            Log.d("LOI KHOI DONG", e.getMessage());
        }
        //MainActivity.myPagerAdapter.notifyDataSetChanged();


        btnLoadImage = (ImageButton) view.findViewById(R.id.btn_load_images);
        btnCamera = (ImageButton) view.findViewById(R.id.btn_camera);

        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openGallery();

            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.dispatchTakePictureIntent();
            }
        });
        return view;
    }


}

