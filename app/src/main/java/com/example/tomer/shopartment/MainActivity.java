package com.example.tomer.shopartment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                case R.id.Clear:  // clear current list (assuming we have only one list at a time.
                    Toast.makeText(this,"list cleared" , Toast.LENGTH_SHORT).show();
                    break;
                case R.id.Exit:  // kill the proccess
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
            }
        }
        return true;
    }
}
