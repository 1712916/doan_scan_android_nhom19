package com.example.mayscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ItemRow> List;

    public GridViewAdapter(Context context, int layout, List<ItemRow> List) {
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
        ImageView imgIcon;
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
            holder.imgIcon=(ImageView)view.findViewById(R.id.imgv_grid_item);

            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)view.getTag();
        }
        ItemRow itemRow;

        itemRow=List.get(i);
        Glide.with(context)
                .load(itemRow.getUri())
                .placeholder(R.drawable.placeholder_image)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into( holder.imgIcon);

        return view;
    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }
}
