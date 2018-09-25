package com.example.tomer.shopartment.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.security.acl.Owner;
import java.util.Date;
import java.util.HashMap;

public class ShoppingList implements Parcelable {
    public ShoppingList(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<ShoppingList> CREATOR = new Parcelable.Creator<ShoppingList>() {
        public ShoppingList createFromParcel(Parcel in) {
            return new ShoppingList(in);
        }

        public ShoppingList[] newArray(int size) {

            return new ShoppingList[size];
        }

    };

    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.createdBy = in.readString();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString( this.id);
        dest.writeString(this.createdBy);
    }
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

    public Date getDate(){
        return this.date;
    }
}
