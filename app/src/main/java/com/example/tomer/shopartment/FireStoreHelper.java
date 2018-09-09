package com.example.tomer.shopartment;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class FireStoreHelper {
    private FirebaseFirestore firestoreDB;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DocumentReference userRef;
    private DocumentReference currListRef;
    private boolean hasList = false;

    private final String TAG = "FirestoreHelper";


    class UserNullExeption extends Exception{
        @Override
        public String getMessage() {
            return "Null user exception.";
        }
    }

    public FireStoreHelper() throws UserNullExeption{
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            userRef = firestoreDB.collection("users").document(user.getUid());
        }
        else{
            throw new UserNullExeption();
        }
    }


    public void setHasListTrue() {
        Map<String , Object> note = new HashMap<>();
        note.put("hasList" , true);
        userRef.set(note , SetOptions.merge());
    }

    public void setHasListFalse() {
        Map<String , Object> note = new HashMap<>();
        note.put("hasList" , false);
        userRef.set(note , SetOptions.merge());
    }

    public void setListRef(DocumentReference ref) {
        currListRef = ref;
    }

    public void addToList(Item item){
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


    public void removeFromList(String name){
        currListRef.collection("items").document(name).delete();
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




    public void addUser(){
        //add user to the db
        userRef.set(new User(user.getUid(), user.getDisplayName() , user.getEmail()));
    }

    public boolean nameExists(String name){
       firestoreDB.collection("users").whereEqualTo("name" , name)
               .get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       int count = 0;
                       for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                           count++;
                           }
                       }
               });
       return true; // TODO cont
    }
}

