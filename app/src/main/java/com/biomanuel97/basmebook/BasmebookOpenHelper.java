package com.biomanuel97.basmebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BasmebookOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Basmebook.db";
    public static final int DATABASE_VERSION = 1;

    public BasmebookOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BasmebookDatabaseContract.CustomerEntry.SQL_CREATE_TABLE);
        db.execSQL(BasmebookDatabaseContract.TransactionEntry.SQL_CREATE_TABLE);

        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCustomers();
        worker.insertTransactions();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
