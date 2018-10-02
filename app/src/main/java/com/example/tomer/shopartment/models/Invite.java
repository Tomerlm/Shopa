package com.example.tomer.shopartment.models;

public class Invite {

    String inviteId;
    String sentBy;
    ShoppingList shoppingList;

    public Invite(){
        this.inviteId = null;
        this.shoppingList = null;
    }

    public Invite(String inviteId , ShoppingList shoppingList , String sentBy){
        this.inviteId = inviteId;
        this.shoppingList = shoppingList;
        this.sentBy = sentBy;
    }

    public void setInviteId(String inviteId){
        this.inviteId = inviteId;
    }
    public String getInviteId(){
        return this.inviteId;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getSentBy() {
        return sentBy;
    }
}
