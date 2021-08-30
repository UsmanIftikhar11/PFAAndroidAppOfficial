package com.pfa.pfaapp.dbutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbHelper extends SQLiteAssetHelper {

    @SuppressLint("StaticFieldLeak")
    private volatile static DbHelper instance;
    private static final String DATABASE_NAME = "pfadb.sqlite";
    private static final int DATABASE_VERSION = 3;

    SQLiteDatabase dataBase;

    @SuppressLint("LongLogTag")
    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dataBase = getWritableDatabase();
        Log.e("Max size of db can grow to: ", "" + dataBase.getMaximumSize());
    }

    /*
     * Singleton Object for DBHelper
     */
    public static DbHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DbHelper(ctx);
        }
        return instance;
    }
}
