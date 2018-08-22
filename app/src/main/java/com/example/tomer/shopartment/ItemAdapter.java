package com.example.tomer.shopartment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Object> {

    ArrayList<Object> list;
    private static final int ITEM = 0;
    private static final int HEADER = 1;

    public ItemAdapter(Context context , ArrayList<Object> list) {
        super(context , 0 , list);
        this.list = list;
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
               TextView name = (TextView) view.findViewById(R.id.itemNameTextView);

               name.setText(((Item)list.get(i)).getName());
               break;
           case HEADER:
               TextView title = (TextView) view.findViewById(R.id.itemListViewHeader);
               title.setText(((String)list.get(i)));
               break;

       }
       return view;
    }
}
