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

    public void setItem(Item item){
        this.item = item;
        this.name.setText(item.getName());
        this.quantity.setText(item.getQuantity());
    }

    @Override
    public void onClick(View view) {

        if(this.item != null){
            //TODO go to edit intent... make it go to fragment in the future
            Toast.makeText(context, "CLICK", Toast.LENGTH_SHORT).show();
        }
    }
}
