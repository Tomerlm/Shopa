package com.example.tomer.shopartment.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.holders.CategoryViewHolder;
import com.example.tomer.shopartment.holders.ItemViewHolder;
import com.example.tomer.shopartment.holders.MyViewHolder;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ListItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class FirestoreAdapterHelper extends FirestoreRecyclerAdapter<ListItem, MyViewHolder> {

    private final List<ListItem> mItems;
    final Context context;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *  @param options
     * @param mItems
     * @param context
     */
    public FirestoreAdapterHelper(Context context, @NonNull FirestoreRecyclerOptions options, List<ListItem> mItems) {
        super(options);
        this.mItems = mItems;
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ListItem model) {
        ListItem item = mItems.get(position);
        holder.bindType(item);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        switch (i) {
            case ListItem.ITEM:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_list_view, viewGroup, false);
                return new ItemViewHolder(view);
            case ListItem.CATEGORY:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_list_view_header, viewGroup, false);
                return new CategoryViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
