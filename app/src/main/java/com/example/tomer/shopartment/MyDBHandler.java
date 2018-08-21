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
    public static final String COLUMN_NAME = "ItemName";
    public static final String COLUMN_QUANTITY = "Quantity";
    public static final String COLUMN_PRICE = "ApproxPrice";
    public static final String COLUMN_CATEGORY = "Category";

    //initialize the database
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( Number INTEGER PRIMARY KEY AUTOINCREMENT , ItemName TEXT , Quantity INTEGER , ApproxPrice DOUBLE , Category TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int lowerVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, int quantity, Double price, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content_values = new ContentValues();
        content_values.put(COLUMN_NAME, name);
        content_values.put(COLUMN_QUANTITY, quantity);
        content_values.put(COLUMN_PRICE, price);
        content_values.put(COLUMN_CATEGORY, category);
        long result = db.insert(TABLE_NAME, null, content_values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return result;
    }

    public Integer removeData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ItemName = ?", new String[]{name});

    }

    public void updateData(String num, String name, int quantity, Double price, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_POS, num);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_QUANTITY, quantity);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_CATEGORY, category);
        db.update(TABLE_NAME, contentValues, "Number = ?", new String[]{num});
    }

    public int size() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor mCursor = db.rawQuery(count, null);
        mCursor.moveToFirst();
        int icount = mCursor.getInt(0);
        mCursor.close();
        return icount;
    }

    public Cursor getAllNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT ItemName FROM " + TABLE_NAME, null);
        return result;
    }

    public Cursor getAllIds() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT Number FROM " + TABLE_NAME, null);
        return result;
    }

    public String getIdByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        long num = 0;
        String rec = null;
        Cursor mCursor = db.rawQuery(
                "SELECT Number FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = '" + name + "'", null);
        if (mCursor != null && mCursor.getCount() != 0) {

            mCursor.moveToFirst();
            num = mCursor.getLong(0);
            rec = String.valueOf(num);
        }

        return rec;
    }

    public String getCategoryByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        long num = 0;
        String rec = null;
        Cursor mCursor = db.rawQuery(
                "SELECT Category FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = '" + name + "'", null);
        if (mCursor != null && mCursor.getCount() != 0) {

            mCursor.moveToFirst();
            num = mCursor.getLong(0);
            rec = String.valueOf(num);
        }

        return rec;
    }

    public String[] columnToStrings(String name) { // returns all values in same row of string name
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.rawQuery(
                "SELECT * FROM "+ TABLE_NAME + " WHERE ItemName='"  + name + "' LIMIT 1", null);
        if (mCursor != null) {
            int numOfCols = mCursor.getColumnCount();
            if (numOfCols == 0) return null;
            String[] strings = new String[numOfCols];
            mCursor.moveToFirst();
            for(int j = 0; j < numOfCols; j++) {
                strings[j] = mCursor.getString(j);

            }
            mCursor.close();
            return strings;

        }
        mCursor.close();
        return null;
    }

    public double getTotalPrice(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor prices = db.rawQuery(
                "SELECT ApproxPrice FROM "+ TABLE_NAME , null);
        Cursor quntities = db.rawQuery(
                "SELECT Quantity FROM "+ TABLE_NAME , null);
        if (prices != null && quntities != null) {
            prices.moveToFirst();
            quntities.moveToFirst();
            double count = Double.parseDouble(prices.getString(0))*Integer.parseInt(quntities.getString(0));
            while(prices.moveToNext() && quntities.moveToNext()){
                count += (Double.parseDouble(prices.getString(0))*Integer.parseInt(quntities.getString(0)));
            }
            prices.close();
            quntities.close();
            return count;
        }
        else{
            prices.close();
            quntities.close();
            return 0;
        }
    }
}

