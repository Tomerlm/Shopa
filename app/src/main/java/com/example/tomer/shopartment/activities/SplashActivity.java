package com.example.tomer.shopartment.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.tomer.shopartment.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 5000;  // the time in ms which the splash activity will stay on screen TODO change to 5000
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN); //   hides the title bar
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isUserLogged();
            }
        },SPLASH_TIME_OUT);
    }

    private void isUserLogged() {
        if(mAuth.getCurrentUser() == null){
            // User not found - go to Login
            Intent main = new Intent(SplashActivity.this , LoginActivity.class);
            startActivity(main);
            finish();
        }
        else{
            // User found - go to Main
            Intent main = new Intent(SplashActivity.this , MainActivity.class);
            startActivity(main);
            finish();
        }

    }
}
