package com.example.tomer.shopartment.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.adapters.ChooseListAdapter;
import com.example.tomer.shopartment.holders.ShoppingListViewHolder;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ShoppingList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseListFragment extends Fragment {

    FirebaseFirestore firestoreDB;
    FirebaseAuth mAuth;
    CollectionReference userListsCollection;
    ArrayList<ShoppingList> usersLists;
    FirestoreRecyclerAdapter<ShoppingList , ShoppingListViewHolder> firestoreRecyclerAdapter;
    RecyclerView recyclerView;

    public ChooseListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        userListsCollection = firestoreDB.collection("lists").document(mAuth.getCurrentUser().getEmail()).collection("userLists");

        // get the users shopping lists list
        Query query = userListsCollection.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ShoppingList> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ShoppingList>()
                .setQuery(query , ShoppingList.class)
                .build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ShoppingList, ShoppingListViewHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position, @NonNull final ShoppingList model) {
                holder.setShoppingList(getActivity().getApplicationContext() , model);

                //set click method
                holder.getmView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).setCurrentListRef(model);
                        getFragmentManager().popBackStack();
                    }
                });


            }

            @NonNull
            @Override
            public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shopping_list, viewGroup , false);
                return new ShoppingListViewHolder(view);
            }

            @Override
            public void onDataChanged() { // TODO watch video part 10 and sort the progress bar shit
                super.onDataChanged();
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

        };
        recyclerView = view.findViewById(R.id.chooseListRecycle);
        recyclerView.setAdapter(firestoreRecyclerAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        // TODO check if works firestoreRecyclerAdapter.startListening();


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }



}
