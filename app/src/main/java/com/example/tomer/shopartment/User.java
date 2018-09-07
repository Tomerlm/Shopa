package com.example.tomer.shopartment;

import java.util.ArrayList;

public class User {
    private String uId;
    private String name;
    private String email;
    private ArrayList<String> lists;

    public User(String uId , String name , String email){
       this.uId = uId;
       this.name = name;
       this.email = email;
       this.lists = new ArrayList<>();
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
}
