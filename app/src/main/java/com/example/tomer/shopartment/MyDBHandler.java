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
        public static final String COLUMN_POS = "Number";
        public static final String COLUMN_NAME= "ItemName";
        public static final String COLUMN_QUANTITY = "Quantity";
    public static final String COLUMN_PRICE = "ApproxPrice";
        //initialize the database
        public MyDBHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( Number INTEGER PRIMARY KEY AUTOINCREMENT , ItemName TEXT , Quantity INTEGER , ApproxPrice DOUBLE)";
            db.execSQL(CREATE_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int lowerVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
            onCreate(db);
        }

        public boolean insertData(String name , int quantity , Double price){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues content_values = new ContentValues();
            content_values.put(COLUMN_NAME , name);
            content_values.put(COLUMN_QUANTITY , quantity);
            content_values.put(COLUMN_PRICE , price);
            long result = db.insert(TABLE_NAME , null , content_values);
            if (result == -1){
                return false;
            }
            else{
                return true;
            }

        }

        public Cursor getAllData(){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor result = db.rawQuery("SELECT * FROM "+ TABLE_NAME , null);
            return result;
        }

        public Integer removeData(String name){
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME , "ItemName = ?" , new String[] {name} );

        }

        public int size(){
            SQLiteDatabase db = this.getWritableDatabase();
            String count = "SELECT COUNT(*) from " + TABLE_NAME;
            Cursor mcursor = db.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            return icount;
        }

        public Cursor getAllNames(){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor result = db.rawQuery("SELECT ItemName FROM "+ TABLE_NAME , null);
            return result;
        }

        public Cursor getAllIds(){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor result = db.rawQuery("SELECT Number FROM "+ TABLE_NAME , null);
            return result;
        }

    }

