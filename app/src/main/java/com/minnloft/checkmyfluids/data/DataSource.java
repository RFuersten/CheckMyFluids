package com.minnloft.checkmyfluids.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.minnloft.checkmyfluids.data.LogsContract.CoolantTable;
import com.minnloft.checkmyfluids.data.LogsContract.OilTable;

import java.util.ArrayList;

/**
 * Created by Ryan on 9/27/2015.
 */
public class DataSource {

   // public static DataSource dataSource;
    private SQLiteDatabase mSQLiteDatabase;
    private LogsDBHelper mLogsDBHelper;

    //String of column names for the oil sqlite database.
    private String[] mAllOilColumns = {
            OilTable.COLUMN_ID,
            OilTable.COLUMN_DATE,
            OilTable.COLUMN_AMOUNT,
            OilTable.COLUMN_MILES};

    //String of column names for the coolant sqlite database.
    private String[] mAllCoolantColumns = {
            OilTable.COLUMN_ID,
            OilTable.COLUMN_DATE,
            OilTable.COLUMN_AMOUNT,
            OilTable.COLUMN_MILES};

    public DataSource(Context context) {
        mLogsDBHelper = new LogsDBHelper(context);
    }

    public void open() throws SQLiteException {
        mSQLiteDatabase = mLogsDBHelper.getWritableDatabase();
    }

    public void close() {
        mLogsDBHelper.close();
    }


    //Method takes a string and depending on the string creates a query from the correct
    //database table and returns the amount of items in the table
    public int getLength(String tag){
        Cursor cursor;

        switch (tag) {
            case OilTable.TAG:
                cursor = mSQLiteDatabase.query(OilTable.TABLE_NAME,
                        mAllOilColumns, null, null, null, null, null);
                return cursor.getCount();
            case CoolantTable.TAG:
                cursor = mSQLiteDatabase.query(CoolantTable.TABLE_NAME,
                        mAllCoolantColumns, null, null, null, null, null);
                return cursor.getCount();
        }
        return 0;
    }

    //Inserts a log into the correct sqlite table with the passed values
    public void insert(String date, String amount, String miles, String tag) {
        ContentValues values = new ContentValues();

         switch (tag) {
            case OilTable.TAG:
                values.put(OilTable.COLUMN_DATE, date);
                values.put(OilTable.COLUMN_AMOUNT, amount);
                values.put(OilTable.COLUMN_MILES, miles);
                mSQLiteDatabase.insert(OilTable.TABLE_NAME, null, values);
                break;
            case CoolantTable.TAG:
                values.put(CoolantTable.COLUMN_DATE, date);
                values.put(CoolantTable.COLUMN_AMOUNT, amount);
                values.put(CoolantTable.COLUMN_MILES, miles);
                mSQLiteDatabase.insert(CoolantTable.TABLE_NAME, null, values);
                break;

        }
    }

    //Edits/Updates a log into the correct sqlite table with the passed values
    public void edit(Long id, String date, String amount, String miles, String tag){
        ContentValues values = new ContentValues();

        switch (tag) {
            case OilTable.TAG:
                values.put(OilTable.COLUMN_DATE, date);
                values.put(OilTable.COLUMN_AMOUNT, amount);
                values.put(OilTable.COLUMN_MILES, miles);
                mSQLiteDatabase.update(OilTable.TABLE_NAME, values, "_id=" + id, null);
                break;
            case CoolantTable.TAG:
                values.put(CoolantTable.COLUMN_DATE, date);
                values.put(CoolantTable.COLUMN_AMOUNT, amount);
                values.put(CoolantTable.COLUMN_MILES, miles);
                mSQLiteDatabase.update(CoolantTable.TABLE_NAME, values, "_id=" + id, null);
                break;
        }
    }

    //Deletes a log into the correct sqlite table based on the log id.
    public void delete(Log log, String tag) {
        long id = log.getmId();

        switch(tag) {
            case OilTable.TAG:
                mSQLiteDatabase.delete(OilTable.TABLE_NAME,
                        OilTable.COLUMN_ID + " = " + id, null);
                break;
            case CoolantTable.TAG:
                mSQLiteDatabase.delete(CoolantTable.TABLE_NAME,
                        CoolantTable.COLUMN_ID + " = " + id, null);
                break;
        }
    }

    //Returns an Arraylist of all of the logs from the table specified from the passed in value.
    public ArrayList getAllLogs(String tag){

        ArrayList logs = new ArrayList();
        Cursor cursor;
        Log log;

        switch(tag) {
            case OilTable.TAG:
                cursor = mSQLiteDatabase.query(OilTable.TABLE_NAME,
                    mAllOilColumns, null, null, null, null, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    log = cursorToLog(cursor);
                    logs.add(log);
                    cursor.moveToNext();
                }
                cursor.close();
                break;
            case CoolantTable.TAG:
                cursor = mSQLiteDatabase.query(CoolantTable.TABLE_NAME,
                        mAllCoolantColumns, null, null, null, null, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    log = cursorToLog(cursor);
                    logs.add(log);
                    cursor.moveToNext();
                }
                cursor.close();
                break;
        }

        return logs;
    }

    //Used in local method getAllLogs()
    private Log cursorToLog(Cursor cursor) {
        Log log = new Log();
        log.setmId(cursor.getLong(0));
        log.setmDate(cursor.getString(1));
        log.setmAmount(cursor.getString(2));
        log.setmMiles(cursor.getString(3));
        return log;
    }


    //Used to get all of the dates from the specified database in the call.
    public String[] getAllDates(String tag){
        Cursor cursor;
        int index;
        String[] dates = new String[0];
        int i = 0;

        switch(tag) {
            case OilTable.TAG:
                cursor = mSQLiteDatabase.query(OilTable.TABLE_NAME, mAllOilColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(OilTable.COLUMN_DATE);
                dates = new String[cursor.getCount()];
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    dates[i] = cursor.getString(index);
                    i++;
                    cursor.moveToNext();
                }

                cursor.close();
                break;
            case CoolantTable.TAG:
                cursor = mSQLiteDatabase.query(CoolantTable.TABLE_NAME, mAllCoolantColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(CoolantTable.COLUMN_DATE);
                dates = new String[cursor.getCount()];
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    dates[i] = cursor.getString(index);
                    i++;
                    cursor.moveToNext();
                }

                cursor.close();
                break;
        }
        return dates;
    }

    //Used to get all of the amounts from the specified database in the call.
    public String[] getAllAmounts(String tag){
        Cursor cursor;
        int index;
        String[] amounts = new String[0];
        int i = 0;

        switch(tag) {
            case OilTable.TAG:
                cursor = mSQLiteDatabase.query(OilTable.TABLE_NAME, mAllOilColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(OilTable.COLUMN_AMOUNT);
                amounts = new String[cursor.getCount()];
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    amounts[i] = cursor.getString(index);
                    i++;
                    cursor.moveToNext();
                }

                cursor.close();
                break;
            case CoolantTable.TAG:
                cursor = mSQLiteDatabase.query(CoolantTable.TABLE_NAME, mAllCoolantColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(CoolantTable.COLUMN_AMOUNT);
                amounts = new String[cursor.getCount()];
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    amounts[i] = cursor.getString(index);
                    i++;
                    cursor.moveToNext();
                }

                cursor.close();
                break;
        }

        return amounts;
    }

    //Used to get all of the miles from the specified database in the call.
    public String[] getAllMiles(String tag){
        Cursor cursor;
        int index;
        String[] miles = new String[0];
        int i = 0;

        switch(tag) {
            case OilTable.TAG:
                cursor = mSQLiteDatabase.query(OilTable.TABLE_NAME, mAllOilColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(OilTable.COLUMN_MILES);
                miles = new String[cursor.getCount()];
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    miles[i] = cursor.getString(index);
                    i++;
                    cursor.moveToNext();
                }

                cursor.close();
                break;
            case CoolantTable.TAG:
                cursor = mSQLiteDatabase.query(CoolantTable.TABLE_NAME, mAllCoolantColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(CoolantTable.COLUMN_MILES);
                miles = new String[cursor.getCount()];
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    miles[i] = cursor.getString(index);
                    i++;
                    cursor.moveToNext();
                }

                cursor.close();
                break;
        }

        return miles;
    }

    public long getLastLogID(String tag){
        Cursor cursor;
        int index;
        long lastLogID = -1;
        int i = 0;

        switch(tag) {
            case OilTable.TAG:
                cursor = mSQLiteDatabase.query(OilTable.TABLE_NAME, mAllOilColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(OilTable.COLUMN_ID);
                cursor.moveToLast();

                lastLogID = cursor.getLong(index);
                cursor.close();
                break;
            case CoolantTable.TAG:
                cursor = mSQLiteDatabase.query(CoolantTable.TABLE_NAME, mAllCoolantColumns, null, null, null, null, null);
                index = cursor.getColumnIndex(CoolantTable.COLUMN_ID);
                cursor.moveToLast();

                lastLogID = cursor.getLong(index);
                cursor.close();
                break;
        }

        return lastLogID;
    }
}