package com.example.listviewgiaodien1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String IMGHINH="HINH";
    public static final String TEN="TEN";
    public static final String BUNDLE="BUNDLE";
    ListView lvAnh;
    ArrayList<Anh> arrayAnh;
    DanhSachAnhAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Anhxa();

        adapter=new DanhSachAnhAdapter(this,R.layout.itemp_anh,arrayAnh);
        lvAnh.setAdapter(adapter);
        lvAnh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Anh anh;
                anh=new Anh(arrayAnh.get(i).getTen(),arrayAnh.get(i).getDiaChi());
                byBundle(anh.getTen(),anh.getDiaChi());


            }
        });
    }

    public void byBundle(String ten,int diaChi){
        Intent intent=new Intent(MainActivity.this, Activity2.class);
        Bundle bundle=new Bundle();
        bundle.putString(TEN,ten);
        bundle.putInt(IMGHINH,diaChi);
        intent.putExtras(bundle);

        startActivity(intent);
    }
    void Anhxa(){
        lvAnh =(ListView) findViewById(R.id.listviewAnh);
        arrayAnh=new ArrayList<>();
        arrayAnh.add(new Anh("Bơ",R.drawable.bo));
        arrayAnh.add(new Anh("Bưởi",R.drawable.buoi));
        arrayAnh.add(new Anh("Cherry",R.drawable.chery));
        arrayAnh.add(new Anh("Kiwi",R.drawable.kiwi));
        arrayAnh.add(new Anh("Nhãn",R.drawable.nhan));
        arrayAnh.add(new Anh("Măng cuộc",R.drawable.mangcuoc));
        arrayAnh.add(new Anh("Sung",R.drawable.sung));
        arrayAnh.add(new Anh("Táo",R.drawable.tao));





    }
}
