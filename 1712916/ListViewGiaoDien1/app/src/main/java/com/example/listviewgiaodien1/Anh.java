package com.example.listviewgiaodien1;



public class Anh {
    private String Ten;
    private int DiaChi;

    public String getTen() {
        return Ten;
    }

    public void setTen(String ten) {
        Ten = ten;
    }

    public int getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(int diaChi) {
        DiaChi = diaChi;
    }

    public Anh(String ten, int diaChi) {
        Ten = ten;
        DiaChi = diaChi;
    }
}
