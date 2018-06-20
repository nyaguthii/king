package org.gilbre.app.gilbre;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by nyaguthii on 11/25/17.
 */

public class ChaniaDBHelper extends SQLiteOpenHelper {

    public ChaniaDBHelper(Context context) {
        super(context, ChaniaContract.DB_NAME,null,ChaniaContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ChaniaContract.SQL_CREATE_ENTRIES);
        db.execSQL(ChaniaContract.SQL_CREATE_TYPES);
        db.execSQL(ChaniaContract.SQL_CREATE_CUSTOMERS);
        db.execSQL(ChaniaContract.SQL_CREATE_VEHICLES);
        db.execSQL(ChaniaContract.SQL_CREATE_PLACES);
        db.execSQL(ChaniaContract.SQL_CREATE_PARCELS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        db.execSQL(ChaniaContract.SQL_DELETE_ENTRIES);
        db.execSQL(ChaniaContract.SQL_DELETE_TYPES);
        db.execSQL(ChaniaContract.SQL_DELETE_CUSTOMERS);
        db.execSQL(ChaniaContract.SQL_DELETE_VEHICLES);
        db.execSQL(ChaniaContract.SQL_DELETE_PLACES);
        db.execSQL(ChaniaContract.SQL_DELETE_PARCELS);
        onCreate(db);
    }
}
