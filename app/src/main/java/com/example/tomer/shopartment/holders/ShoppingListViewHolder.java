package com.example.tomer.shopartment.holders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.activities.ChooseListFragment;
import com.example.tomer.shopartment.activities.MainActivity;
import com.example.tomer.shopartment.interfaces.ItemClickListener;
import com.example.tomer.shopartment.models.ShoppingList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ShoppingListViewHolder extends RecyclerView.ViewHolder{

    private TextView listNameTextView;
    private TextView createdByTextView;
    private TextView dateTextView;
    private ItemClickListener itemClickListener;
    private View mView;

    public ShoppingListViewHolder(View itemView) {
        super(itemView);
        listNameTextView = itemView.findViewById(R.id.list_name_textview);
        createdByTextView = itemView.findViewById(R.id.created_by_textview);
        dateTextView = itemView.findViewById(R.id.date_textview);
        mView = itemView;


    }

    public void setShoppingList(Context context , final ShoppingList mShoppingList){

        listNameTextView.setText(mShoppingList.getName());
        createdByTextView.setText("Created by: " + mShoppingList.getCreatedBy());

        Date date = mShoppingList.getDate();
        if(date != null){
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM , Locale.US);
            dateTextView.setText(dateFormat.format(date));
        }
    }

    public View getmView(){
        return mView;
    }

}
