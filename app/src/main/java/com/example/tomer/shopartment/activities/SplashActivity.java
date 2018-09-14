package com.example.tomer.shopartment.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.tomer.shopartment.R;

public class SplashActivity extends AppCompatActivity {
private static int SPLASH_TIME_OUT = 2;  // the time in ms which the splash activity will stay on screen TODO change to 5000
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN); //   hides the title bar
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {       // a method for delaying the SpalshActivity , and then go directly for the MainActivity
                Intent homeIntent = new Intent(SplashActivity.this , LoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
