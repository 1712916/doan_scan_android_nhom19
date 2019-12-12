package com.example.mayscanner;


import android.net.Uri;

public class ItemRow {
    private String text;
    private Uri uri;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }


}
