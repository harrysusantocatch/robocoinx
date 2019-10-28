package com.example.robocoinx.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.robocoinx.model.db.Fingerprint;

import java.util.ArrayList;

public class FingerprintHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "db_robocoinx";
    private static final String TABLE_NAME = "fingerprint";
    private static final String KEY_ID = "f_id";
    private static final String KEY_FINGERPRINT1 = "f_fingerprint1";
    private static final String KEY_FINGERPRINT2 = "f_fingerprint2";
    private static FingerprintHandler instance;
    public static FingerprintHandler getInstance(Context context){
        if(instance == null){
            instance = new FingerprintHandler(context);
        }
        return instance;
    }

    private FingerprintHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FINGERPRINT1 + " TEXT,"
                + KEY_FINGERPRINT2 + " TEXT"+ ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insert(Fingerprint f){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_FINGERPRINT1, f.fingerprint1);
        cValues.put(KEY_FINGERPRINT2, f.fingerprint2);
        long newRowId = db.insert(TABLE_NAME,null, cValues);
        db.close();
        return newRowId;
    }

    public ArrayList<Fingerprint> getFingerprint(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Fingerprint> result = new ArrayList<>();
        String query = "SELECT "+ KEY_FINGERPRINT1 +", "+ KEY_FINGERPRINT2
                +" FROM "+TABLE_NAME+" ORDER BY "+KEY_ID+" DESC";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            String fingerprint1 = cursor.getString(cursor.getColumnIndex(KEY_FINGERPRINT1));
            String fingerprint2 = cursor.getString(cursor.getColumnIndex(KEY_FINGERPRINT2));
            Fingerprint f = new Fingerprint(fingerprint1, fingerprint2);
            result.add(f);
        }
        return  result;
    }
}
