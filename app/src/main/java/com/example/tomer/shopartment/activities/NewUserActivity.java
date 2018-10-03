package com.example.tomer.shopartment.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tomer.shopartment.R;
import com.example.tomer.shopartment.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewUserActivity extends AppCompatActivity {
    EditText username;
    FloatingActionButton fabOK;
    Vibrator vibe;
    FirebaseAuth mAuth;
    FirebaseFirestore firestoreDB;
    DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        username = findViewById(R.id.newUsernameEdit);
        fabOK = findViewById(R.id.fabApproveName);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        userRef = firestoreDB.collection("users").document(mAuth.getCurrentUser().getUid());
        configOkBtn();

    }

    private void configOkBtn(){
        fabOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(50);
                userProfileUpdate();
                Intent main = new Intent(NewUserActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        });

    }

    private void userProfileUpdate(){

        //updates profile with username and dummy profile picture
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            userRef.set(new User(user.getUid(), user.getDisplayName() , user.getEmail()));
            String usernameString = username.getText().toString().trim();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(usernameString)
                    .setPhotoUri(Uri.parse("https://image.ibb.co/eseF9e/trollface.png")).build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("TEST" , "Your profile was updated");
                            }
                        }
                    });
        }
    }

}
