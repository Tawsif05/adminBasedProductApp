package com.example.adminbasedproductapp;

public class version {

    int v;

    public version(){

    }

    public version(int v) {
        this.v = v;
    }

    public int getV() {
        return v;
    }

    @Override
    public String toString() {
        return v+"";
    }

    public void setV(int v) {
        this.v = v;
    }
}
