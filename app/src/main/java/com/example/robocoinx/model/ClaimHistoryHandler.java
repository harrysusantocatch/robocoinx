package com.example.robocoinx.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class ClaimHistoryHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "usersdb";
    private static final String TABLE_NAME = "userdetails";
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_CLAIM = "claim";
    private static final String KEY_BALANCE = "balance";

    public ClaimHistoryHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " TEXT,"
                + KEY_CLAIM + " TEXT,"
                + KEY_BALANCE + " TEXT"+ ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insert(String date, String claim, String balance){
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_DATE, date);
        cValues.put(KEY_CLAIM, claim);
        cValues.put(KEY_BALANCE, balance);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME,null, cValues);
        db.close();
        return newRowId;
    }

    public ArrayList<HashMap<String, String>> getClaimHistories(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        String query = "SELECT "+KEY_DATE+", "+KEY_CLAIM+", "+KEY_BALANCE
                +" FROM "+TABLE_NAME+" ORDER BY "+KEY_ID+" DESC";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> claims = new HashMap<>();
            claims.put(KEY_DATE,cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            claims.put(KEY_CLAIM,cursor.getString(cursor.getColumnIndex(KEY_CLAIM)));
            claims.put(KEY_BALANCE,cursor.getString(cursor.getColumnIndex(KEY_BALANCE)));
            result.add(claims);
        }
        return  result;
    }
}
