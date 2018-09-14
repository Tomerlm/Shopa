package com.example.tomer.shopartment.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;

import com.example.tomer.shopartment.CategoryHandler;
import com.example.tomer.shopartment.ItemAdapter;
import com.example.tomer.shopartment.MyDBHandler;
import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ShoppingList;
import com.example.tomer.shopartment.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String lastItemDocId;

    // firebase stuff
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    ImageView profilePic;
    TextView username;
    TextView email;
    FirebaseFirestore firestoreDB;
    DocumentReference userRef;
    DocumentReference currListRef;
    List<String> shared_lists;
    String currListName;
    String lastList;

    //FireStoreHelper mFirestoreHelper;

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
        checkIfGotList();
        setCurrnetListRef();
        configNavView();
        initDrawer();
        updateUI();
        configureAddButton();
        setItemClick();



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
                        Item item = new Item(searchbar.getText().toString().trim().replaceAll(" +", " ") , 1 , 0.0 , "Other");
                        addToList(item);
                        boolean status = db.insertData(searchbar.getText().toString().trim().replaceAll(" +", " "),
                                1, 0.0, "Other"); // add to db
                        if (status) {
                            Item itemToAdd = new Item(searchbar.getText().toString().trim().replaceAll(" +", " "), 1, 0, "Other");
                            showOnScreen(item);
                            searchbar.getText().clear();
                            totalPriceSet();
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

    private void showOnScreen(Item item) {

        adapter.add(item.getCategory());  // if exist return true and add 1 to its amount , if not: create new and returns false
        insertInRightPos(item);
        ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();

    } // this method shows on screen the new item as button

    private void restoreList() {
        currListRef.collection("items").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            Item item = doc.toObject(Item.class);
                            showOnScreen(item);

                        }
                        totalPriceSet();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG , "Couldn't restore list");
                    }
                });

        /**
        int size = db.size();
        if (size == 0) {
            totalPrice.setText(R.string.totalPrice0);
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
         **/
    } // print the saved db to the screen by iterating cursor and using showOnScreen mathod.

    private void goToEdit(String itemId) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("currItemId", itemId);
        intent.putExtra("currListId" , currListRef.getId());
        startActivityForResult(intent, REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST): {
                if (resultCode == EditActivity.RESULT_OK) {
                    // item was editted - applay changes
                    String newName = data.getStringExtra("itemName");
                    String[] newData = db.columnToStrings(newName);
                    adapter.remove(lastItem);
                    removeFromList(lastItem.getName());
                    Item newItem = new Item(newData[1] , Integer.parseInt(newData[2]) ,Double.parseDouble(newData[3])  , newData[4] );
                    showOnScreen(newItem);// (name , quantity , price , category)
                    Item item = new Item(newData[1] ,Integer.parseInt(newData[2]), Double.parseDouble(newData[3]) , newData[4]);
                    addToList(item);
                    ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();
                    totalPriceSet();
                }
                else if(resultCode == EditActivity.DELETE_REQUEST){
                    // edit was canceled
                    adapter.remove(lastItem);
                    removeFromList(lastItem.getName());
                    ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();
                        if(createdItems.size() == 0){
                            totalPrice.setText(R.string.totalPrice0);
                        }
                        else{
                            totalPriceSet();
                        }
                    }
                    else {
                     totalPriceSet();
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
        clearList();
        adapter.clear();
        totalPrice.setText(R.string.totalPrice0);
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
                    currListRef.collection("items").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                        Item item = doc.toObject(Item.class);
                                        if(lastItem.getName().equals(item.getName())){
                                           goToEdit(doc.getId());
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG ,"failed to get doc id");
                                }
                            });
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

    public void listNameDialogPop(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View newListDialog = getLayoutInflater().inflate(R.layout.dialog_newlist , null);
        final EditText newListName = newListDialog.findViewById(R.id.listNameEdit);
        final Button confirmList = newListDialog.findViewById(R.id.listNameConfirm);

        mBuilder.setView(newListDialog);
        final AlertDialog dialog = mBuilder.create();
        confirmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.isStringValid(newListName.getText().toString())){
                    currListName = newListName.getText().toString().trim();

                    //create the new list and add to "lists" collection
                    Map<String , Object> listMap = new HashMap<>();
                    listMap.put(KEY_LIST_NAME , currListName);
                    listMap.put(KEY_OWNER , mAuth.getCurrentUser().getEmail());
                    listMap.put(KEY_HAS_ACCESS , Arrays.asList(mAuth.getCurrentUser().getEmail()));
                    userRef.update("hasAccess" , FieldValue.arrayUnion(currListName));
                    currListRef = firestoreDB.collection(KEY_LISTS).document(mAuth.getCurrentUser().getEmail());

                   // mFirestoreHelper.setListRef(currListRef);
                    currListRef.set(listMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    setHasListTrue();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error. couldn't write data");
                                }
                            });
                    restoreList();
                    dialog.dismiss();





                }
                else{
                    Toast.makeText(MainActivity.this, "name not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        }


    public void checkIfGotList(){
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            if (documentSnapshot.get("hasList").equals(false)) {
                                listNameDialogPop();

                            }
                            else{
                                User user = documentSnapshot.toObject(User.class);
                                ArrayList<String> lists = user.getLists();
                                currListName = lists.get(lists.size()-1);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "An error has occurred while trying check if user has list");
            }
        });
    }

    private void totalPriceSet(){
        currListRef.collection("items").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        double totPrice = 0;
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            Item item = doc.toObject(Item.class);
                            totPrice += item.getApproxPrice();
                        }
                        totalPrice.setText("Total Price: " + totPrice);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error. couldn't calculate total price");
                    }
                });
    }

    private void addToList(Item item){
        currListRef.collection("items").document().set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Item successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error. couldn't upload item");
                    }
                });
    }

    private void removeFromList(String name){
        currListRef.collection("items").document(name).delete();
    }

    private void setCurrnetListRef(){
        firestoreDB.collection(KEY_LISTS).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            if(doc.get("name").equals(currListName)){
                                String newId = doc.getId();
                                currListRef = firestoreDB.collection(KEY_LISTS).document(newId);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG , "somting went wrong");
                    }
                });
    }

    private void setHasListTrue() {
        Map<String , Object> note = new HashMap<>();
        note.put("hasList" , true);
        userRef.set(note , SetOptions.merge());
    }

    public  void clearList(){ // delete all of the items collection documents
        final WriteBatch deleteBatch = firestoreDB.batch();
        currListRef.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){ // iterate over all of the docs in current list
                    for (QueryDocumentSnapshot  doc: task.getResult()){
                        deleteBatch.delete(currListRef.collection("items").document(doc.toObject(Item.class).getName()));

                    }
                    deleteBatch.commit();
                }
                else{
                    Log.d(TAG, "Error. couldn't delete item");
                }
            }
        });

    }

    public void chooseListDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View newListDialog = getLayoutInflater().inflate(R.layout.dialog_choose_list , null);
        final RecyclerView listsRecyclerView = findViewById(R.id.lists_recycler);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TextView empty = findViewById(R.id.empty_view);
        final ProgressBar progressBar = findViewById(R.id.lists_dialog_progress);
        mBuilder.setView(newListDialog);
        final AlertDialog dialog = mBuilder.create();
        Query query = firestoreDB.collection("lists").document(mAuth.getCurrentUser().getEmail()).collection("userLists").orderBy("name" , Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ShoppingList> mFirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ShoppingList>().setQuery(query , ShoppingList.class).build();
        confirmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.isStringValid(newListName.getText().toString())){
                    currListName = newListName.getText().toString().trim();

                    //create the new list and add to "lists" collection
                    Map<String , Object> listMap = new HashMap<>();
                    listMap.put(KEY_LIST_NAME , currListName);
                    listMap.put(KEY_OWNER , mAuth.getCurrentUser().getEmail());
                    listMap.put(KEY_HAS_ACCESS , Arrays.asList(mAuth.getCurrentUser().getEmail()));
                    userRef.update("hasAccess" , FieldValue.arrayUnion(currListName));
                    currListRef = firestoreDB.collection(KEY_LISTS).document(mAuth.getCurrentUser().getEmail());

                    // mFirestoreHelper.setListRef(currListRef);
                    currListRef.set(listMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    setHasListTrue();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error. couldn't write data");
                                }
                            });
                    restoreList();
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




