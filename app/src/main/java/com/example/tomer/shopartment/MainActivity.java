package com.example.tomer.shopartment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MyDBHandler db;
    EditText searchbar;
    ImageButton addItemButton;
    LinearLayout printLayout;
    TextView gap;
    static int i = 1;
    static boolean white = true;
    ArrayList<TextView> itemsList = new ArrayList<TextView>();
    static int currentClickId = 0;

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
        registerForContextMenu(item);
        printLayout.addView(item , printParams);
    }

    public void configureItemClick(TextView item){
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this , "Move to item settings" , Toast.LENGTH_SHORT).show(); //TODO: will change to another intent in the future
            }
        });

    }



    public void onCreateContextMenu(ContextMenu menu , View v , ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu , v , menuInfo);
        currentClickId = v.getId();
        getMenuInflater().inflate(R.menu.long_click_menu , menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView tv = (TextView) findViewById(currentClickId);
        String text = tv.getText().toString();
        switch (item.getItemId()) {
            case R.id.delete:
                removeViewAndColorize();
                if(db.removeData(text) == 1){
                    Toast.makeText(MainActivity.this , "REMOVED" , Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(MainActivity.this , "FUCKOFF" , Toast.LENGTH_LONG).show();

                return true;
            case R.id.edit:
                Toast.makeText(MainActivity.this, "edited", Toast.LENGTH_SHORT).show();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }


    public void removeViewAndColorize(){
        printLayout.removeView(findViewById(currentClickId));
        TextView current;
        int colorCode = getResources().getColor(R.color.light_grey);
        if (white) white = false;
        else white = true;
        for(int j = currentClickId + 1; j <= i ; j++){
            current = findViewById(j);
            if( current == null){
                continue;
            }
            else{
                if (current.getBackground() instanceof ColorDrawable) {
                    ColorDrawable cd = (ColorDrawable) current.getBackground();
                    colorCode = cd.getColor();
                }
                if (colorCode == getResources().getColor(R.color.light_grey)){
                    current.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                else{
                    current.setBackgroundColor(getResources().getColor(R.color.light_grey));
                }

            }
        }
    }
}


