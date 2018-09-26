package com.example.tomer.shopartment.holders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ShoppingList;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    Item item;
    Context context;
    TextView name;
    TextView quantity;

    private View mView;


    public ItemViewHolder(View itemView){
        super(itemView);

        this.name = itemView.findViewById(R.id.itemNameTextView);
        this.quantity = itemView.findViewById(R.id.quantityTextView);
        mView = itemView;

        itemView.setOnClickListener(this);
    }

    public void setItem(Context context , String userEmail , ShoppingList shoppingList, Item item){
        this.context = context;
        this.item = item;
        this.name.setText(item.getName());
        this.quantity.setText(item.getQuantity());
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(context, "CLACK", Toast.LENGTH_SHORT).show();
    }
}
