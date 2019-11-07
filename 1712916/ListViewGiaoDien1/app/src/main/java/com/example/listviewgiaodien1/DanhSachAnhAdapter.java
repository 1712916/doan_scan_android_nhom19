package com.example.listviewgiaodien1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.listviewgiaodien1.Anh;

import java.util.List;

public class DanhSachAnhAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Anh> List;

    public DanhSachAnhAdapter(Context context, int layout, List<Anh> List) {
        this.context = context;
        this.layout = layout;
        this.List = List;
    }

    @Override
    public int getCount() {
        return List.size();
    }

    @Override
    public Object getItem(int i) {
        return List.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        ImageView imgHinh;
        TextView txtTen;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view==null)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(layout,null);
            holder=new ViewHolder();
            //ánh xạ view
            holder.txtTen=(TextView)view.findViewById(R.id.textviewTen);
            holder.imgHinh=(ImageView)view.findViewById(R.id.imageviewHinh);
            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)view.getTag();
        }


        //gán giá trị
        Anh anh=List.get(i);
        holder.txtTen.setText(anh.getTen());

        holder.imgHinh.setImageResource(anh.getDiaChi());
        return view;
    }
}
