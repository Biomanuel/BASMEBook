package com.biomanuel97.basmebook;

import android.provider.BaseColumns;

final class BasmebookDatabaseContract {
    private BasmebookDatabaseContract() {
    }

    static final class TransactionEntry implements BaseColumns {
        static final String TABLE_NAME = "transactions";
        static final String COLUMN_CUSTOMER_ALIAS = "customer_alias";
        static final String COLUMN_PHONE = "phone";
        static final String COLUMN_DATA_QUANTITY = "data_quantity";
        static final String COLUMN_TIMESTAMP = "time_stamp";
        static final String COLUMN_PAID = "paid";
        static final String COLUMN_IS_DELETED = "is_deleted";

        // CREATE TABLE transactions (customer_alias TYPE_INFO, phone TYPE_INFO, data_quantity TYPE_INFO, time_stamp TYPE_INFO, paid TYPE_INFO)
        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_CUSTOMER_ALIAS + " TEXT NOT NULL, " +
                        COLUMN_PHONE + " TEXT NOT NULL, " +
                        COLUMN_DATA_QUANTITY + " TEXT NOT NULL, " +
                        COLUMN_TIMESTAMP + " TEXT NOT NULL, " +
                        COLUMN_PAID + " INTEGER DEFAULT 1, " +
                        COLUMN_IS_DELETED + " INTEGER DEFAULT 0)";
    }

    static final class CustomerEntry implements BaseColumns {
        static final String TABLE_NAME = "customers";
        static final String COLUMN_CUSTOMER_ALIAS = "customer_alias";
        static final String COLUMN_PHONE = "phone";
        static final String COLUMN_TIMESTAMP = "time_stamp";
        static final String COLUMN_IS_FAVORITE = "is_favorite";
        static final String COLUMN_IS_DELETED = "is_deleted";

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_CUSTOMER_ALIAS + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_PHONE + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_TIMESTAMP + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, "+
                        COLUMN_IS_DELETED + " INTEGER DEFAULT 0"+
                        ")";
    }
}
