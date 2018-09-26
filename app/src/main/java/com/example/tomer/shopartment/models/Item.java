package com.example.tomer.shopartment.models;

import java.io.Serializable;

public class Item implements Serializable {
    // private
    private String itemName;
    private int quantity;
    private double approxPrice;
    private String category;
    private String id;

    // public
    public Item(){}

    public Item(String name , int qauntity , double price , String category) {
        this.itemName = name;
        this.quantity = qauntity;
        this.approxPrice = price;
        this.category = category;
    }

    public String getName(){
        return this.itemName;
    }

    public double getApproxPrice(){
        return this.approxPrice;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public void setName(String name){
        this.itemName = name;
    }

    public String getCategory() {return this.category;}

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }
}
