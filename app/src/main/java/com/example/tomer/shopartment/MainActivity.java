package com.example.tomer.shopartment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
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
    ArrayList<TextView> createdItems;
    static int i = 1; //TODO: find max position of a value in the db, and update i to it on create. IMPORTENT
    static boolean white = true;
    static int currentClickId = 0;
    final int REQUEST = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDBHandler(this);
        searchbar = (EditText) findViewById(R.id.searchbar_edit_text);
        addItemButton = (ImageButton) findViewById(R.id.searchbar_plus_icon);
        gap = (TextView) findViewById(R.id.textViewGap);
        createdItems = new ArrayList<TextView>();
        configureAddButton();
        restoreDb();

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
                case R.id.View:
                    Intent view = new Intent(MainActivity.this , ViewActivity.class);
                    startActivity(view);
                    break;
            }
        }
        return true;
    }

    public void configureAddButton(){
        searchbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addData();
                return false;
            }
        });
    }


    public void addData(){ // add an item to the db. TODO: work on exceptions! check if item exists by name, illegal chars etc.
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = db.insertData(searchbar.getText().toString() ,
                        0 , 0.0 , "General");
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

    private void setClickableTextview(TextView item , String name , LinearLayout printLayout , LinearLayout.LayoutParams printParams){
        item.setText(name);
        item.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        item.setTextSize(20);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            item.setId(View.generateViewId());
            i++;
        }
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
        createdItems.add(item);
        printLayout.addView(item , printParams);
    }

    public void configureItemClick(TextView item){
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView newItem = findViewById(view.getId());
                currentClickId = view.getId();
                goToEdit(newItem.getText().toString());
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
             default:
                return super.onContextItemSelected(item);
        }
    }


    public void removeViewAndColorize(){ // change the color of all textViews below the one deleted
        printLayout.removeView(findViewById(currentClickId));
        int colorCode = getResources().getColor(R.color.light_grey);
        if (white) white = false;
        else white = true;
        boolean found = false;
        TextView temp = null;
        for(TextView it: createdItems){
            if (it.getId() == currentClickId){ // should always find a match
                found = true;
                temp = it;
                continue;
            }
            if(found) {
                if (it.getBackground() instanceof ColorDrawable) {
                    ColorDrawable cd = (ColorDrawable) it.getBackground();
                    colorCode = cd.getColor();
                }
                if (colorCode == getResources().getColor(R.color.light_grey)) {
                    it.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    it.setBackgroundColor(getResources().getColor(R.color.light_grey));
                }

            }
        }
        createdItems.remove(temp);
    }

    public void restoreDb(){
        int size = db.size();
        if(size == 0){
            return;
        }
        Cursor names = db.getAllNames();
        names.moveToFirst();
        Cursor nums = db.getAllIds();
        nums.moveToFirst();
        int maxId = 0;
        for (int j = 0; j < size; j++ ){
            showOnScreen(names.getString(0));
            if (Integer.parseInt(nums.getString(0)) > maxId){
                maxId = Integer.parseInt(nums.getString(0));
            }
            nums.moveToNext();
            names.moveToNext();
        }
        i = maxId + 1;
    }

    public void goToEdit(String itemName){
        Intent intent = new Intent(MainActivity.this , EditActivity.class);
        intent.putExtra("name" , itemName);
        startActivityForResult(intent , REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (REQUEST) : {
                if (resultCode == EditActivity.RESULT_OK) {
                    String newName = data.getStringExtra("itemName");
                    TextView currItem = (TextView) findViewById(currentClickId);
                    currItem.setText(newName);
                }
                break;
            }
        }
    }
}


