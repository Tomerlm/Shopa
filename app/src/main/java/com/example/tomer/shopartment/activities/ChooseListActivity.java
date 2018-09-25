package com.example.tomer.shopartment.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.holders.ShoppingListViewHolder;
import com.example.tomer.shopartment.models.ShoppingList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChooseListActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore firestoreDB;
    CollectionReference userListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_list);
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        userListRef = firestoreDB.collection("lists").document(mAuth.getCurrentUser().getEmail()).collection("userLists");
        chooseListDialog();
    }

    public void chooseListDialog(){
        final RecyclerView listsRecyclerView = findViewById(R.id.lists_recycler);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TextView emptyView = findViewById(R.id.empty_view);
        final ProgressBar progressBar = findViewById(R.id.lists_dialog_progress);
        Query query = userListRef.orderBy("name" , Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ShoppingList> mFirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ShoppingList>()
                .setQuery(query , ShoppingList.class)
                .build();

        FirestoreRecyclerAdapter<ShoppingList , ShoppingListViewHolder> firestoreRecyclerAdapter =
                new FirestoreRecyclerAdapter<ShoppingList, ShoppingListViewHolder>(mFirestoreRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position, @NonNull ShoppingList model) {
                        holder.setShoppingList(getApplicationContext() , model);
                    }

                    @NonNull
                    @Override
                    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_choose_list , viewGroup , false);
                        return new ShoppingListViewHolder(view);
                    }

                    @Override
                    public void onDataChanged() {
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }

                        if(getItemCount() == 0){
                            listsRecyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        else{
                            listsRecyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE );
                        }
                    }

                    @Override
                    public int getItemCount() {
                        return super.getItemCount();
                    }
                };
        listsRecyclerView.setAdapter(firestoreRecyclerAdapter);
    }
}
