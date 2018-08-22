package com.example.tomer.shopartment;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    private enum StringStatus {NAME_NOT_VALID , NAME_EXIST , NAME_OK , NO_NUMBER , NUMBER_OK  }
    MyDBHandler db;
    Spinner categories;
    EditText nameEdit;
    EditText quantityEdit;
    EditText priceEdit;
    ArrayList<String> itemList;
    FloatingActionButton fab;
    Vibrator vibe;
    String origItemID;

    String itemName;
    String[] attributes;

    class UpdateError extends Exception{ // TODO add delete option here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent lastIntent = getIntent(); // gets the previously created intent
        db = new MyDBHandler(this);
        itemName = lastIntent.getStringExtra("name");
        origItemID = db.getIdByName(itemName);
        itemList = lastIntent.getStringArrayListExtra("itemList");
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setAllEdits();
        setSaveButton();

    }

    public void setAllEdits(){
        nameEdit = (EditText) findViewById(R.id.itemNameEdit);
        nameEdit.setText(itemName);
        attributes = db.columnToStrings(itemName);
        quantityEdit = (EditText) findViewById(R.id.quantityEdit);
        quantityEdit.setText(attributes[2]);
        quantityEditTouch();
        priceEdit = (EditText) findViewById(R.id.priceEdit);
        priceEditTouch();
        priceEdit.setText(attributes[3]);
        categories = (Spinner) findViewById(R.id.categorySpinner);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(EditActivity.this, // settings for the spinner
                android.R.layout.simple_list_item_1 ,
                getResources().getStringArray(R.array.categories));
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(catAdapter);
        setSpinnerText(attributes[4]);

    }

    public void setSaveButton(){
       fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(50);
                try {
                    updateValueInDb();
                }
                catch (UpdateError e){
                    e.getMessage();
                    return;
                }
                Intent main = new Intent();
                main.putExtra("itemName", nameEdit.getText().toString());
                setResult(EditActivity.RESULT_OK, main);
                finish();
            }
        });

    }

    private void updateValueInDb() throws UpdateError {
        /**
        switch (editNameValidity(nameEdit.getText().toString())) {

            case NAME_NOT_VALID:
                Toast.makeText(EditActivity.this, "Name is not valid. enter a valid string.", Toast.LENGTH_LONG).show();
                throw new UpdateError();
            case NAME_EXIST:
                Toast.makeText(EditActivity.this, "Item Exists, choose a different name.", Toast.LENGTH_LONG).show();
                throw new UpdateError();
            case NAME_OK:
         **/
                String num = db.getIdByName(itemName);
                if(isANum(quantityEdit.getText().toString()) && isANum(priceEdit.getText().toString())) {
                    db.updateData(num,
                            nameEdit.getText().toString().trim().replaceAll(" +", " "),
                            Integer.parseInt(quantityEdit.getText().toString()),
                            Double.parseDouble(priceEdit.getText().toString()),
                            categories.getSelectedItem().toString());
                }
                else{
                    Toast.makeText(EditActivity.this, "please enter valid numbers for quantity and price", Toast.LENGTH_LONG).show();
                    throw new UpdateError();
                }
        }



    public void setSpinnerText(String text){
        String compareValue = text;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            categories.setSelection(spinnerPosition);
        }
    }

    private StringStatus editNameValidity(String name) throws UpdateError {
        if(!MainActivity.isStringValid(name)) return StringStatus.NAME_NOT_VALID;
        else if(itemExists(name)) return StringStatus.NAME_EXIST;
        else return StringStatus.NAME_OK;

    }

    private boolean itemExists(String itemName) throws UpdateError {
        String newItemID = db.getIdByName(itemName);
        for(String it: itemList){
            if (it.equals(itemName)) {
                if(origItemID.equals(newItemID)){
                    return false;
                }
                return true;
            }

        }
        return false;
    }

    private boolean isANum(String str){
        boolean numeric = true;
        try{
            Double num = Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            numeric = false;
        }
        return numeric;
    }

    private void quantityEditTouch(){
        quantityEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                quantityEdit.setText("");
                return false;
            }
        });
    }
    private void priceEditTouch(){
        priceEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                priceEdit.setText("");
                return false;
            }
        });
    }

}