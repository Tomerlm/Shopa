package com.example.tomer.shopartment;

import android.content.Intent;
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
    Button saveButton;


    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent lastIntent = getIntent(); // gets the previously created intent
        db = new MyDBHandler(this);
        text = lastIntent.getStringExtra("name");
        setAllEdits();
        setSaveButton();

    }

    public void setAllEdits(){
        nameEdit = (EditText) findViewById(R.id.itemNameEdit);
        nameEdit.setText(text);
        quantityEdit = (EditText) findViewById(R.id.quantityEdit);
        priceEdit = (EditText) findViewById(R.id.priceEdit);
        categories = (Spinner) findViewById(R.id.categorySpinner);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(EditActivity.this,
                android.R.layout.simple_list_item_1 ,
                getResources().getStringArray(R.array.categories));
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(catAdapter);

    }

    public void setSaveButton(){
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateValueInDb();
                Toast.makeText(EditActivity.this , "Item Updated" , Toast.LENGTH_SHORT).show();
                Intent main = new Intent();
                main.putExtra("itemName" , nameEdit.getText().toString());
                setResult(EditActivity.RESULT_OK , main);
                finish();

            }
        });
    }

    public void updateValueInDb(){
        String num = db.getIdByName(text);
        db.updateData(num ,
                nameEdit.getText().toString() ,
                Integer.parseInt(quantityEdit.getText().toString()) ,
                Double.parseDouble(priceEdit.getText().toString()) ,
                categories.getSelectedItem().toString());

    }
}