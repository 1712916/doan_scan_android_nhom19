package com.example.mayscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
                                Toast.makeText(MainActivity.this, "Bạn đang trong trạng thái đăng nhập",
                                        Toast.LENGTH_LONG).show();
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
                                Toast.makeText(MainActivity.this, "Bạn đang trong trạng thái đăng nhập",
                                        Toast.LENGTH_LONG).show();
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
                                Toast.makeText(MainActivity.this, "Bạn đã xác minh email, không cần xác minh nữa",
                                        Toast.LENGTH_LONG).show();
                                break;
                            }
                            final FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(MainActivity.this,
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // [START_EXCLUDE]
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this,
                                                        "Đã gửi xác minh đến email " + user.getEmail(),
                                                        Toast.LENGTH_LONG).show();

                                            } else {
                                                Log.e("FAILURE", "sendEmailVerification", task.getException());
                                                Toast.makeText(MainActivity.this, "Thất bại khi gửi xác minh.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            // [END_EXCLUDE]
                                        }
                                    });
                            // [END send_email_verification]

                            break;
                        case R.id.menuLogInWithGoogle:
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                Toast.makeText(MainActivity.this, "Bạn đang trong trạng thái đăng nhập",
                                        Toast.LENGTH_LONG).show();
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

                            File file = Environment.getExternalStorageDirectory();
                            String[] uploadedDirectoryName = { "/Images", "/TextOCR", "/PDFs" };
                            for (int idx = 0; idx < 3; idx++) {
                                File directory = new File(file.getPath() + "/MyScanPDF" + uploadedDirectoryName[idx]);
                                Log.d("fullpath", file.getPath() + "/MyScanPDF" + uploadedDirectoryName[idx]);
                                if (directory.exists()) {
                                    StorageReference mStorageRef;
                                    mStorageRef = FirebaseStorage.getInstance()
                                            .getReferenceFromUrl("gs://scanpdf-92556.appspot.com/"
                                                    + mAuth.getCurrentUser().getEmail() + uploadedDirectoryName[idx]);

                                    // Toast.makeText(getActivity(),"Load dữ Images liệu thành
                                    // công!",Toast.LENGTH_LONG).show();
                                    File[] files = directory.listFiles();
                                    for (int i = 0; i < files.length; i++) {
                                        File z = files[i];
                                        Uri mFileUri = Uri.fromFile(z);
                                        StorageReference mIslandRef = mStorageRef.child(mFileUri.getLastPathSegment());
                                        mIslandRef.putFile(mFileUri)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // Get a URL to the uploaded content
                                                        Toast.makeText(MainActivity.this, "Upload thành công",
                                                                Toast.LENGTH_LONG).show();
                                                        Log.d("UPLOAD_SUCCESSFUL", "SUCCESS");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(MainActivity.this, "Upload thất bại",
                                                                Toast.LENGTH_LONG).show();
                                                        Log.d("EXCEPTION_UPLOAD", exception.getMessage());
                                                    }
                                                });
                                    }
                                }
                            }

                            break;
                        case R.id.menuDownload:
                            if (mAuth.getCurrentUser() == null) {
                                Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();
                                break;
                            }

                            File file2 = Environment.getExternalStorageDirectory();
                            String[] downloadedDirectoryName = { "/Images", "/TextOCR", "/PDFs" };
                            for (int idx = 0; idx < 3; idx++) {
                                File directory2 = new File(
                                        file2.getPath() + "/MyScanPDF" + downloadedDirectoryName[idx]);
                                directory2.mkdirs();
                                StorageReference listRef = FirebaseStorage.getInstance()
                                        .getReferenceFromUrl("gs://scanpdf-92556.appspot.com/"
                                                + mAuth.getCurrentUser().getEmail() + downloadedDirectoryName[idx]);
                                listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        for (StorageReference prefix : listResult.getPrefixes()) {
                                            // All the prefixes under listRef.
                                            // You may call listAll() recursively on them.
                                        }

                                        for (StorageReference item : listResult.getItems()) {
                                            // All the items under listRef.
                                            String fileName = item.getName();
                                            Log.d("tenfile", fileName);
                                            File newFile = new File(directory2, fileName);

                                            item.getFile(newFile).addOnSuccessListener(
                                                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(
                                                                FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                            // Get a URL to the uploaded content
                                                            Toast.makeText(MainActivity.this, "Download thành công",
                                                                    Toast.LENGTH_LONG).show();
                                                            Log.d("DOWNLOAD_SUCCESSFUL", "SUCCESS");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            Toast.makeText(MainActivity.this, "Download thất bại",
                                                                    Toast.LENGTH_LONG).show();
                                                            Log.d("EXCEPTION_DOWNLOAD", exception.getMessage());

                                                        }
                                                    });
                                            FileOutputStream fileOutputStream = null;
                                            try {
                                                fileOutputStream = new FileOutputStream(newFile);
                                                fileOutputStream.flush();
                                                fileOutputStream.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Uh-oh, an error occurred!
                                        Log.d("DOWNLOAD_DIR", e.getMessage());
                                    }
                                });
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
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
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
        File file = Environment.getExternalStorageDirectory();
        File directory = new File(file.getAbsolutePath() + "/ScanPDF/Images");
        directory.mkdirs();

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File newFile = new File(directory, fileName + ".JPG");
        File image = null;
        image = newFile;
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
                    File file;
                    source = data.getData();

                    file=saveBitmap(uriToBitmap(source),"");
                    source=Uri.fromFile(file);


                    Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                    intent.putExtra("URI", source.toString());
                    intent.putExtra("FILENAME", file.getName());
                    startActivity(intent);

                    break;
                }
                case TAKE_PHOTO: {
                    // Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                    Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                    intent.putExtra("URI", source.toString());
                    intent.putExtra("FILENAME", new File(source.getPath()).getName());
                    startActivity(intent);

                    break;
                }


            }

        }

    }
    private File saveBitmap(Bitmap bm, String fileName) {
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = Environment.getExternalStorageDirectory();

        File directory = new File(file.getAbsolutePath() + "/ScanPDF/Images");
        directory.mkdirs();
        if (fileName.equals("")) {
            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        File newFile = new File(directory, fileName);


        try {
            //MediaScannerConnection.scanFile(getApplicationContext(), new String[]  {directory.getPath()} , new String[]{"image/*"}, null);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(getApplication(),
                    "Save Bitmap: " + fileOutputStream.toString(),
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),
                    "Something wrong: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),
                    "Something wrong: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        return newFile;
    }
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image=null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
