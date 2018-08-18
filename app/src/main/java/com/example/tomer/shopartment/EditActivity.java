package com.example.tomer.shopartment;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    MyDBHandler db;
    Spinner categories;
    EditText nameEdit;
    EditText quantityEdit;
    EditText priceEdit;
    EditText categoryEdit;
    FloatingActionButton fab;
    Vibrator vibe;

    String itemName;
    String[] attributes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent lastIntent = getIntent(); // gets the previously created intent
        db = new MyDBHandler(this);
        itemName = lastIntent.getStringExtra("name");
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
        priceEdit = (EditText) findViewById(R.id.priceEdit);
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
                updateValueInDb();
                Toast.makeText(EditActivity.this, "Item Updated", Toast.LENGTH_SHORT).show();
                Intent main = new Intent();
                main.putExtra("itemName", nameEdit.getText().toString());
                setResult(EditActivity.RESULT_OK, main);
                finish();
            }
        });

    }

    public void updateValueInDb(){
        String num = db.getIdByName(itemName);
        db.updateData(num ,
                nameEdit.getText().toString() ,
                Integer.parseInt(quantityEdit.getText().toString()) ,
                Double.parseDouble(priceEdit.getText().toString()) ,
                categories.getSelectedItem().toString());

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
}