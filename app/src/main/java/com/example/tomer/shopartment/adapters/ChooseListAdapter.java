package com.example.tomer.shopartment.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.holders.ShoppingListViewHolder;
import com.example.tomer.shopartment.models.ShoppingList;

import java.util.ArrayList;


public class ChooseListAdapter extends RecyclerView.Adapter{

    ArrayList<ShoppingList> list;

    public ChooseListAdapter(ArrayList<ShoppingList> list){
        super();
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_choose_view, viewGroup , false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((ListViewHolder) viewHolder).bindView(i);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        private TextView listName;

        public ListViewHolder(View itemView){
            super(itemView);
            listName = itemView.findViewById(R.id.listNameText);
            itemView.setOnClickListener(this);

        }

        public void bindView(int pos){
            if(list.get(pos) != null) {
                listName.setText(((ShoppingList) list.get(pos)).getName());
            }
            else{
                return;
            }


        }

        public void onClick(View view){

        }

    }

}
