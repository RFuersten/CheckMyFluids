package com.minnloft.checkmyfluids.data;

import android.provider.BaseColumns;

/**
 * Created by Ryan on 9/27/2015.
 */
public class LogsContract {

    public static final class OilTable implements BaseColumns {

        public static final String TAG = "OT";

        //Table column names for the oil table
        public static final String TABLE_NAME = "oil";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_MILES = "miles";
    }

    public static final class CoolantTable implements BaseColumns {

        public static final String TAG = "CT";

        //Table column names for the coolant table
        public static final String TABLE_NAME = "coolant";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_MILES = "miles";
    }
}
