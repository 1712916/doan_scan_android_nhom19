package com.example.mayscanner;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//extands Activity ko su dung duoc ham getSupportFragmentManager
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static MyPagerAdapter myPagerAdapter;

    private static final int REQUEST_ID_WRITE_PERMISSION = 200,REQUEST_ID_READ_PERMISSIO=2001;

    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;
    public AppCompatTextView txtUsername;
    public AppCompatTextView txtState;

    public FirebaseAuth mAuth;
    public GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    //public DefaultFragment defaultFragment;

    final int PICK_IMAGES = 1;
    final int TAKE_PHOTO = 2;
    public FragmentGrid fragmentImages;
    public FragmentList fragmentPdfs;
    public static ArrayList<ItemRow> array_view_images = new ArrayList<>();
    public static ArrayList<ItemRow> array_view_pdfs = new ArrayList<>();
    Uri source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Xin phép đọc, ghi file
        askPermissionToReadFile();
        askPermissionToWriteFile();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNavigation();

        View headerLayoutView = navigationView.getHeaderView(0);
        txtUsername = (AppCompatTextView)headerLayoutView.findViewById(R.id.txtUsername);
        txtState = (AppCompatTextView)headerLayoutView.findViewById(R.id.txtState);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController.getCurrentDestination().getId() != navController.getGraph().getStartDestination()) {
                    navController.navigate(navController.getGraph().getStartDestination());
                }
                else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        Log.d("ORDER", "01");
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
        myPagerAdapter.notifyDataSetChanged();
        Log.d("ORDER", "02");

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("ORDER", "03");
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

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                source=Uri.fromFile(photoFile);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    public File createPhotoFile() {
        File file = Environment.getExternalStorageDirectory();
        File directory = new File(file.getAbsolutePath() + "/ScanPDF/Images");
        directory.mkdirs();

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File newFile = new File(directory, fileName + ".JPG");
        File image = null;
        image = newFile;
        return image;
    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGES);
    }


    // Setting Up One Time Navigation
    private void setupNavigation() {
        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);

            drawerLayout = findViewById(R.id.drawer_layout);

            navigationView = findViewById(R.id.navigationView);

            navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

            NavigationUI.setupWithNavController(navigationView, navController);

            navigationView.setNavigationItemSelectedListener(this);

        } catch (Exception e) {
            Log.d("LOILOI", e.getMessage());
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(drawerLayout, Navigation.findNavController(this, R.id.nav_host_fragment));
    }

    @Override
    public void onBackPressed() {

        Log.d("CURRENT_FRAGMENT", "" + navController.getCurrentDestination().getId() + " " + R.id.signInFragment);
        Log.d("DEFAULT_FRAGMENT", "" + navController.getGraph().getStartDestination() + " " + R.id.defaultFragment);
        //Toast.makeText(MainActivity.this, "Back pressed", Toast.LENGTH_LONG).show();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (navController.getCurrentDestination().getId() != navController.getGraph().getStartDestination()) {
                navController.navigate(navController.getGraph().getStartDestination());
            }
        }
    }

    @Nullable
    @Override
    public ActionBar getActionBar() {
        Log.d("ACTIONBAR", "ACTIONBAR");
        return super.getActionBar();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.menuSignUp:
                navController.navigate(R.id.signUpFragment);
                break;

            case R.id.menuSignIn:
                navController.navigate(R.id.signInFragment);
                break;

            case R.id.menuSignOut:
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_LONG).show();
                updateUI();
                break;

            case R.id.menuVerify:
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
            case R.id.menuUpload:
                UploadAsyncTask uAT = new UploadAsyncTask(MainActivity.this);
                uAT.execute();
                break;
            case R.id.menuDownload:
                DownloadAsyncTask dAT = new DownloadAsyncTask(MainActivity.this);
                dAT.execute();
                break;

        }
        return true;
    }

    private File saveBitmap(Bitmap bm, String fileName) {
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = Environment.getExternalStorageDirectory();

        File directory = new File(file.getAbsolutePath() + "/ScanPDF/Images");
        directory.mkdirs();
        if (fileName.equals("")) {
            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            fileName=fileName+".JPG";
        }
        File newFile = new File(directory, fileName);


        try {
            //MediaScannerConnection.scanFile(getApplicationContext(), new String[]  {directory.getPath()} , new String[]{"image/*"}, null);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

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

    public static ArrayList<ItemRow> getArrImages(){
        return array_view_images;
    }
    public static ArrayList<ItemRow> getArrPdfs(){
        return array_view_pdfs;
    }

    public static void getFilePaths(ArrayList<ItemRow> array ,String fileName) {
        array.clear();
        //File dowloadsFolder= getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = Environment.getExternalStorageDirectory();
        File directory=new File(file.getAbsolutePath()+"/ScanPDF/"+fileName);
        directory.mkdirs();


        if(directory.exists())
        {
            //Toast.makeText(getActivity(),"Load dữ Images liệu thành công!",Toast.LENGTH_LONG).show();
            File[] files=directory.listFiles();
            for(int i=0;i<files.length;i++){
                File z=files[i];
                ItemRow item=new ItemRow();

                item.setText(z.getName()); //lay ten cua tep
                item.setUri(Uri.fromFile(z)); //lay uri cua tep

                array.add(item);
            }
        }
    }

    public static ProgressDialog mProgressDialog;

    public void showProgressDialog(String message) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
            }
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } catch (Exception e) {
            Log.d("SHOWDIALOG", e.getMessage());
        }
    }

    public void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.d("HIDEDIALOG", e.getMessage());
        }
    }

    public void updateUI() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String s = "Xin chào " + mAuth.getCurrentUser().getEmail() + "\n";
            txtUsername.setText(s);
            if (mAuth.getCurrentUser().isEmailVerified())
                s = "Đã xác minh email";
            else
                s = "Chưa xác minh email";
            txtState.setText(s);
        }
        else {
            txtUsername.setText("Bạn chưa đăng nhập");
            txtState.setText("");
        }
        if (mAuth.getCurrentUser() == null) {
            navigationView.getMenu().findItem(R.id.menuSignUp).setVisible(true);
            navigationView.getMenu().findItem(R.id.menuSignIn).setVisible(true);
            navigationView.getMenu().findItem(R.id.menuVerify).setVisible(false);
            navigationView.getMenu().findItem(R.id.menuUpload).setVisible(false);
            navigationView.getMenu().findItem(R.id.menuDownload).setVisible(false);
            navigationView.getMenu().findItem(R.id.menuSignOut).setVisible(false);
        }
        else {
            navigationView.getMenu().findItem(R.id.menuSignUp).setVisible(false);
            navigationView.getMenu().findItem(R.id.menuSignIn).setVisible(false);
            navigationView.getMenu().findItem(R.id.menuUpload).setVisible(true);
            navigationView.getMenu().findItem(R.id.menuDownload).setVisible(true);
            navigationView.getMenu().findItem(R.id.menuSignOut).setVisible(true);
            if (mAuth.getCurrentUser().isEmailVerified()) {
                navigationView.getMenu().findItem(R.id.menuVerify).setVisible(false);
            }
            else {
                navigationView.getMenu().findItem(R.id.menuVerify).setVisible(true);
            }
        }

    }



    private void askPermissionToWriteFile() {
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        if (!canWrite) {
            Toast.makeText(this, "Bạn cần xin phép để ghi File!",Toast.LENGTH_LONG).show();
        }
    }
    private void askPermissionToReadFile() {
        boolean canRead = this.askPermission(REQUEST_ID_READ_PERMISSIO,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        //
        if (!canRead) {
            Toast.makeText(this, "Bạn cần xin phép để đọc File!",Toast.LENGTH_LONG).show();
        }
    }


    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}