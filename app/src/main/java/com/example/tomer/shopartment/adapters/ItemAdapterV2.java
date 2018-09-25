package com.example.tomer.shopartment.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomer.shopartment.holders.ItemViewHolder;
import com.example.tomer.shopartment.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapterV2 extends RecyclerView.Adapter<ItemViewHolder> {

    ArrayList<Item> items;
    Context context;
    int itemResource;

    public ItemAdapterV2(Context context, int itemResource, ArrayList<Item> items) {

        // 1. Initialize our adapter
        this.items = items;
        this.context = context;
        this.itemResource = itemResource;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(this.itemResource, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {

        Item item = this.items.get(i);
        itemViewHolder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
