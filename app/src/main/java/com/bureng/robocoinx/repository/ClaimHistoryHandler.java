package com.bureng.robocoinx.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.bureng.robocoinx.model.db.ClaimHistory;

import java.util.ArrayList;

public class ClaimHistoryHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "db_robocoinx";
    private static final String TABLE_NAME = "claim_history";
    private static final String KEY_ID = "c_id";
    private static final String KEY_DATE = "c_date";
    private static final String KEY_AMOUNT = "c_amount";
    private static final String KEY_BALANCE = "c_balance";
    private static final String KEY_TYPE = "c_type";
    private static final String KEY_NAME = "c_name";
    private static ClaimHistoryHandler instance;
    public static ClaimHistoryHandler getInstance(Context context){
        if(instance == null){
            instance = new ClaimHistoryHandler(context);
        }
        return instance;
    }

    private ClaimHistoryHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " TEXT,"
                + KEY_AMOUNT + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_BALANCE + " TEXT"+ ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insert(String date, String name, String type, String amount, String balance){
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_DATE, date);
        cValues.put(KEY_AMOUNT, amount);
        cValues.put(KEY_BALANCE, balance);
        cValues.put(KEY_TYPE, type);
        cValues.put(KEY_NAME, name);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME,null, cValues);
        db.close();
        return newRowId;
    }

    public ArrayList<ClaimHistory> getClaimHistories(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ClaimHistory> result = new ArrayList<>();
        String query = "SELECT "+KEY_DATE+", "+ KEY_AMOUNT +", "+KEY_BALANCE+", "+KEY_NAME+", "+KEY_TYPE
                +" FROM "+TABLE_NAME+" ORDER BY "+KEY_ID+" DESC";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            ClaimHistory claim = new ClaimHistory();
            claim.date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            claim.claim = cursor.getString(cursor.getColumnIndex(KEY_AMOUNT));
            claim.balance = cursor.getString(cursor.getColumnIndex(KEY_BALANCE));
            claim.type = ClaimHistory.TransactionType.valueOf(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
            claim.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            result.add(claim);
        }
        return  result;
    }
}
