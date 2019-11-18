package com.example.mayscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ItemRow> List;

    public ListViewAdapter(Context context, int layout, List<ItemRow> List) {
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
        TextView txtText;
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
            holder.txtText=(TextView)view.findViewById(R.id.item_text);
            holder.imgIcon=(ImageView)view.findViewById(R.id.item_icon);
            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)view.getTag();
        }


        //gán giá trị
        ItemRow itemRow=List.get(i);
        holder.txtText.setText(itemRow.getText());
        holder.imgIcon.setImageResource(R.drawable.icons_pdf);
        return view;
    }


}

