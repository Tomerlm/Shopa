package com.example.tomer.shopartment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    MyDBHandler db;
    EditText searchbar;
    ImageButton addItemButton;
    LinearLayout printLayout;
    TextView check;
    static int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new MyDBHandler(this);
        searchbar = (EditText) findViewById(R.id.searchbar_edit_text);
        addItemButton = (ImageButton) findViewById(R.id.searchbar_plus_icon);
        check = (TextView) findViewById(R.id.textViewCheck);

        configureAddButton();
        checkClickable();

    }

    public void checkClickable() {
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOnScreen("IS CLOCK");
            }
        });
    }

    // Creates the 3 dot menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // 3 dot menu 2 options:
        {
            int id = item.getItemId();
            switch (id){
                case R.id.Clear:  // clear current list (assuming we have only one list at a time).
                    Toast.makeText(this,"list cleared" , Toast.LENGTH_SHORT).show();
                    break;
                case R.id.Exit:  // kill the process
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
            }
        }
        return true;
    }

    public void configureAddButton(){
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });
    }


    public void addData(){ // add an item to the db. TODO: work on exceptions! check if item exists by name, illegal chars etc.
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = db.insertData(searchbar.getText().toString() ,
                        0 , 0.0);
                if (status){
                    Toast.makeText(MainActivity.this , "The item was inserted to the list!" , Toast.LENGTH_LONG).show();
                    showOnScreen(searchbar.getText().toString());
                    searchbar.getText().clear();
                }
                else{
                    Toast.makeText(MainActivity.this , "Error. Item was not inserted." , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showOnScreen(String name){  // this method shows on screen the new item as button
        printLayout = (LinearLayout) findViewById(R.id.printLayout);
        TextView item = new TextView(MainActivity.this);
        LinearLayout.LayoutParams printParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT ,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        setClickableTextview(item , name);
        printLayout.addView(item , printParams);



    }

    private void setClickableTextview(TextView item , String name){
        item.setText(name);
        item.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        item.setTextSize(20);
        item.setId(i++);
        item.setClickable(true);
        item.setBackgroundColor(getResources().getColor(R.color.light_grey));
        item.setTextAppearance(this, R.style.itemTextViewStyle);
    }
}


