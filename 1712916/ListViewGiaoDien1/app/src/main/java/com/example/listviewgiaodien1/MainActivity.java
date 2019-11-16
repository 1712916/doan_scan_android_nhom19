package com.example.listviewgiaodien1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String IMGHINH="HINH";
    public static final String TEN="TEN";
    public static final String BUNDLE="BUNDLE";
    //ListView lvAnh;
    GridView lvAnh;
    ArrayList<ItemRow> arrayAnh;
    MyAdapter adapter;
    ImageButton btnCamera,btnLoadImage;
    final int PICK_IMAGES = 1;
    Uri source;
    Bitmap pickImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Anhxa();

        btnCamera=(ImageButton)findViewById(R.id.btn_camera);
        btnLoadImage=(ImageButton)findViewById(R.id.btn_load_images);

        adapter=new MyAdapter(this,R.layout.item_row,arrayAnh);
        lvAnh.setAdapter(adapter);
        lvAnh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ItemRow itemRow;
                itemRow=new ItemRow(arrayAnh.get(i).getText(),arrayAnh.get(i).getIcon());
                byBundle(itemRow.getText(),itemRow.getIcon());


            }
        });

/**************************************************/
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
                  try {
                      pickImage = BitmapFactory.decodeStream(
                              getContentResolver().openInputStream(source));

                  } catch (FileNotFoundException e) {
                      e.printStackTrace();
                  }

                 //add bitmap mới vào gridview image


                  break;
              }
          }



      }


  }
  /******************************************************/




    public void byBundle(String ten,int diaChi){
        Intent intent=new Intent(MainActivity.this, Activity2.class);
        Bundle bundle=new Bundle();
        bundle.putString(TEN,ten);
        bundle.putInt(IMGHINH,diaChi);
        intent.putExtras(bundle);

        startActivity(intent);
    }
    void Anhxa(){
        //lvAnh =(ListView) findViewById(R.id.listviewAnh);
        lvAnh =(GridView) findViewById(R.id.listviewAnh);
        arrayAnh=new ArrayList<>();
        arrayAnh.add(new ItemRow("123123",R.drawable.bo));
        arrayAnh.add(new ItemRow("123123123",R.drawable.buoi));
        arrayAnh.add(new ItemRow("123123",R.drawable.chery));
        arrayAnh.add(new ItemRow("123123",R.drawable.icons_pdf));
        arrayAnh.add(new ItemRow("554654",R.drawable.kiwi));
        arrayAnh.add(new ItemRow("456456",R.drawable.nhan));
        arrayAnh.add(new ItemRow("456456",R.drawable.mangcuoc));
        arrayAnh.add(new ItemRow("456456456",R.drawable.sung));
        arrayAnh.add(new ItemRow("456456456",R.drawable.icons_pdf));
        arrayAnh.add(new ItemRow("456456456",R.drawable.tao));
        arrayAnh.add(new ItemRow("456456456",R.drawable.icons_pdf));
        arrayAnh.add(new ItemRow("456456456",R.drawable.icons_pdf));
        arrayAnh.add(new ItemRow("456456456",R.drawable.icons_pdf));
        arrayAnh.add(new ItemRow("456456456",R.drawable.icons_pdf));





    }
}
