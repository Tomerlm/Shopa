package com.example.tomer.shopartment;

public class Item {
    // private
    private String item_name;
    private int quantity;
    private double approx_price;

    // public
    public Item(){}
    public Item(String name , int qauntity , double price) {
        this.item_name = name;
        this.quantity = qauntity;
        this.approx_price = price;
    }

    public String getName(){
        return this.item_name;
    }

    public double getApproxPrice(){
        return this.approx_price;
    }

    public int getQuantity(){
        return this.quantity;
    }
}
