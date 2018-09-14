package com.example.tomer.shopartment.models;

import java.util.ArrayList;

public class User {
    private String uId;
    private String name;
    private String email;
    private boolean hasList;
    private ArrayList<String> lists;

    public User(){
        this.uId = "uId";
        this.name = "name";
        this.email = "email";
        this.hasList = true;
        this.lists = new ArrayList<>();

    }

    public User(String uId , String name , String email){
       this.uId = uId;
       this.name = name;
       this.email = email;
       this.hasList = false;
       this.lists = new ArrayList<>();
    }

    public User(User user){
        this.uId = user.uId;
        this.name = user.name;
        this.email = user.email;
        this.hasList = user.hasList;
        this.lists = user.lists;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getLists() {
        return lists;
    }

    public boolean addList(String list) {
       return lists.add(list);
    }

    public boolean removeList(String list){
        return lists.remove(list);
    }

    public void setHasList(boolean hasList) {
        this.hasList = hasList;
    }

    public boolean getHasList(){
        return hasList;
    }
}
