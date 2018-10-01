package com.example.tomer.shopartment.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.tomer.shopartment.models.ListItem;

public abstract class MyViewHolder extends RecyclerView.ViewHolder {
    public MyViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindType(ListItem item);
}