package com.example.mypageview;



public class ItemRow {
    private String text;
    private int icon;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public ItemRow(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }
}
