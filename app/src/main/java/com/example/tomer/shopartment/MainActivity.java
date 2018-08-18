package com.example.tomer.shopartment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
    ArrayList<TextView> createdItems;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navView;
    Vibrator vibe;

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
        createdItems = new ArrayList<TextView>();
        navView = (NavigationView) findViewById(R.id.navView);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        configNavView();
        initDrawer();
        configureAddButton();
        restoreDb();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        vibe.vibrate(100);
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void configureAddButton() {
        searchbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vibe.vibrate(100);
                addData();
                return false;
            }
        });
    }


    public void addData() { // add an item to the db. TODO:prevent double spaces, space at begining or ending
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // TODO: add vibration to click
                boolean isValid = isStringValid(searchbar.getText().toString());
                if (isValid) {
                    boolean status = db.insertData(searchbar.getText().toString(),
                            0, 0.0, "Other");
                    if (status) {
                        showOnScreen(searchbar.getText().toString());
                        searchbar.getText().clear();
                    } else {
                        Toast.makeText(MainActivity.this, "Error. Item was not inserted.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please, enter a valid item name! (english letters and spaces only)", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void showOnScreen(String name) {  // this method shows on screen the new item as button
        printLayout = (LinearLayout) findViewById(R.id.printLayout);
        TextView item = new TextView(MainActivity.this);
        LinearLayout.LayoutParams printParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        setClickableTextview(item, name, printLayout, printParams);


    }

    private void setClickableTextview(TextView item, String name, LinearLayout printLayout, LinearLayout.LayoutParams printParams) {
        item.setText(name);
        item.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        item.setTextSize(20);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            item.setId(View.generateViewId());
        }
        item.setClickable(true);
        item.setHeight(160);
        if (white) {
            item.setBackgroundColor(getResources().getColor(R.color.light_grey));
            white = false;
        } else {
            item.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            white = true;
        }
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setTextAppearance(this, R.style.itemTextViewStyle);
        configureItemClick(item);
        registerForContextMenu(item);
        createdItems.add(item);
        printLayout.addView(item, printParams);
    }

    public void configureItemClick(TextView item) {
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView newItem = findViewById(view.getId());
                currentClickId = view.getId();
                goToEdit(newItem.getText().toString());
            }
        });

    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        currentClickId = v.getId();
        getMenuInflater().inflate(R.menu.long_click_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView tv = (TextView) findViewById(currentClickId);
        String text = tv.getText().toString();
        switch (item.getItemId()) {
            case R.id.delete:
                removeViewAndColorize();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void removeViewAndColorize() { // change the color of all textViews below the one deleted
        printLayout.removeView(findViewById(currentClickId));
        int colorCode = getResources().getColor(R.color.light_grey);
        if (white) white = false;
        else white = true;
        boolean found = false;
        TextView temp = null;
        for (TextView it : createdItems) {
            if (it.getId() == currentClickId) { // should always find a match
                found = true;
                temp = it;
                continue;
            }
            if (found) {
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
        db.removeData(temp.getText().toString());
        createdItems.remove(temp);
    }

    public void restoreDb() { // print the saved db to the screen by iterating cursor and using showOnScreen mathod.
        int size = db.size();
        if (size == 0) {
            return;
        }
        Cursor names = db.getAllNames();
        names.moveToFirst();
        Cursor nums = db.getAllIds();
        nums.moveToFirst();
        int maxId = 0;
        for (int j = 0; j < size; j++) {
            showOnScreen(names.getString(0));
            if (Integer.parseInt(nums.getString(0)) > maxId) {
                maxId = Integer.parseInt(nums.getString(0));
            }
            nums.moveToNext();
            names.moveToNext();
        }
    }

    public void goToEdit(String itemName) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("name", itemName);
        startActivityForResult(intent, REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST): {
                if (resultCode == EditActivity.RESULT_OK) {
                    String newName = data.getStringExtra("itemName");
                    TextView currItem = (TextView) findViewById(currentClickId);
                    currItem.setText(newName);
                }
                break;
            }
        }
    }

    public void clearItemList() {
        if (createdItems.size() == 0) {
            return;
        }
        int currId;
        for (TextView it : createdItems) {
            printLayout.removeView(findViewById(it.getId()));
            db.removeData(it.getText().toString());

        }
        createdItems.clear();
    }

    public boolean isStringValid(String str) {
        if (str.isEmpty()) {
            return false;
        }
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!(str.charAt(i) >= 'a' && str.charAt(i) <= 'z' ||
                    str.charAt(i) >= 'A' && str.charAt(i) <= 'Z' ||
                    str.charAt(i) == ' ')) {
                return false;
            }
        }
        return true;
    }

    public void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void configNavView() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.Clear:  // clear current list (assuming we have only one list at a time).
                    clearItemList();
                    break;
                case R.id.Exit:  // kill the process
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                case R.id.View:
                    Intent view = new Intent(MainActivity.this, ViewActivity.class);
                    startActivity(view);
                    break;
            }
            return true;
        }

        });
    }
    public void starWars(){
        vibe.vibrate(new long[]{0, 500, 110, 500, 110, 450, 110, 200, 110, 170, 40, 450, 110, 200, 110, 170, 40, 500}, -1);
    }
}



