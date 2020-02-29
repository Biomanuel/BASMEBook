package com.biomanuel97.basmebook;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.biomanuel97.basmebook.BasmebookDatabaseContract.CustomerEntry;
import com.biomanuel97.basmebook.BasmebookDatabaseContract.TransactionEntry;

class DatabaseDataWorker {
    private SQLiteDatabase mDb;
    private UtilManager mManager;

    DatabaseDataWorker(SQLiteDatabase db){
        mManager = UtilManager.getInstance();
        mDb = db;
    }

    private long insertTransaction(String phone, String customerAlias, String dataQuantity, String timeStamp, boolean isPaid){
        ContentValues values = new ContentValues();
        values.put(TransactionEntry.COLUMN_CUSTOMER_ALIAS, customerAlias);
        values.put(TransactionEntry.COLUMN_PHONE, phone);
        values.put(TransactionEntry.COLUMN_DATA_QUANTITY, dataQuantity);
        values.put(TransactionEntry.COLUMN_TIMESTAMP, timeStamp);
        values.put(TransactionEntry.COLUMN_PAID, (isPaid) ? 1: 0);

        return mDb.insert(TransactionEntry.TABLE_NAME, null, values);
    }

    long insertTransaction(Transaction t){
        return insertTransaction(t.getPhone(), t.getCustomerAlias(), t.getDataQuantity(), String.valueOf(t.getTimeStamp()), t.isPaid());
    }

    private long insertCustomer(String phone, String customerAlias, String timeStamp){
        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_PHONE, phone);
        values.put(CustomerEntry.COLUMN_CUSTOMER_ALIAS, customerAlias);
        values.put(CustomerEntry.COLUMN_TIMESTAMP, timeStamp);

        return mDb.insert(CustomerEntry.TABLE_NAME, null, values);
    }

    long insertCustomer(Customer c){
        return insertCustomer(c.getPhone(), c.getAlias(), String.valueOf(c.getInitTimeStamp()));
    }

    public void insertTransactions(){
        int len = mManager.getAllTransactions().size();
        for(int i=0;i<len;i++){
            Transaction t = mManager.getAllTransactions().get(i);
            insertTransaction(t.getPhone(), t.getCustomerAlias(), t.getDataQuantity(), String.valueOf(t.getTimeStamp()), t.isPaid());
        }
    }

    void insertCustomers(){
        int len = mManager.getCustomers().size();
        for(int i = 0; i<len; i++){
            Customer c = mManager.getCustomers().get(i);
            c.setId(insertCustomer(c.getPhone(), c.getAlias(), String.valueOf(c.getInitTimeStamp())));
        }
    }

    void loadCustomersFromDatabase(SQLiteDatabase db){
        String selection = CustomerEntry.COLUMN_IS_DELETED + " = ?";
        String[] selectionArgs = {Integer.toString(0)};

        final String[] customersColumns = {
                CustomerEntry._ID,
                CustomerEntry.COLUMN_CUSTOMER_ALIAS,
                CustomerEntry.COLUMN_PHONE,
                CustomerEntry.COLUMN_TIMESTAMP
        };
        Cursor customerCursor = db.query(CustomerEntry.TABLE_NAME, customersColumns,
                selection, selectionArgs, null, null, CustomerEntry.COLUMN_TIMESTAMP);

        int idPos = customerCursor.getColumnIndex(CustomerEntry._ID);
        int phonePos = customerCursor.getColumnIndex(CustomerEntry.COLUMN_PHONE);
        int customerAliasPos = customerCursor.getColumnIndex(CustomerEntry.COLUMN_CUSTOMER_ALIAS);
        int timestampPos = customerCursor.getColumnIndex(CustomerEntry.COLUMN_TIMESTAMP);

        while (customerCursor.moveToNext()) {
            long id = customerCursor.getLong(idPos);
            String phone = customerCursor.getString(phonePos);
            String customerAlias = customerCursor.getString(customerAliasPos);
            long timestamp = Long.valueOf(customerCursor.getString(timestampPos));

            mManager.newCustomer(phone, customerAlias, timestamp, id);
        }

        customerCursor.close();
    }

    void loadTransactionsFromDatabase(SQLiteDatabase db){
        String selection = TransactionEntry.COLUMN_IS_DELETED + " = ?";
        String[] selectionArgs = {Integer.toString(0)};

        final String[] transactionsColumns = {
                TransactionEntry._ID,
                TransactionEntry.COLUMN_CUSTOMER_ALIAS,
                TransactionEntry.COLUMN_PHONE,
                TransactionEntry.COLUMN_DATA_QUANTITY,
                TransactionEntry.COLUMN_TIMESTAMP,
                TransactionEntry.COLUMN_PAID
        };
        Cursor transactionCursor = db.query(TransactionEntry.TABLE_NAME, transactionsColumns,
                selection, selectionArgs, null, null, TransactionEntry.COLUMN_TIMESTAMP);

        int idPos = transactionCursor.getColumnIndex(TransactionEntry._ID);
        int phonePos = transactionCursor.getColumnIndex(TransactionEntry.COLUMN_PHONE);
        int customerAliasPos = transactionCursor.getColumnIndex(TransactionEntry.COLUMN_CUSTOMER_ALIAS);
        int dataQuantityPos = transactionCursor.getColumnIndex(TransactionEntry.COLUMN_DATA_QUANTITY);
        int timestampPos = transactionCursor.getColumnIndex(TransactionEntry.COLUMN_TIMESTAMP);
        int paidPos = transactionCursor.getColumnIndex(TransactionEntry.COLUMN_PAID);

        while(transactionCursor.moveToNext()){
            long id = transactionCursor.getLong(idPos);
            String phone = transactionCursor.getString(phonePos);
            String customerAlias = transactionCursor.getString(customerAliasPos);
            String dataQuantity = transactionCursor.getString(dataQuantityPos);
            long timestamp = Long.valueOf(transactionCursor.getString(timestampPos));
            boolean paid = transactionCursor.getInt(paidPos) == 1;

            mManager.registerTransaction(phone, customerAlias, dataQuantity, timestamp, paid, id);
        }
        transactionCursor.close();
    }
    
    void deleteTransactionFromDatabase(SQLiteDatabase db, Transaction transaction){
        String selection = TransactionEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(transaction.id)};

        ContentValues values = new ContentValues();
        values.put(TransactionEntry.COLUMN_IS_DELETED, 1);

        db.update(TransactionEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void deleteCustomerFromDatabase(SQLiteDatabase db, Customer customer){
        String selection = CustomerEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(customer.getId())};

        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_IS_DELETED, 1);

        db.update(CustomerEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void starCustomerInDatabase(SQLiteDatabase db, Customer customer){
        String selection = CustomerEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(customer.getId())};

        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_IS_FAVORITE, 1);

        db.update(CustomerEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void unStarCustomerInDatabase(SQLiteDatabase db, Customer customer){
        String selection = CustomerEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(customer.getId())};

        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_IS_FAVORITE, 0);

        db.update(CustomerEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void editTransactionInDatabase(SQLiteDatabase db, Transaction transaction){
        String selection = TransactionEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(transaction.id)};

        ContentValues values = new ContentValues();
        values.put(TransactionEntry.COLUMN_CUSTOMER_ALIAS, transaction.getCustomerAlias());
        values.put(TransactionEntry.COLUMN_PHONE, transaction.getPhone());
        values.put(TransactionEntry.COLUMN_DATA_QUANTITY, transaction.getDataQuantity());
        values.put(TransactionEntry.COLUMN_PAID, (transaction.isPaid()) ? 1: 0);

        db.update(TransactionEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void editCustomerInDatabase(SQLiteDatabase db, Customer customer){
        String selection = CustomerEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(customer.getId())};

        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_PHONE, customer.getPhone());
        values.put(CustomerEntry.COLUMN_CUSTOMER_ALIAS, customer.getAlias());
        values.put(CustomerEntry.COLUMN_TIMESTAMP, String.valueOf(customer.getInitTimeStamp()));

        db.update(CustomerEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    void updateTransactionInDatabase(SQLiteDatabase db, Transaction transaction){
        String selection = TransactionEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(transaction.id)};

        ContentValues values = new ContentValues();
        values.put(TransactionEntry.COLUMN_PAID, 1);

        db.update(TransactionEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void updateCustomerInDatabase(SQLiteDatabase db, Customer customer){
        String selection = CustomerEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(customer.getId())};

        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_IS_DELETED, 1);

        db.update(CustomerEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
