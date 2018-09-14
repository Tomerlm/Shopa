package com.example.tomer.shopartment.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.security.acl.Owner;
import java.util.Date;
import java.util.HashMap;

public class ShoppingList {
    private String name , id , createdBy;
    @ServerTimestamp
    private Date date;

    public ShoppingList() {}

    public ShoppingList(String name, String id, String createdBy) {
        this.name = name;
        this.id = id;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
