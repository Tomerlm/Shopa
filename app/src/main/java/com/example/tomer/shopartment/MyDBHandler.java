package com.example.tomer.shopartment;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
public class MyDBHandler extends SQLiteOpenHelper {

        //information of database
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "ItemsDB.db";
        public static final String TABLE_NAME = "Item";
        public static final String COLUMN_NAME= "ItemName";
        public static final String COLUMN_QUANTITY = "Quantity";
    public static final String COLUMN_PRICE = "ApproxPrice";
        //initialize the database
        public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( " + COLUMN_NAME +
                    "TEXT," + COLUMN_QUANTITY + "INTEGER," + COLUMN_PRICE +"INTEGER )";
            db.execSQL(CREATE_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int j) {}
        public String loadHandler() {

            String result = "";
            String query = "Select * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                int result_0 = cursor.getInt(0);
                String result_1 = cursor.getString(1);
                result += String.valueOf(result_0) + " " + result_1 +
                        System.getProperty("line.separator");
            }
            cursor.close();
            db.close();
            return result;
        }
        }
        public void addHandler(Item item) {}
        public Item findHandler(String item_name) {}
        public boolean deleteHandler(int ID) {}
        public boolean updateHandler(int ID, String name) {}
    }

