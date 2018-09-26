package com.example.tomer.shopartment.holders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.activities.ChooseListFragment;
import com.example.tomer.shopartment.activities.EditActivity;
import com.example.tomer.shopartment.activities.EditFragment;
import com.example.tomer.shopartment.activities.MainActivity;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ShoppingList;
import com.example.tomer.shopartment.models.User;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    Item item;
    Context context;

    TextView name;
    TextView quantity;

    private View mView;
    FragmentManager fragmentManager;


    public ItemViewHolder(View itemView){
        super(itemView);

        this.name = itemView.findViewById(R.id.itemNameTextView);
        this.quantity = itemView.findViewById(R.id.quantityTextView);
        mView = itemView;

        itemView.setOnClickListener(this);
    }

    public void setItem(Context context , String userEmail , ShoppingList shoppingList, Item item , FragmentManager fragmentManager){
        String quantity = String.format("quantity: %d" , item.getQuantity());
        this.context = context;
        this.item = item;
        this.name.setText(item.getName());
        this.quantity.setText(quantity);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View view) {
        goToEdit();

        Toast.makeText(context, "CLACK", Toast.LENGTH_SHORT).show();
    }

    private void goToEdit() {
        EditFragment editFragment = new EditFragment();
        Bundle bundle = new Bundle();
        Item item = this.item;
        bundle.putSerializable("item", item);
        editFragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.drawer , editFragment , "frag2").addToBackStack("what").commit();
    }
}


