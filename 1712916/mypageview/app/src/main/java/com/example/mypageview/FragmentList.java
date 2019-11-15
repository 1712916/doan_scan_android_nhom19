package com.example.mypageview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentList  extends Fragment {
    private ListView listView;
    private ArrayList<ItemRow> array_view_pdf;
    ListViewAdapter listViewAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.list_pdf,container,false);
        listView=(ListView) view.findViewById(R.id.list_view_pdf);
        array_view_pdf=new ArrayList<>();
        array_view_pdf.add(new ItemRow("123123",R.drawable.icons_pdf));
        array_view_pdf.add(new ItemRow("123123123",R.drawable.icons_pdf));
        array_view_pdf.add(new ItemRow("123123",R.drawable.icons_pdf));
        array_view_pdf.add(new ItemRow("123123",R.drawable.icons_pdf));
        array_view_pdf.add(new ItemRow("554654",R.drawable.icons_pdf));
        array_view_pdf.add(new ItemRow("456456",R.drawable.icons_pdf));
        array_view_pdf.add(new ItemRow("456456",R.drawable.icons_pdf));
        array_view_pdf.add(new ItemRow("456456456",R.drawable.icons_pdf));

        listViewAdapter=new ListViewAdapter(getContext(),R.layout.list_view_item,array_view_pdf);

        listView.setAdapter(listViewAdapter);

        return  view;

    }
}
