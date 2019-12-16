package com.example.newweek3;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager; // unused packages here.


import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by e.lavieri on 8/4/2017.
 * For SNHU: COCE.
 * Target: CS360 Module Three.
 */
public class CampDBHandler extends SQLiteOpenHelper{
    // database name and version
    private static final int DB_VER = 1;
    private static final String DB_NAME = "campDB.db";
    // table
    public static final String TABLE_CAMP = "camps";
    public static final String TABLE_DETAIL = "detail";
    // columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STATE = "STATE";
    public static final String COLUMN_FAV = "FAV";

    public static final String COLUMN_D_ID = "id"; //this is for another database.
    public static final String COLUMN_D_Price = "PRICE";
    public static final String COLUMN_D_Address = "Address";
    public static final String COLUMN_D_Rating = "Rating";
    public static final String COLUMN_D_Desc = "Description"; //this is never used.
    // constructor
    public CampDBHandler(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DB_NAME, factory, DB_VER);
    }
    // This method creates the Camps table when the DB is initialized.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CAMPSITE_TABLE = "CREATE TABLE " +
                TABLE_CAMP + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_STATE + " TEXT," +
                COLUMN_FAV + " INTEGER" +

                ")";
        db.execSQL(CREATE_CAMPSITE_TABLE);

        String CREATE_DETAIL_TABLE = "CREATE TABLE " +
                TABLE_DETAIL + "(" +
                COLUMN_D_ID + " INTEGER PRIMARY KEY," +
                COLUMN_D_Price + " TEXT," +
                COLUMN_D_Address + " TEXT," +
                COLUMN_D_Rating + " INTEGER" +

                ")";
        db.execSQL(CREATE_DETAIL_TABLE);
    }
    // This method closes an open DB if a new one is created.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL);
        onCreate(db);
    }
    // This method is used to add a Camp record to the database.
    public void addCamp(Camp camp) {
        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();
        values.put(COLUMN_NAME, camp.getName());
        values.put(COLUMN_STATE, camp.getState());
        values.put(COLUMN_FAV, camp.getFav());
        values2.put(COLUMN_D_Price, camp.getPrice());
        values2.put(COLUMN_D_Address, camp.getAddress());
        values2.put(COLUMN_D_Rating, camp.getRating());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CAMP, null, values);
        db.insert(TABLE_DETAIL, null, values2);
        db.close();
    }
    // implements the search/find functionality
    public Camp searchCamp(String campName) {
        String query = "SELECT * FROM " +
                TABLE_CAMP + " WHERE " + COLUMN_NAME +
                " = \"" + campName + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Camp camp = new Camp();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            camp.setID(Integer.parseInt(cursor.getString(0)));
            camp.setName(cursor.getString(1));
            camp.setState(cursor.getString(2));
            camp.setFav(Integer.parseInt(cursor.getString(3)));

            String ids= (cursor.getString(0));
            cursor.close();
            String query2 = "SELECT * FROM " +
                    TABLE_DETAIL + " WHERE " + COLUMN_D_ID +
                    " = " + ids;
            //SQLiteDatabase dbs = this.getWritableDatabase();
            // so there are total of 2 database tables. One is for basic campsite and user information.
            //second one is for campsite's details. I think it's not efficient to have two tables. I might consider
            //combining them together.
            Cursor cursor2 = db.rawQuery(query2, null);
            if (cursor2.moveToFirst()) {
                cursor2.moveToFirst();
                String pricev = cursor2.getString(1);

                camp.setPrice(cursor2.getString(1));
                camp.setAddress(cursor2.getString(2));
                camp.setRating(Integer.parseInt(cursor2.getString(3)));
                cursor2.close();
            }
        } else {
            camp = null;
        }
        db.close();
        return camp;
    }
    // implements the delete camp functionality
    public boolean deleteCamp(String campName) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_CAMP +
                " WHERE " + COLUMN_NAME + " = \"" + campName + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Camp camp = new Camp();
        if (cursor.moveToFirst()) {
            camp.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_CAMP, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(camp.getID())});

            String ids= String.valueOf(camp.getID());
            cursor.close();
            result = true;

            //since there are two tables, you have to make two separate queries. I don't think this is efficient.
            String query2 = "SELECT * FROM " + TABLE_DETAIL +
                    " WHERE " + COLUMN_D_ID + " = " + ids;
            Cursor cursor2 = db.rawQuery(query2, null);

            if (cursor2.moveToFirst()) {
                db.delete(TABLE_DETAIL, COLUMN_D_ID + " = ?",
                        new String[] { ids});
                cursor2.close();
            }

        }
        db.close();
        return result;
    }

}
