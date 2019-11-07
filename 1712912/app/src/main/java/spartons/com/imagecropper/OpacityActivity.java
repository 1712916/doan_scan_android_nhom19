package spartons.com.imagecropper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

public class OpacityActivity extends AppCompatActivity {
    Uri sourceUri;
    String currentPhotoPath;
    ImageView imageView;
    Button cancelButton, okButton;
    Bitmap sourceBitmap, destinationBitmap, tmpBitmap;
    private SeekBar alphaSeekBar, contrastSeekBar;
    private UiHelper uiHelper = new UiHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opacity);
        imageView = findViewById(R.id.imageView3);
        Intent data = getIntent();
        sourceUri = Uri.parse(data.getStringExtra("uri"));
        showImage(sourceUri);

        alphaSeekBar = findViewById(R.id.alphaSeekBar);
        contrastSeekBar = findViewById(R.id.contrastSeekBar);
        cancelButton = findViewById(R.id.cancelButton2);
        okButton = findViewById(R.id.okButton2);

        try {
            sourceBitmap = MediaStore.Images.Media.getBitmap(OpacityActivity.this.getContentResolver(), sourceUri);
            destinationBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), sourceBitmap.getConfig().ARGB_8888);
        } catch (Exception e) {

        }

        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                okButton.setText("" + progress);
                setAlphaImage(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setContrastImage(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "cancel");
                OpacityActivity.this.setResult(Activity.RESULT_OK, intent);
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
                    destinationBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    intent.putExtra("result", "ok");
                    intent.putExtra("bitmap", destinationBitmap);
                    OpacityActivity.this.setResult(Activity.RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    uiHelper.toast(OpacityActivity.this, "Please select another image");
                }
            }
        });

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

    private void setAlphaImage(int value) {
        Canvas canvas = new Canvas();
        canvas.setBitmap(destinationBitmap);
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(value);
        canvas.drawBitmap(sourceBitmap, 0, 0, alphaPaint);
        //imageView.setImageAlpha(value);
        imageView.setImageBitmap(destinationBitmap);
    }

    private void setContrastImage(double value)
    {
        // image size
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas canvas = new Canvas();
        canvas.setBitmap(destinationBitmap);

        // draw bitmap to bmOut from src bitmap so we can modify it
        canvas.drawBitmap(sourceBitmap, 0, 0, new Paint());


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = sourceBitmap.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                destinationBitmap.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        imageView.setImageBitmap(destinationBitmap);
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
