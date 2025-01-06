package com.example.conversordemonedas;

public class SpinnerMonedas {
    private String monedasNombre;
    private int monedasImg;

    public SpinnerMonedas(String monedasNombre, int monedasImg) {
        this.monedasNombre = monedasNombre;
        this.monedasImg = monedasImg;
    }

    public int getMonedasImg() {
        return monedasImg;
    }

    public void setMonedasImg(int monedasImg) {
        this.monedasImg = monedasImg;
    }

    public SpinnerMonedas(String monedasNombre){
        this.monedasNombre = monedasNombre;
    }

    public String getMonedasNombre() {
        return monedasNombre;
    }

    public void setMonedasNombre(String monedasNombre) {
        this.monedasNombre = monedasNombre;
    }
}
