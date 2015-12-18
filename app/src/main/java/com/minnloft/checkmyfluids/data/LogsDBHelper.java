package com.minnloft.checkmyfluids.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.minnloft.checkmyfluids.data.LogsContract.OilTable;
import com.minnloft.checkmyfluids.data.LogsContract.CoolantTable;

/**
 * Created by Ryan on 9/27/2015.
 */
public class LogsDBHelper extends SQLiteOpenHelper {

    //Application database name and version
    private static final String DATABASE_NAME = "logs.db";
    private static final int DATABASE_VERSION = 1;

    public LogsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Database creation sql statement for oil table
        final String SQL_CREATE_OIL_TABLE = "CREATE TABLE "
                + OilTable.TABLE_NAME + "( "
                + OilTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OilTable.COLUMN_DATE + " TEXT NOT NULL,"
                + OilTable.COLUMN_AMOUNT + " REAL NOT NULL,"
                + OilTable.COLUMN_MILES + " INTEGER NOT NULL);";

        // Database creation sql statement for coolant table
        final String SQL_CREATE_COOLANT_TABLE = "CREATE TABLE "
                + CoolantTable.TABLE_NAME + "( "
                + CoolantTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CoolantTable.COLUMN_DATE + " TEXT NOT NULL,"
                + CoolantTable.COLUMN_AMOUNT + " REAL NOT NULL,"
                + CoolantTable.COLUMN_MILES + " INTEGER NOT NULL);";

        //Create the tables in the database
        db.execSQL(SQL_CREATE_OIL_TABLE);
        db.execSQL(SQL_CREATE_COOLANT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ OilTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ CoolantTable.TABLE_NAME);
        onCreate(db);
    }

}
