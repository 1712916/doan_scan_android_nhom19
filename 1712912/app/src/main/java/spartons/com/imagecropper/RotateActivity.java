package spartons.com.imagecropper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import spartons.com.imagecropper.utils.FileUtils;
import spartons.com.imagecropper.utils.UiHelper;

public class RotateActivity extends AppCompatActivity {

    Uri sourceUri;
    String currentPhotoPath;
    ImageView imageView;
    Button cancelButton, okButton;
    Bitmap bitmap, bmp;
    private SeekBar gauge;
    private UiHelper uiHelper = new UiHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);
        imageView = findViewById(R.id.imageView2);
        Intent data = getIntent();
        sourceUri = Uri.parse(data.getStringExtra("uri"));
        showImage(sourceUri);

        gauge = findViewById(R.id.seekBar);
        cancelButton = findViewById(R.id.cancelButton);
        okButton = findViewById(R.id.okButton);

        gauge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rotateImage( Float.parseFloat("" + seekBar.getProgress()) - 45f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //rotateImage( Float.parseFloat("" + seekBar.getProgress()) - 45f);
                //txt.setText("" + seekBar.getProgress());
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "cancel");
                RotateActivity.this.setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                try {
                    File file = getImageFile();
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    //destinationUri = Uri.fromFile(file);
                    //showImage(destinationUri);
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(RotateActivity.this.getContentResolver(), destinationUri);

                    intent.putExtra("result", "ok");
                    intent.putExtra("bitmap", bmp);
                    RotateActivity.this.setResult(Activity.RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    uiHelper.toast(RotateActivity.this, "Please select another image");
                }
            }
        });

        //bitmapOrg = BitmapFactory.decodeFile(currentPhotoPath);


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

    private void rotateImage(float angle) {
        //Bitmap scale = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.getWidth(), bitmapOrg.getHeight(), true);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(RotateActivity.this.getContentResolver(), sourceUri);
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(bmp);
        } catch (Exception e) {
            okButton.setText(e.getMessage().toString());
            return;
        }
//        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
//        matrix.postRotate(20f, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
//        imageView.setImageMatrix(matrix);
        //imageView.setRotation(angle);

    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
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
}
