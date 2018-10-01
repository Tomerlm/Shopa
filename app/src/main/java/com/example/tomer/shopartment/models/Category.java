package com.example.tomer.shopartment.models;

public class Category implements ListItem {

    private String name;

    public Category(String name){
        this.name = name;

    }
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public int getType() {
        return ListItem.CATEGORY;
    }
}
