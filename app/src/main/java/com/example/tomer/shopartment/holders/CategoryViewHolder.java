package com.example.tomer.shopartment.holders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.models.Category;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ListItem;
import com.example.tomer.shopartment.models.ShoppingList;

public class CategoryViewHolder extends MyViewHolder {

    Category category;
    Context context;

    TextView name;
    private View mView;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        this.name = itemView.findViewById(R.id.itemListViewHeader);

        mView = itemView;
    }

    public void setCategory(Context context , Category category){
        this.name.setText(category.getName());
    }

    @Override
    public void bindType(ListItem item) {
        this.name.setText(((Category) item).getName());
    }
}
