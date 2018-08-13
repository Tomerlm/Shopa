package com.example.tomer.shopartment;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewActivity extends AppCompatActivity {
    MyDBHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        db = new MyDBHandler(this);
        ViewAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // 3 dot menu 2 options:
        {
            int id = item.getItemId();
            switch (id) {
                case R.id.Back:  // clear current list (assuming we have only one list at a time.
                    finish();
                    break;
            }
        }
        return true;
    }

    public void ViewAll(){
        Cursor result = db.getAllData();
        if (result.getCount() == 0){

            showMessage("Error." ,"Nothing found.");

        }
        else{
            StringBuffer buffer = new StringBuffer();
            while (result.moveToNext()){
                buffer.append("Item number: "+ result.getString(0) + "\n" );
                buffer.append("Item name: "+ result.getString(1) + "\n" );
                buffer.append("Quantity: "+ result.getString(2) + "\n" );
                buffer.append("Approx price: "+ result.getString(3) + "\n" );
            }

            showMessage("Your Current List" , buffer.toString());
        }
    }

    public void showMessage(String title , String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
    }

}
