package com.example.tomer.shopartment.models;

public class Category {

    private String name;
    private int amount;

    public Category(String name , int amount){
        this.name = name;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public int amountUp(){
        return ++amount;
    }

    public int amountDown(){
        return --amount;
    }
}
