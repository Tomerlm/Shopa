package com.example.tomer.shopartment.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;

import com.example.tomer.shopartment.CategoryHandler;
import com.example.tomer.shopartment.adapters.ChooseListAdapter;
import com.example.tomer.shopartment.adapters.ItemAdapter;
import com.example.tomer.shopartment.MyDBHandler;
import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.adapters.ItemAdapterV2;
import com.example.tomer.shopartment.holders.ItemViewHolder;
import com.example.tomer.shopartment.holders.ShoppingListViewHolder;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ShoppingList;
import com.example.tomer.shopartment.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
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
    RecyclerView printLayout;
    ArrayList<Item> createdItems;
    ArrayList<ShoppingList> userLists;
    ItemAdapterV2 itemListAdapter;
    ChooseListAdapter chooseListAdapter;
    FloatingActionButton createListButton;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navView;
    Vibrator vibe;
    Item lastItem;
    String lastItemDocId;

    // firebase stuff
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirestoreRecyclerAdapter<Item , ItemViewHolder> ItemsRecyclerAdapter;

    ImageView profilePic;
    TextView username;
    TextView email;
    FirebaseFirestore firestoreDB;
    DocumentReference userRef;
    DocumentReference currListRef;
    CollectionReference userShoppingListRef;
    List<String> shared_lists;
    String currListName = "defaultListName";
    String lastList;
    String currListId;
    CollectionReference currentListRef;

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
    final String DEFAULT_LIST_NAME = "defaultListName";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set firebase tools
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        userRef = firestoreDB.collection(KEY_USERS).document(mAuth.getCurrentUser().getEmail());
        userShoppingListRef = firestoreDB.collection("lists").document(mAuth.getCurrentUser().getEmail()).collection("userLists");

        db = new MyDBHandler(this);
        // set views
        searchbar =  findViewById(R.id.searchbar_edit_text);
        addItemButton = findViewById(R.id.searchbar_plus_icon);
        navView = findViewById(R.id.navView);
        totalPrice = findViewById(R.id.totalPriceText);
        createListButton = findViewById(R.id.fabCreateList);

        // lists for RecycleAdapters
        createdItems = new ArrayList<>();
        userLists = new ArrayList<>();

        // set adapters
        printLayout =  findViewById(R.id.printLayout);
        itemListAdapter = new ItemAdapterV2(this, R.layout.item_list_view, createdItems);
        printLayout.setAdapter(itemListAdapter);
        chooseListAdapter = new ChooseListAdapter(userLists);



        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // testChooseDialog();
        // getUserList();
        authListnerConfig();
        createListFab();
        //chooseListDialog();
       // checkIfGotList();
       // setCurrnetListRef();
        configNavView();
        initDrawer();
        updateUI();
        configureAddButton();
        //setItemClick();



    }


    private void createListFab() {

        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Create A List: ");
                final EditText listNameEdit = new EditText(MainActivity.this);
                listNameEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                listNameEdit.setHint("list name");
                listNameEdit.setHintTextColor(Color.GRAY);
                builder.setView(listNameEdit);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //when create pressed

                        String listName = listNameEdit.getText().toString();
                        addShoppingList(listName);
                        currentListRef = firestoreDB.collection("items").document(currListId).collection("listItems");
                        currListName = listName;

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //when cancel is pressed
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });


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
                if(currListName.equals(DEFAULT_LIST_NAME)){
                    Toast.makeText(MainActivity.this, "Please Choose a list first!", Toast.LENGTH_LONG).show();
                    return;
                }
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

        //itemListAdapter.(item.getCategory());  // if exist return true and add 1 to its amount , if not: create new and returns false
        insertInRightPos(item);
        printLayout.getAdapter().notifyDataSetChanged();

    } // this method shows on screen the new item as button


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
     // print the saved db to the screen by iterating cursor and using showOnScreen mathod.

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
                    createdItems.remove(lastItem);
                    removeFromList(lastItem.getName());
                    Item newItem = new Item(newData[1] , Integer.parseInt(newData[2]) ,Double.parseDouble(newData[3])  , newData[4] );
                    showOnScreen(newItem);// (name , quantity , price , category)
                    Item item = new Item(newData[1] ,Integer.parseInt(newData[2]), Double.parseDouble(newData[3]) , newData[4]);
                    addToList(item);
                   // ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();
                    totalPriceSet();
                }
                else if(resultCode == EditActivity.DELETE_REQUEST){
                    // edit was canceled
                    createdItems.remove(lastItem);
                    removeFromList(lastItem.getName());
                   // ((ItemAdapter) printLayout.getAdapter()).notifyDataSetChanged();
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
        createdItems.clear();
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
                case R.id.Choose:
                    // open choose list fragment with all of the user's lists
                    drawerLayout.closeDrawers();
                    ChooseListFragment chooseListFragment = new ChooseListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("userLists" , userLists);
                    chooseListFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.drawer , chooseListFragment , "frag1").addToBackStack("what").commit();

                    break;
            }
            return true;
        }

        });
    }

    /**
    private void setItemClick() { // move to edit if item is clicked
        printLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(itemListAdapter.getItem(i) instanceof Item) {
                    lastItem = (Item) itemListAdapter.getItem(i);
                    currentClickId = view.getId();
                    currentListRef.get()
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
     **/

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

        Fragment chooseListFragment = getSupportFragmentManager().findFragmentByTag("frag1");
        if (chooseListFragment == null) {
            //not exist
            super.onBackPressed();
            moveTaskToBack(true);
        }
        else{
            //fragment exist
            super.onBackPressed();
        }

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
                vibe.vibrate(50);
                if(MainActivity.isStringValid(newListName.getText().toString())){
                    currListName = newListName.getText().toString().trim();
                    currListRef = firestoreDB.collection("lists").document(mAuth.getCurrentUser().getEmail()).collection("userLists").document();
                    String listId = currListRef.getId();

                    //create the new list and add to "lists" collection
                    ShoppingList list = new ShoppingList(currListName , listId , mAuth.getCurrentUser().getEmail() );
                    currentListRef.add(list).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
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


    private void totalPriceSet(){
        currentListRef.get()
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

        currentListRef.document(item.getName()).set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        updateScreen();
    }


    private void removeFromList(String name){
        currentListRef.document(name).delete();
    }



    private  void clearList(){ // delete all of the items collection documents
        final WriteBatch deleteBatch = firestoreDB.batch();
        currentListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){ // iterate over all of the docs in current list
                    for (QueryDocumentSnapshot  doc: task.getResult()){
                        deleteBatch.delete(currentListRef.document(doc.toObject(Item.class).getName()));

                    }
                    deleteBatch.commit();
                }
                else{
                    Log.d(TAG, "Error. couldn't delete item");
                }
            }
        });

    }




    private void addShoppingList(String listName){

        String shoppingListId = userShoppingListRef.document().getId();
        ShoppingList shoppingList = new ShoppingList(listName , shoppingListId , mAuth.getCurrentUser().getEmail());
        userShoppingListRef.document(shoppingListId).set(shoppingList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG , "list was added");
            }
        });
        currListId = shoppingListId;


    }

    public void setCurrentListRef(ShoppingList shoppingList){
        createdItems.clear();
        itemListAdapter.notifyDataSetChanged();
        currListName = shoppingList.getName();
        currentListRef = firestoreDB.collection("items").document(shoppingList.getId()).collection("listItems");
        updateScreen();


    }

    private void updateScreen() { // TODO no use in the itemsAdapterArray... need to figure out this shit
        Query query = currentListRef.orderBy("name" , Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Item> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query , Item.class)
                .build();
        ItemsRecyclerAdapter = new FirestoreRecyclerAdapter<Item, ItemViewHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
                holder.setItem(model);
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_view, viewGroup , false);
                return new ItemViewHolder(view);
            }
        };


    }

    public void setNewTitle(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(currListName);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(ItemsRecyclerAdapter != null) {
            ItemsRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(mAuthListner);
        if(ItemsRecyclerAdapter != null) {
            ItemsRecyclerAdapter.stopListening();
        }
    }





}




