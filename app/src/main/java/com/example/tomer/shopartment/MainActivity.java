package com.example.tomer.shopartment;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainActivity extends AppCompatActivity {
    // buttons and visual features
    MyDBHandler db;
    EditText searchbar;
    TextView totalPrice;
    ImageButton addItemButton;
    ListView printLayout;
    ArrayList<Object> createdItems;
    ItemAdapter adapter;
    CategoryHandler currentCategories;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navView;
    Vibrator vibe;
    Item lastItem;

    // firebase stuff
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    ImageView profilePic;
    TextView username;
    TextView email;
    FirebaseFirestore firestoreDB;
    DocumentReference userRef;
    List<String> shared_lists;
    String currListName;

    // defines
    static boolean white = true;
    static int currentClickId = 0;
    final int REQUEST = 99;
    final String KEY_USERS = "users";
    final String KEY_LISTS = "lists";
    final String KEY_LIST_NAME = "name";
    final String KEY_OWNER = "owner";
    final String KEY_HAS_ACCESS = "hasAccess";
    final String TAG = "MainActivity";

    @Override
    protected void onStart() {
        super.onStart();
        // the state listener checks if a user is connected or not
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDBHandler(this);
        searchbar = (EditText) findViewById(R.id.searchbar_edit_text);
        addItemButton = (ImageButton) findViewById(R.id.searchbar_plus_icon);
        createdItems = new ArrayList<>();
        currentCategories = new CategoryHandler();
        printLayout = (ListView) findViewById(R.id.printLayout);
        adapter = new ItemAdapter(this , createdItems);
        printLayout.setAdapter(adapter);
        navView = (NavigationView) findViewById(R.id.navView);
        totalPrice = (TextView) findViewById(R.id.totalPriceText);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        userRef = firestoreDB.collection(KEY_USERS).document(mAuth.getUid());
       // getUserList();
        authListnerConfig();
        configNavView();
        initDrawer();
        updateUI();
        configureAddButton();
        setItemClick();
        restoreDb();
        listNameDialogPop();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        vibe.vibrate(50);
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    } // handles the drawer toggle

    public void configureAddButton() {
        searchbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                addData();
                return false;
            }
        });
    } // configure the plus button to call the addData function


    private void addData() { // add an item to the db and print it to the screen
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(50);
                if (isStringValid(searchbar.getText().toString())) {
                    if(!db.nameExists(searchbar.getText().toString())) {
                        boolean status = db.insertData(searchbar.getText().toString().trim().replaceAll(" +", " "),
                                1, 0.0, "Other"); // add to db
                        if (status) {
                            showOnScreen(searchbar.getText().toString().trim().replaceAll(" +", " "), 1, 0, "Other");
                            searchbar.getText().clear();
                            totalPrice.setText("Total Price: " + db.getTotalPrice());
                        } else {
                            Toast.makeText(MainActivity.this, "Error. Item was not inserted.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Item already exist. you can edit quantity instead.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Please, enter a valid item name! (english letters and spaces only)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showOnScreen(String name ,int quantity , double price , String catName) {

        adapter.add(new String(catName));  // if exist return true and add 1 to its amount , if not: create new and returns false
        Item current = new Item(name , quantity , price , catName);
        insertInRightPos(current);
        ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();

    } // this method shows on screen the new item as button

    private void restoreDb() {
        int size = db.size();
        if (size == 0) {
            totalPrice.setText("Total Price: 0.0" );
            return;
        }
        Cursor names = db.getAllNames();
        names.moveToFirst();
        Cursor nums = db.getAllIds();
        nums.moveToFirst();
        String currName;
        for (int j = 0; j < size; j++) {
            currName = names.getString(0);
            showOnScreen(currName , db.getQuantityByName(currName) , db.getPriceByName(currName), db.getCategoryByName(currName));
            nums.moveToNext();
            names.moveToNext();
        }
        totalPrice.setText("Total Price: " + db.getTotalPrice());
    } // print the saved db to the screen by iterating cursor and using showOnScreen mathod.

    private void goToEdit(String itemName) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("name", itemName);
        //intent.putStringArrayListExtra("itemList" , createdItems()); // TODO why it doesnt crash?
        startActivityForResult(intent, REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST): {
                if (resultCode == EditActivity.RESULT_OK) {
                    String newName = data.getStringExtra("itemName");
                    String[] newData = db.columnToStrings(newName);
                    adapter.remove(lastItem);
                    showOnScreen(newData[1] , Integer.parseInt(newData[2]) ,Double.parseDouble(newData[3])  , newData[4] );
                    ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();
                    totalPrice.setText("Total Price: " + db.getTotalPrice());
                }
                else if(resultCode == EditActivity.DELETE_REQUEST){
                    adapter.remove(lastItem);
                    ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();
                        if(createdItems.size() == 0){
                            totalPrice.setText("Total Price: 0.0");
                        }
                        else{
                            totalPrice.setText("Total Price: " + db.getTotalPrice());
                        }
                    }
                    else {
                        totalPrice.setText("Total Price: " + db.getTotalPrice());
                    }
                }
                break;
            }
        } // handles the result returning from editActivity (add or remove an item)


    private void clearItemList() {
        if (createdItems.size() == 0) {
            return;
        }
        for (Object it : createdItems) {
            if (it instanceof Item){
                db.removeData(((Item) it).getName());
            }
        }
        adapter.clear();
        totalPrice.setText("Total Price: 0.0");
    } // clear the list and the db

    public static boolean isStringValid(String str) {
        Pattern p = Pattern.compile("^[ A-Za-zא-ת]+$");
        Matcher m = p.matcher(str);
        return m.matches();

    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View hView =  navView.getHeaderView(0);
        profilePic = (ImageView) hView.findViewById(R.id.profilePicDrawer);
        username = (TextView) hView.findViewById(R.id.usernameTextDrawer);
        email = (TextView) hView.findViewById(R.id.emailTextDrawer);
    }

    private void configNavView() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {    @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            vibe.vibrate(50);
            int id = item.getItemId();
            switch (id) {
                case R.id.Clear:  // clear current list (assuming we have only one list at a time).
                    clearItemList();
                    drawerLayout.closeDrawers();
                    break;
                case R.id.Logout:  // logout current user
                    mAuth.signOut();
                    Intent login = new Intent(MainActivity.this , LoginActivity.class);
                    startActivity(login);
                    finish();
                    break;
                case R.id.View:
                    drawerLayout.closeDrawers();
                    Intent view = new Intent(MainActivity.this, ViewActivity.class);
                    startActivity(view);
                    break;
            }
            return true;
        }

        });
    }

    private void setItemClick() { // move to edit if item is clicked
        printLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapter.getItem(i) instanceof Item) {
                    lastItem = (Item) adapter.getItem(i);
                    currentClickId = view.getId();
                    goToEdit(((Item) adapter.getItem(i)).getName());
                }

            }
        });
    }

    private void insertInRightPos(Item item){
        int index = 0;
        for(Object it: createdItems ){
            if(it instanceof String){
                if(it.equals(item.getCategory())){
                    index++;
                    break;
                }
            }
            index++;
        }
        createdItems.add(index , item);
    } // after an item has been edit to a new category, adds it under the right category

    private void updateUI(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            String displayname = user.getDisplayName();
            profilePic.setImageURI(user.getPhotoUrl());
            username.setText(displayname);
            email.setText(user.getEmail());

        }
    }

    private void authListnerConfig() {
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent main = new Intent(MainActivity.this , LoginActivity.class);
                    startActivity(main);
                    finish();
                }
            }
        };
    }
/**
    private void getUserList(){
       userRef.get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                         //   shared_lists = (List<String>) documentSnapshot.get(KEY_SHARED_LISTS);
                            if(shared_lists.size() == 0 ){
                                listNameDialogPop();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Doc doesn't exist.", Toast.LENGTH_SHORT).show();
                        }
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(MainActivity.this, "Error. can't get data", Toast.LENGTH_SHORT).show();
                   }
               });

    }
 **/

    @Override
    public void onBackPressed() { // minimize app
        super.onBackPressed();
        moveTaskToBack(true);
    }

    private void listNameDialogPop(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View newListDialog = getLayoutInflater().inflate(R.layout.dialog_newlist , null);
        final EditText newListName = newListDialog.findViewById(R.id.listNameEdit);
        final Button confirmList = newListDialog.findViewById(R.id.listNameConfirm);

        mBuilder.setView(newListDialog);
        final AlertDialog dialog = mBuilder.create();
        confirmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStringValid(newListName.getText().toString())){
                    currListName = newListName.getText().toString().trim();
                    itemList mList = new itemList(currListName , mAuth.getCurrentUser().getEmail());
                    Map<String , Object> listMap = new HashMap<>();
                    listMap.put(KEY_LIST_NAME , currListName);
                    listMap.put(KEY_OWNER , mAuth.getCurrentUser().getEmail());
                    listMap.put(KEY_HAS_ACCESS , Arrays.asList(mAuth.getCurrentUser().getEmail()));
                    //TODO add list to current user 'hasAccess' field
                    firestoreDB.collection(KEY_LISTS).document().set(listMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error. couldn't write data");
                                }
                            });
                    dialog.dismiss();





                }
                else{
                    Toast.makeText(MainActivity.this, "name not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();


    }
}




