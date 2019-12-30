package com.example.mayscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FragmentList extends Fragment {
    private ListView listView;
    private ArrayList<ItemRow> array_view_pdf = new ArrayList<>();
    ListViewAdapter listViewAdapter;
    private static final int REQUEST_ID_READ_PERMISSION = 200;
    String fileName;
    /*    AsyncLoadPdfs asyncLoadPdfs=new AsyncLoadPdfs();*/

    public FragmentList(ArrayList<ItemRow> array_view_pdf) {
        this.array_view_pdf = array_view_pdf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_pdf, container, false);
        listView = (ListView) view.findViewById(R.id.list_view_pdf);


        listViewAdapter = new ListViewAdapter(getContext(), R.layout.list_view_item, array_view_pdf);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu=new PopupMenu(getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.popupShare:
                                testShareFile_Feture(array_view_pdf.get(position).getUri());
                                break;
                            case R.id.popupDelete:
                                String pdfFileName2 = "/sdcard/ScanPDF/PDFs/" + fileName + ".pdf";
                                Uri uri2 = Uri.fromFile(new File(fileName));
                                File fdelete = new File(uri2.getPath());
                                if (fdelete.exists()) {
                                    try {
                                        fdelete.getCanonicalFile().delete();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (fdelete.exists()) {
                                        getContext().deleteFile(fdelete.getName());
                                    }


                                }
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();



                return true;
            }
        });

//        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("Open file").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Log.i("viettt", fileName);
//                        Intent intent1 = new Intent(getContext(), PDFViewActivity.class);
//                        Log.i("viettt", "onItemClick: ###");
//                        StringTokenizer tokens = new StringTokenizer(fileName, ".");
//                        fileName = tokens.nextToken();
//                        intent1.putExtra("filename", fileName);
//                        startActivity(intent1);
//                        return false;
//                    }
//                });
//                menu.add("Share file").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        String pdfFileName = "/sdcard/ScanPDF/PDFs/" + fileName + ".pdf";
//                        Uri uri = Uri.fromFile(new File(fileName));
//                        Intent intent = new Intent(getContext(), ShareFileActivity.class);
//                        intent.putExtra("URI", uri.toString());
//                        intent.putExtra("flag", "pdf");
//                        Log.i("viettt", uri.toString());
//                        startActivity(intent);
//                        return false;
//                    }
//                });
//
//                menu.add("Delete file").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        String pdfFileName = "/sdcard/ScanPDF/PDFs/" + fileName + ".pdf";
//                        Uri uri = Uri.fromFile(new File(fileName));
//                        File fdelete = new File(uri.getPath());
//                        if(fdelete.exists())
//                        {
//                            try {
//                                fdelete.getCanonicalFile().delete();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            if(fdelete.exists()){
//                                getContext().deleteFile(fdelete.getName());
//                            }
//                        }
//                        return false;
//                    }
//                });
//            }
//        });
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                fileName = array_view_pdf.get(position).getText();
//
//                return false;
//            }
//        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("viettt", "aaaa");
                fileName = array_view_pdf.get(position).getText();
                Log.i("viettt", fileName);
                Intent intent1 = new Intent(getContext(), PDFViewActivity.class);
                Log.i("viettt", "onItemClick: ###");
                StringTokenizer tokens = new StringTokenizer(fileName, ".");
                fileName = tokens.nextToken();
                intent1.putExtra("filename", fileName);
                startActivity(intent1);
            }
        });

        return view;

    }

    public void notifyDataSetChanged() {

        listViewAdapter.notifyDataSetChanged();
    }

    private void testShareFile_Feture( Uri uri  ) {
        String extStore = System.getenv("EXTERNAL_STORAGE");
        File f_exts = new File(extStore);
        String path = f_exts.getAbsolutePath();
        String myFilePath;
//
//        if(flag.equals("pdf"))
//        {
//            myFilePath= path + "/ScanPDF/PDFs/" + uri.getLastPathSegment();
//        }
//        else {
//            myFilePath = path + "/ScanPDF/Images/" + uri.getLastPathSegment();
//        }

        //File file = new File(myFilePath);
        File file=new File(uri.getPath());

       Toast.makeText(getContext(), file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri_2 = FileProvider.getUriForFile(getContext(),BuildConfig.APPLICATION_ID, file);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri_2);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }
}