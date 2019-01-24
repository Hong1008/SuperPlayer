package com.example.zxcbn.superplayer;

import android.app.VoiceInteractor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zxcbn on 2018-04-19.
 */

public class OptionDB extends SQLiteOpenHelper{


    public OptionDB(Context context){
        super(context,"OptionDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE OptionDB ( mid PRIMARY KEY, level0 INTEGER," +
                " level1 INTEGER, level2 INTEGER, level3 INTEGER, level4 INTEGER, mname CHAR(50));");
        Log.i("dbok","ok");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS OptionDB");
        onCreate(db);
    }

}
