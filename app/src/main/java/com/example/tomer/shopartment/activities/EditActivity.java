package com.example.tomer.shopartment.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tomer.shopartment.MyDBHandler;
import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    private enum StringStatus {NAME_NOT_VALID, NAME_EXIST, NAME_OK, NO_NUMBER, NUMBER_OK}

    public static final int DELETE_REQUEST = 2;
    MyDBHandler db;
    Spinner categories;
    EditText nameEdit;
    EditText quantityEdit;
    EditText priceEdit;
    ArrayList<String> itemList;
    FloatingActionButton fabOK;
    FloatingActionButton fabDEL;
    Vibrator vibe;
    String origItemID;

    String itemId;
    String listId;
    String[] attributes;

    FirebaseFirestore firestoreDb;
    DocumentReference currItemRef;
    CollectionReference currListRef;

    private final String TAG = "EditActivity";

    class UpdateError extends Exception {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent lastIntent = getIntent(); // gets the previously created intent
        db = new MyDBHandler(this);
        itemId = lastIntent.getStringExtra("currItemId");
        listId = lastIntent.getStringExtra("currListId");
        origItemID = db.getIdByName(itemId);
        itemList = lastIntent.getStringArrayListExtra("itemList");
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        firestoreDb = FirebaseFirestore.getInstance();

        currListRef = firestoreDb.collection("lists").document(listId).collection("items");
        currItemRef = currListRef.document(itemId);

        getCurrentValues();
        setSaveButton();
        setDelButton();

    }
    private void getCurrentValues(){
        currItemRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        setAllEdits(documentSnapshot.toObject(Item.class));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG , "failed to get item");
                    }
                });
    }
    private void setAllEdits(Item item) {
        nameEdit = (EditText) findViewById(R.id.itemNameEdit);
        nameEdit.setText(item.getName());
        // attributes = db.columnToStrings(itemName);
        quantityEdit = (EditText) findViewById(R.id.quantityEdit);
        quantityEdit.setText(item.getQuantity());
        quantityEditTouch();
        priceEdit = (EditText) findViewById(R.id.priceEdit);
        priceEditTouch();
        priceEdit.setText(Double.toString(item.getApproxPrice()));
        categories = (Spinner) findViewById(R.id.categorySpinner);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(EditActivity.this, // settings for the spinner
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.categories));
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(catAdapter);
        setSpinnerText(item.getCategory());

    }

    public void setSaveButton() {
        fabOK = findViewById(R.id.fabOK);
        fabOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(50);
                checkAndUpdate();
                Intent main = new Intent();
                main.putExtra("itemName", nameEdit.getText().toString());
                setResult(EditActivity.RESULT_OK, main);
                finish();
            }
        });

    }

    public void setDelButton() {
        fabDEL = findViewById(R.id.fabDEL); // TODO design
        fabDEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(50);
                try {
                    deleteValueFromDb();
                } catch (UpdateError e) {
                    e.getMessage();
                    return;
                }
                Intent main = new Intent();
                setResult(EditActivity.DELETE_REQUEST, main);
                finish();
            }
        });

    }
/**
    private void updateValueInDb() throws UpdateError { // TODO won't update if we don't change the name

        switch (editNameValidity(nameEdit.getText().toString())) {

            case NAME_NOT_VALID:
                Toast.makeText(EditActivity.this, "Name is not valid. enter a valid string.", Toast.LENGTH_LONG).show();
                throw new UpdateError();
            case NAME_EXIST:
                Toast.makeText(EditActivity.this, "Item Exists, choose a different name.", Toast.LENGTH_LONG).show();
                throw new UpdateError();
            case NAME_OK:

               // String num = db.getIdByName(itemName);
                if (isANum(quantityEdit.getText().toString()) && isANum(priceEdit.getText().toString())) {
                  //  db.updateData(num,
                            nameEdit.getText().toString().trim().replaceAll(" +", " "),
                            Integer.parseInt(quantityEdit.getText().toString()),
                            Double.parseDouble(priceEdit.getText().toString()),
                            categories.getSelectedItem().toString());
                } else {
                    Toast.makeText(EditActivity.this, "please enter valid numbers for quantity and price", Toast.LENGTH_LONG).show();
                    throw new UpdateError();
                }
        }
    }
    **/

        private void deleteValueFromDb () throws UpdateError {
           // db.removeData(itemName);
            currItemRef.delete();
        }


        public void setSpinnerText (String text){
            String compareValue = text;
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categories.setAdapter(adapter);
            if (compareValue != null) {
                int spinnerPosition = adapter.getPosition(compareValue);
                categories.setSelection(spinnerPosition);
            }
        }

        private StringStatus editNameValidity (String name) throws UpdateError {
            if (!MainActivity.isStringValid(name)) return StringStatus.NAME_NOT_VALID;
            else if (db.nameExists(name) && !sameItem(name , origItemID)) return StringStatus.NAME_EXIST;
            else return StringStatus.NAME_OK;

        }

        private boolean isANum (String str){
            boolean numeric = true;
            try {
                Double num = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                numeric = false;
            }
            return numeric;
        } // checks if input string is a number

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

        private boolean sameItem(String name , String origId){
            return db.getIdByName(name).equals(origId);
        }

        private void checkAndUpdate(){
            currListRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String reqName = nameEdit.getText().toString().trim().replaceAll(" +", " ");
                            if (MainActivity.isStringValid(reqName)) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    Item item = doc.toObject(Item.class);

                                    if (!doc.getId().equals(currItemRef.getId()) && item.getName().equals(reqName)) {

                                        Toast.makeText(EditActivity.this, "Item name exists. choose different one", Toast.LENGTH_LONG).show();
                                        return;

                                    }
                                }
                                // if we got here , we need to update the item

                                Item updItem = new Item(nameEdit.getText().toString().trim().replaceAll(" +", " "),
                                        Integer.parseInt(quantityEdit.getText().toString()),
                                        Double.parseDouble(priceEdit.getText().toString()),
                                        categories.getSelectedItem().toString());
                                currItemRef.set(updItem);
                            }
                            else{
                                Toast.makeText(EditActivity.this, "name is not valid. choose different one", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG , "Error while updating item");
                }
            });
        }
}
