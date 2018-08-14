package com.example.tomer.shopartment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
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
    TextView gap;
    static int i = 1;
    static boolean white = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new MyDBHandler(this);
        searchbar = (EditText) findViewById(R.id.searchbar_edit_text);
        addItemButton = (ImageButton) findViewById(R.id.searchbar_plus_icon);
        gap = (TextView) findViewById(R.id.textViewGap);

        configureAddButton();

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
        setClickableTextview(item , name , printLayout , printParams);




    }

    private void setClickableTextview(TextView item , String name , LinearLayout printLayout , LinearLayout.LayoutParams printParams){ //TODO: switch between white and grey
        item.setText(name);
        item.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        item.setTextSize(20);
        item.setId(i++);
        item.setClickable(true);
        item.setHeight(160);
        if(white){
            item.setBackgroundColor(getResources().getColor(R.color.light_grey));
            white = false;
        }
        else{
            item.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            white = true;
        }
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setTextAppearance(this, R.style.itemTextViewStyle);
        configureItemClick(item);
        printLayout.addView(item , printParams);
    }

    public void configureItemClick(TextView item){
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this , "TOST" , Toast.LENGTH_SHORT).show(); //TODO: will change to another intent in the future
            }
        });

    }
}


