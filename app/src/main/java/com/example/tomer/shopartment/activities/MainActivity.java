package com.example.tomer.shopartment.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;

import com.example.tomer.shopartment.adapters.ChooseListAdapter;
import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.holders.ItemViewHolder;
import com.example.tomer.shopartment.models.Invite;
import com.example.tomer.shopartment.models.Item;
import com.example.tomer.shopartment.models.ShoppingList;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainActivity extends AppCompatActivity {

    // defines
    final String TAG = "MainActivity";
    final String DEFAULT_LIST_NAME = "defaultListName";

    // buttons and visual features
    EditText searchbar;
    TextView totalPrice;
    ImageButton addItemButton;
    ArrayList<Item> createdItems;
    ArrayList<ShoppingList> userLists;
    ChooseListAdapter chooseListAdapter;
    ImageView profilePic;
    TextView username;
    TextView email;
    DrawerLayout drawerLayout;
    RecyclerView itemRecyclerView;
    ActionBarDrawerToggle toggle;
    NavigationView navView;
    Vibrator vibe;

    // firebase stuff
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirestoreRecyclerAdapter<Item , ItemViewHolder> ItemsRecyclerAdapter;
    FirebaseFirestore firestoreDB;
    DocumentReference userRef;
    CollectionReference userShoppingListRef;
    String currListName = DEFAULT_LIST_NAME;
    String currListId;
    CollectionReference currentListRef;
    CollectionReference listToDeleteRef;
    ShoppingList currentListModel = null;
    FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set firebase tools
        mAuth = FirebaseAuth.getInstance();
        authListnerConfig();
        firestoreDB = FirebaseFirestore.getInstance();
        userRef = firestoreDB.collection("users").document(mAuth.getCurrentUser().getEmail());
        userShoppingListRef = firestoreDB.collection("lists").document(mAuth.getCurrentUser().getEmail()).collection("userLists");

        // set views
        searchbar =  findViewById(R.id.searchbar_edit_text);
        addItemButton = findViewById(R.id.searchbar_plus_icon);
        navView = findViewById(R.id.navView);
        totalPrice = findViewById(R.id.totalPriceText);

        // lists for RecycleAdapters
        createdItems = new ArrayList<>();
        userLists = new ArrayList<>();

        // set adapters
        chooseListAdapter = new ChooseListAdapter(userLists);
        itemRecyclerView = findViewById(R.id.itemsRecycleView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        itemRecyclerView.setLayoutManager(layoutManager);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        configNavView();
        initDrawer();
        updateUI();
        configureAddButton();
        setInvitesListener();




    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        vibe.vibrate(50);
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.deleteList:
                deleteList();
                break;
        }
        return super.onOptionsItemSelected(item);
    } // handles the drawer toggle

    private void deleteList() {
        if(currentListModel == null){
            Toast.makeText(this, "no list to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!currentListModel.getCreatedBy().equals(mAuth.getCurrentUser().getEmail())) {
            // delete only localy, because its not the current user's list
            firestoreDB.collection("lists")
                    .document(mAuth.getCurrentUser().getEmail())
                    .collection("userLists")
                    .document(currentListModel.getId()).delete();
            currentListModel = null;
            currentListRef = null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Shopa");

        }
        // current user created the list, need to delete for all the users sharing
        listToDeleteRef = currentListRef;
        clearList();
        firestoreDB.collection("lists")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("userLists")
                .document(currentListModel.getId()).delete();
        currentListModel = null;
        currentListRef = null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Shopa");
    }

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
                        Item item = new Item(searchbar.getText().toString().trim().replaceAll(" +", " ") , 1 , 0.0 , "Other");
                        addToList(item);
                        searchbar.getText().clear();
                        totalPriceSet();


                }
                else {
                    Toast.makeText(MainActivity.this, "Please, enter a valid item name! (english letters and spaces only)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


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
                    listToDeleteRef = currentListRef;
                    clearList();
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
                case R.id.Create:
                    // pop new list dialog
                    drawerLayout.closeDrawers();
                    popNewListDialog();
                    break;
                case R.id.Share:
                    // creating the invite intent
                    drawerLayout.closeDrawers();
                    onInviteClicked();


            }
            return true;
        }

        });
    }

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

    @Override
    public void onBackPressed() { // minimize app

        Fragment chooseListFragment = getSupportFragmentManager().findFragmentByTag("frag1");
        Fragment edittFragment = getSupportFragmentManager().findFragmentByTag("frag2");
        if (chooseListFragment == null && edittFragment == null) {
            //fragment not exist
            super.onBackPressed();
            moveTaskToBack(true);
        }
        else{
            //fragment exist
            super.onBackPressed();
        }

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
        String itemId = currentListRef.document().getId();
        item.setId(itemId);
        currentListRef.document(itemId).set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
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


    private  void clearList(){ // delete all of the items collection documents
        final WriteBatch deleteBatch = firestoreDB.batch();
        listToDeleteRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){ // iterate over all of the docs in current list
                    for (QueryDocumentSnapshot  doc: task.getResult()){
                        deleteBatch.delete(listToDeleteRef.document(doc.toObject(Item.class).getId()));

                    }
                    deleteBatch.commit();
                }
                else{
                    Log.d(TAG, "Error. couldn't delete item");
                }
            }
        });

    }




    private ShoppingList addShoppingList(String listName){

        String shoppingListId = userShoppingListRef.document().getId();
        ShoppingList shoppingList = new ShoppingList(listName , shoppingListId , mAuth.getCurrentUser().getEmail());
        userShoppingListRef.document(shoppingListId).set(shoppingList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG , "list was added");
            }
        });
        currListId = shoppingListId;
        return shoppingList;


    }

    public void setCurrentListRef(ShoppingList shoppingList){ // get called only when a user chose a list
        createdItems.clear();
        currListName = shoppingList.getName();
        currentListModel = shoppingList;
        currentListRef = firestoreDB.collection("items").document(shoppingList.getId()).collection("listItems");
        updateScreen();
        totalPriceSet();
        setNewTitle();



    }

    public void editItem(Item oldItem , Item newItem){

        currentListRef.document(oldItem.getId()).set(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "item was successfully edited");
            }
        });
        totalPriceSet();

    }

    public void removeItem(Item newItem) {
        currentListRef.document(newItem.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "item was successfully deleted");
            }
        });
        totalPriceSet();
    }

    private void updateScreen() {
        fragmentManager = getSupportFragmentManager();
        Query query = currentListRef.orderBy("category" , Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Item> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query , Item.class)
                .build();

        ItemsRecyclerAdapter = new FirestoreRecyclerAdapter<Item, ItemViewHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {

                holder.setItem(MainActivity.this , model , fragmentManager);

            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_view, viewGroup , false);
                return new ItemViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };

        itemRecyclerView.setAdapter(ItemsRecyclerAdapter);
        ItemsRecyclerAdapter.startListening();

    }

    public void setNewTitle(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(currListName);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
        if(ItemsRecyclerAdapter != null) {
            ItemsRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListner);
        if(ItemsRecyclerAdapter != null) {
            ItemsRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ItemsRecyclerAdapter != null) {
            ItemsRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListner);
        if(ItemsRecyclerAdapter != null) {
            ItemsRecyclerAdapter.startListening();
        }


    }

    private void popNewListDialog(){

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
                    ShoppingList shoppingList = addShoppingList(listName);
                    currentListRef = firestoreDB.collection("items").document(currListId).collection("listItems");
                    currListName = listName;
                    setCurrentListRef(shoppingList);

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

    private void onInviteClicked() {
        if(!mAuth.getCurrentUser().getEmail().equals(currentListModel.getCreatedBy())){
            Toast.makeText(MainActivity.this, "You can't invite to share a list you haven't created", Toast.LENGTH_LONG).show();

        }
        else if (currentListModel == null){
            Toast.makeText(this, "please choose a list to share first", Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Enter friend's email: ");
            final EditText listNameEdit = new EditText(MainActivity.this);
            listNameEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            listNameEdit.setHint("friend's email");
            listNameEdit.setHintTextColor(Color.GRAY);
            builder.setView(listNameEdit);
            builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //when invite pressed
                    String emailToInvite = listNameEdit.getText().toString().toLowerCase();
                    DocumentReference inviteRef = firestoreDB.collection("invites").document(emailToInvite).collection("userInvites").document(currentListModel.getId());
                    Invite invite = new Invite(currentListModel.getId(), currentListModel, mAuth.getCurrentUser().getDisplayName());
                    inviteRef.set(invite).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Invite was sent.");
                            Toast.makeText(MainActivity.this, "an invitation was sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to send invite");
                        }
                    });

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
    }

    public void setInvitesListener(){
        firestoreDB.collection("invites")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("userInvites")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        // listen success, listing all invitations on the log
                        if(!value.isEmpty()) {
                            for (DocumentSnapshot doc : value) {
                                Invite invite = (Invite) doc.toObject(Invite.class);
                                popAcceptInviteDialog(invite.getSentBy() , invite);
                            }
                        }
                    }
                });
    }


    private void popAcceptInviteDialog(String displayName, final Invite invite) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("your friend " + displayName + " wants to share a list with you!");
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //when accept pressed, add shared list to user's lists and delete the invite.
                DocumentReference inviteRef = firestoreDB.collection("invites").document(mAuth.getCurrentUser().getEmail()).collection("userInvites").document(invite.getInviteId());
                userShoppingListRef.document(invite.getInviteId()).set(invite.getShoppingList());
                inviteRef.delete();
            }
        });
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //when Decline is pressed , ignore invite and delete it.
                DocumentReference inviteRef = firestoreDB.collection("invites").document(mAuth.getCurrentUser().getEmail()).collection("userInvites").document(invite.getInviteId());
                inviteRef.delete();
                Toast.makeText(MainActivity.this, "Invite was declined.", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.long_click_menu , menu);
        return true;
    }

    // TODO search for firestore offline capabilities

}




