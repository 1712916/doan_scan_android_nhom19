package com.example.mypageview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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


        //gán giá trị
        ItemRow itemRow=List.get(i);

        holder.imgIcon.setImageResource(itemRow.getIcon());
        return view;
    }
}
