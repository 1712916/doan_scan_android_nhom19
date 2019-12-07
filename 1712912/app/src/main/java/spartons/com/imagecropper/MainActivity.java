package spartons.com.imagecropper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import spartons.com.imagecropper.enums.ImagePickerEnum;
import spartons.com.imagecropper.listeners.IImagePickerLister;
import spartons.com.imagecropper.utils.FileUtils;
import spartons.com.imagecropper.utils.UiHelper;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements IImagePickerLister {

    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
    public static final int ONLY_CAMERA_REQUEST_CODE = 612;
    public static final int ONLY_STORAGE_REQUEST_CODE = 613;
    public static final int ROTATE_ACTION_REQUEST_CODE = 100;
    public static final int OPACITY_ACTION_REQUEST_CODE = 101;

    private String currentPhotoPath = "";
    private UiHelper uiHelper = new UiHelper();
    private ImageView imageView;
    public Uri sourceUri, destinationUri;
    Bitmap bitmap, bmp;
    private int degree = 0;
    String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.selectPictureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (uiHelper.checkSelfPermissions(MainActivity.this))
                        uiHelper.showImagePickerDialog(MainActivity.this, MainActivity.this);
            }
        });
        imageView = findViewById(R.id.imageView);
        findViewById(R.id.cropButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCropActivity(sourceUri, destinationUri);
            }
        });
        findViewById(R.id.rotateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(MainActivity.this, RotateActivity.class);
                data.putExtra("uri", sourceUri.toString());
//                data.putExtra("currentPhotoPath", currentPhotoPath);
                startActivityForResult(data, ROTATE_ACTION_REQUEST_CODE);
                try {
                    File file = getImageFile();
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    //bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), sourceUri);
                    //Matrix matrix = new Matrix();
                } catch (Exception e) {

                }
            }
        });

        findViewById(R.id.opacityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(MainActivity.this, OpacityActivity.class);
                data.putExtra("uri", sourceUri.toString());
                startActivityForResult(data, OPACITY_ACTION_REQUEST_CODE);
            }
        });

//        gauge = findViewById(R.id.seekBar);
//        gauge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                rotateImage( Float.parseFloat("" + seekBar.getProgress()) - 45f);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                //rotateImage( Float.parseFloat("" + seekBar.getProgress()) - 45f);
//                //txt.setText("" + seekBar.getProgress());
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void rotateImage(float angle) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), sourceUri);
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(bmp);
            //learn content provider for more info
//            OutputStream os = MainActivity.this.getContentResolver().openOutputStream(sourceUri);
//
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
//            os.flush();
//            os.close();
        } catch (Exception e) {

        }
//        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
//        matrix.postRotate(20f, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
//        imageView.setImageMatrix(matrix);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, this);
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                uiHelper.toast(this, "ImageCropper needs Storage access in order to store your profile picture.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                uiHelper.toast(this, "ImageCropper needs Camera access in order to take profile picture.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                uiHelper.toast(this, "ImageCropper needs Camera and Storage access in order to take profile picture.");
                finish();
            }
        } else if (requestCode == ONLY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, this);
            else {
                uiHelper.toast(this, "ImageCropper needs Camera access in order to take profile picture.");
                finish();
            }
        } else if (requestCode == ONLY_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, this);
            else {
                uiHelper.toast(this, "ImageCropper needs Storage access in order to store your profile picture.");
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            //openCropActivity(uri, uri);
            sourceUri = uri;
            destinationUri = uri;
            showImage(uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = UCrop.getOutput(data);
                showImage(uri);
            }
        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                sourceUri = data.getData();
                File file = getImageFile();
                destinationUri = Uri.fromFile(file);
                //openCropActivity(sourceUri, destinationUri);
                showImage(sourceUri);


            } catch (Exception e) {
                uiHelper.toast(this, "Please select another image");
            }
        } else if (requestCode == ROTATE_ACTION_REQUEST_CODE && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.equals("ok")) {
                Bitmap bitmap = (Bitmap) data.getParcelableExtra("bitmap");
                imageView.setImageBitmap(bitmap);
            }
        } else if (requestCode == OPACITY_ACTION_REQUEST_CODE && requestCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.equals("ok")) {
                Bitmap bitmap = (Bitmap) data.getParcelableExtra("bitmap");
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);
    }

    public void showImage(Uri imageUri) {
        try {
            File file;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                file = FileUtils.getFile(this, imageUri);
            } else {
                file = new File(currentPhotoPath);
            }
            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            uiHelper.toast(this, "Please select different profile picture.");
        }
    }

    @Override
    public void onOptionSelected(ImagePickerEnum imagePickerEnum) {
        if (imagePickerEnum == ImagePickerEnum.FROM_CAMERA)
            openCamera();
        else if (imagePickerEnum == ImagePickerEnum.FROM_GALLERY)
            openImagesDocument();
    }

    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file;
        try {
            file = getImageFile(); // 1
        } catch (Exception e) {
            e.printStackTrace();
            uiHelper.toast(this, "Please take another image");
            return;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        fileName = imageFileName;
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(100, 100)
                .withAspectRatio(5f, 5f)
                .start(this);
    }
}