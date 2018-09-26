package com.example.tomer.shopartment.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.holders.ShoppingListViewHolder;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ShoppingList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    // views
    EditText nameEdit;
    EditText quantityEdit;
    EditText priceEdit;
    FloatingActionButton fabOK;
    FloatingActionButton fabDEL;
    Vibrator vibe;
    Spinner categories;

    Item currentItem;



    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        Bundle bundle = getArguments();
        assert bundle != null;
        currentItem = (Item) bundle.getSerializable("item");
        setAllEdits(currentItem , view);
        setSaveButton(view);
        setDelButton(view);

        return view;

    }

    private void setAllEdits(Item item , View view) {
        nameEdit = (EditText) view.findViewById(R.id.itemNameEdit);
        nameEdit.setText(item.getName());
        // attributes = db.columnToStrings(itemName);
        quantityEdit = (EditText) view.findViewById(R.id.quantityEdit);
        String quantity = String.format("%d" , item.getQuantity());
        quantityEdit.setText(quantity);
        quantityEditTouch();
        priceEdit = (EditText) view.findViewById(R.id.priceEdit);
        priceEditTouch();
        priceEdit.setText(Double.toString(item.getApproxPrice()));
        categories = (Spinner) view.findViewById(R.id.categorySpinner);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(getContext(), // settings for the spinner
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.categories));
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(catAdapter);
        setSpinnerText(item.getCategory());

    }

    public void setSaveButton(View view) {
        fabOK = view.findViewById(R.id.fabOK);
        fabOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // user want to edit item
                String itemName = nameEdit.getText().toString().trim().replaceAll(" +", " ");
                if (MainActivity.isStringValid(itemName)) {
                    Item newItem = new Item(itemName,
                            Integer.parseInt(quantityEdit.getText().toString()),
                            Double.parseDouble(priceEdit.getText().toString()),
                            categories.getSelectedItem().toString());
                    newItem.setId(currentItem.getId());
                    ((MainActivity) getActivity()).editItem(currentItem , newItem);
                    getFragmentManager().popBackStack();
                }
                else {
                    Toast.makeText(getContext(), "name is not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void setDelButton(View view) {
        fabDEL = view.findViewById(R.id.fabDEL);
        fabDEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // user want to delete item
                ((MainActivity) getActivity()).removeItem(currentItem);
                getFragmentManager().popBackStack();


            }
        });

    }

    private void priceEditTouch() {
        priceEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                priceEdit.setText("");
                return false;
            }
        });
    } // set price editText

    private void quantityEditTouch(){
        quantityEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                quantityEdit.setText("");
                return false;
            }
        });
    } // set qunitity editText

    public void setSpinnerText (String text){
        String compareValue = text;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            categories.setSelection(spinnerPosition);
        }
    }


}
