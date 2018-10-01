package com.example.tomer.shopartment.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tomer.shopartment.models.Category;
import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.models.Item;

import java.util.ArrayList;

public class ItemAdapter  {

    /**
    ArrayList<Object> list;
    private static final int ITEM = 0;
    private static final int HEADER = 1;
    private ArrayList<Category> categories;
    private boolean whiteBg = true;

    public ItemAdapter(Context context , ArrayList<Object> list) {
        super(context , list);
        this.list = list;
        categories = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position){
        if(list.get(position) instanceof Item){
            return ITEM;
        }
        else{
            return HEADER;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       if (view == null){
           switch (getItemViewType(i)){
               case ITEM:
                   view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_view, viewGroup, false);
                   break;
               case HEADER:
                   view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_view_header, viewGroup, false);
                   break;
           }
       }
       switch (getItemViewType(i)){
           case ITEM:
               Item item = ((Item) list.get(i));
               String category = item.getCategory();
                   TextView name = (TextView) view.findViewById(R.id.itemNameTextView);
                   TextView quantity = (TextView) view.findViewById(R.id.itemNameTextView2);
                   name.setText(item.getName());
                   quantity.setText("quantity: " + item.getQuantity());
                   if(whiteBg){
                       name.setBackgroundColor(Resources.getSystem().getColor(android.R.color.white));
                       quantity.setBackgroundColor(Resources.getSystem().getColor(android.R.color.white));
                       whiteBg = false;
                   }
                   else{
                       name.setBackgroundColor(Resources.getSystem().getColor(android.R.color.darker_gray));
                       quantity.setBackgroundColor(Resources.getSystem().getColor(android.R.color.darker_gray));
                       whiteBg = true;
                   }

               break;
           case HEADER:
               TextView title = (TextView) view.findViewById(R.id.itemListViewHeader);
               title.setText(((String)list.get(i)));
               break;

       }
       return view;
    }

    @Override
    public void remove(@Nullable Object object) {
        super.remove(object);
        if(object instanceof Item) {
            categoryCheckDown(((Item) object).getCategory());
        }

    }


    public boolean categoryExistsInc(String catName){
        if(categories.size() == 0){
            categories.add(new Category(catName , 1));
            return false;
        }
        for(Category it: categories){
            if(it.getName().equals(catName)){
                it.amountUp();
                return true;
            }
        }
        categories.add(new Category(catName , 1));
        return false;
    }

    public boolean categoryCheckDown(String catName){
        for(Category it: categories){
            if(it.getName().equals(catName)){
                it.amountDown();
                if(it.getAmount() == 0){
                    categories.remove(it);
                    super.remove(catName);
                    return true;
                }

            }
        }
        return false;
    }

    public boolean categoryExists(String catName){
        if(categories.size() == 0){
            return false;
        }
        for(Category it: categories) {
            if (it.getName().equals(catName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        categories.clear();
    }

    @Override
    public void add(@Nullable Object object) {
        if (object instanceof Item) {
            super.add(object);
            return;
        }
        else{
            if(categoryExistsInc((String) object)){
               return;
            }
            else{
                super.add(object);
                return;
            }

        }
    }
    **/
}
