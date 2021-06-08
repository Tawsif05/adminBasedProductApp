package com.example.adminbasedproductapp;

public class ProductFB {
    private String Name;

    private String Code;

    private String Price;

    public ProductFB(){

    }

    public ProductFB(String name, String code, String price) {
        Name = name;
        Code = code;
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
