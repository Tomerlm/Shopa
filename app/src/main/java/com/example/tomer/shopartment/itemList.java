package com.example.tomer.shopartment;

import java.security.acl.Owner;
import java.util.HashMap;

public class itemList {
    private String name;
    private String owner;
    private HashMap<String, Integer> sharedBy;

    public itemList(String name , String owner){
        this.name = name;
        this.owner = owner;
        this.sharedBy = new HashMap<>();
        sharedBy.put(owner , 2 );
    }

    public String getName() {
        return name;
    }

    public boolean hasAccess(String user){
        return sharedBy.containsKey(user);

    }
}
