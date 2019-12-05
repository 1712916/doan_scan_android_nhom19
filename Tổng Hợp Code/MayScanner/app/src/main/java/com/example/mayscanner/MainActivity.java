package com.example.mayscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//extands Activity ko su dung duoc ham getSupportFragmentManager
public class MainActivity extends FragmentActivity {
    ViewPager viewPager;
    ImageButton btnCamera, btnLoadImage;
    final int PICK_IMAGES = 1;
    final int TAKE_PHOTO = 2;
    private ArrayList<Integer> arr;
    Uri source;
    Bitmap bitmapFromGallery;
    private String pathToFile;


    ImageButton btnMenu;
    TextView txtState;
    MyPagerAdapter myPagerAdapter;

    final int LOG_IN_REQUEST_CODE = 100;
    final int LOG_IN_WITH_GOOGLE_REQUEST_CODE = 101;
    final int SIGN_UP_REQUEST_CODE = 102;
    final int UPLOAD_REQUEST_CODE = 103;
    final int DOWNLOAD_REQUEST_CODE = 104;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // MyCustomPagerAdapter myCustomPagerAdapter=new MyCustomPagerAdapter(this,arr);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        btnLoadImage = (ImageButton) findViewById(R.id.btn_load_images);
        btnCamera = (ImageButton) findViewById(R.id.btn_camera);
        txtState = (TextView) findViewById(R.id.txtState);

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

        btnMenu = (ImageButton) findViewById(R.id.menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnMenu);
                getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuLogIn:
                                if (mAuth.getCurrentUser() != null) {
                                    Toast.makeText(MainActivity.this, "Bạn đang trong trạng thái đăng nhập", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                                MainActivity.this.startActivityForResult(intent, LOG_IN_REQUEST_CODE);
                                break;
                            case R.id.menuLogOut:
                                if (mAuth.getCurrentUser() == null) {
                                    Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                FirebaseAuth.getInstance().signOut();
                                mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                txtState.setText("Bạn chưa đăng nhập");
                                Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.menuSignUp:
                                if (mAuth.getCurrentUser() != null) {
                                    Toast.makeText(MainActivity.this, "Bạn đang trong trạng thái đăng nhập", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                Intent intent2 = new Intent(MainActivity.this, SignUpActivity.class);
                                MainActivity.this.startActivity(intent2);
                                break;
                            case R.id.menuVerify:
                                if (mAuth.getCurrentUser() == null) {
                                    Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(MainActivity.this, "Bạn đã xác minh email, không cần xác minh nữa", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                final FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification()
                                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // [START_EXCLUDE]
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this,
                                                            "Đã gửi xác minh đến email " + user.getEmail(),
                                                            Toast.LENGTH_LONG).show();

                                                } else {
                                                    Log.e("FAILURE", "sendEmailVerification", task.getException());
                                                    Toast.makeText(MainActivity.this,
                                                            "Thất bại khi gửi xác minh.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                // [END_EXCLUDE]
                                            }
                                        });
                                // [END send_email_verification]

                                break;
                            case R.id.menuLogInWithGoogle:
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    Toast.makeText(MainActivity.this, "Bạn đang trong trạng thái đăng nhập", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                Intent intent3 = new Intent(MainActivity.this, GoogleSignInActivity.class);
                                MainActivity.this.startActivityForResult(intent3, LOG_IN_WITH_GOOGLE_REQUEST_CODE);
                                break;

                            case R.id.menuUpload:
                                if (mAuth.getCurrentUser() == null) {
                                    Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
                                    break;
                                }

//                                Intent intent5 = new Intent(MainActivity.this, TextCloudActivity.class);
//                                MainActivity.this.startActivity(intent5);


                                //Intent intent5 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                Intent gallery = new Intent();
                                //intent5.setType("image/*");
                                gallery.setType("*/*");
                                gallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(gallery, UPLOAD_REQUEST_CODE);

                                break;
                            case R.id.menuDownload:
                                if (mAuth.getCurrentUser() == null) {
                                    Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
                                    break;
                                }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String s = "Xin chào " + mAuth.getCurrentUser().getEmail() + "\n";
            if (mAuth.getCurrentUser().isEmailVerified())
                s = s + "Đã xác minh email";
            else
                s = s + "Chưa xác minh email";
            txtState.setText(s);
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myPagerAdapter.notifyDataSetChanged();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                source = photoUri;
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String name="AAAA,jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGES);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGES: {
                    source = data.getData();
                    Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                    intent.putExtra("URI", source.toString());
                    intent.putExtra("FILENAME", "");
                    startActivity(intent);

                    break;
                }
                case TAKE_PHOTO: {
                    //Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                    Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                    intent.putExtra("URI", source.toString());
                    intent.putExtra("FILENAME", "");
                    startActivity(intent);

                    break;
                }
                case LOG_IN_REQUEST_CODE: {
                    break;
                }
                case LOG_IN_WITH_GOOGLE_REQUEST_CODE: {
                    break;
                }
                case UPLOAD_REQUEST_CODE: {
                    Uri mFileUri = data.getData();

                    Log.d("PATHNAME", mFileUri.toString());

                    if (mFileUri != null) {
                        StorageReference mStorageRef;

                        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://scanpdf-92556.appspot.com/" + mAuth.getCurrentUser().getEmail() + "/" + mFileUri.getLastPathSegment());
                        mStorageRef.child("hihihi");
                        mStorageRef.putFile(mFileUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        Toast.makeText(MainActivity.this, "Upload thành công", Toast.LENGTH_LONG).show();
                                        Log.d("UPLOAD_SUCCESS", "SUCCESS");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                        Toast.makeText(MainActivity.this, "Upload thất bại", Toast.LENGTH_LONG).show();
                                        Log.d("EXCEPTION_UPLOAD", exception.getMessage());
                                    }
                                });
                    }
                }

            }


        }


    }
}


