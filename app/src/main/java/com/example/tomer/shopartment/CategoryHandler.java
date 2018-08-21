package com.example.tomer.shopartment;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CategoryHandler {
    private Map<String , Integer> amount;
    private Map<String , Integer> id;

    public CategoryHandler(){
        amount = new HashMap<>();
        id = new HashMap<>();
    }

    public int getAmount(String cat){
        return amount.get(cat);
    }

    public Integer getId(String cat){
        return id.get(cat);
    }

    public boolean contains(String cat){
        return id.containsKey(cat);
    }

    public void addCategory(String cat , int textId){
        if(id.containsKey(cat)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                amount.replace(cat , amount.get(cat) , amount.get(cat)+1);
            }
            return;
        }

        amount.put(cat , 1);
        id.put(cat , textId);
        return;
    }

    public boolean removeCategory(String cat) { //return true if the item was removed
        if (id.containsKey(cat)) {
            if (amount.get(cat) > 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    amount.replace(cat, amount.get(cat), amount.get(cat) - 1);
                    return false;
                }
                else{
                    amount.remove(cat);
                    id.remove(cat);
                    return true;
                }
            }
        }
        assert id.containsKey(cat);
        return true;
    }

    public void clearAll(){
        amount.clear();
        id.clear();
    }

    public int size(){
       return id.size();
    }

    public String[] allCategories(){
        String[] arr = new String[id.size()];
        int i = 0;
        for(String it: id.keySet()){
            arr[i++] = it;
        }
        return arr;
    }

}
