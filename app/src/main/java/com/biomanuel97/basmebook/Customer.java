package com.biomanuel97.basmebook;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Customer {
    private static int transactionCount = 0;
    int index;
    boolean isFavorite = false;
    private String phone;
    private String alias;
    private long dataQuantity;
    private long initTimeStamp;
    private long id;
    private ArrayList<Transaction> mTransactions;

    public Customer(String phone, String alias, long initTimeStamp, long id) {
        this.phone = phone;
        this.alias = alias;
        this.initTimeStamp = initTimeStamp;
        this.id = id;
        dataQuantity = 0;
        this.mTransactions = new ArrayList<Transaction>();
    }

    public Customer(String phone, String alias, long initTimeStamp) {
        this.phone = phone;
        this.alias = alias;
        this.initTimeStamp = initTimeStamp;
        this.mTransactions = new ArrayList<Transaction>();
    }

    public Customer(String phone, String alias){
        this.phone = phone;
        this.alias = alias;
    }

    public Customer(String phone){
        this.phone = phone;
    }

    public static int getTransactionCount() {
        return transactionCount;
    }

    ArrayList<Transaction> getTransactions() {
        return mTransactions;
    }

    void addTransaction(Transaction transaction){
        this.mTransactions.add(transaction);
        this.dataQuantity += transaction.getDataValue();
    }

    void removeTransaction(Transaction transaction){
        for (Transaction t : mTransactions) {
            if(t.getTimeStamp() == transaction.getTimeStamp()) mTransactions.remove(t);
        }
    }

    int getIndex(){
        return UtilManager.getInstance().getCustomerIndex(this);
    }

    public String getIndexS(){
        return String.valueOf(getIndex());
    }

    public String getPhone() {
        return phone;
    }

    public String getAlias() {
        return alias;
    }

    void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMonthAggregate(){
        return UtilManager.getDataQuantityInString(getMonthAggregateValue());
    }

    int getMonthAggregateValue(){
        Calendar currentTime = Calendar.getInstance();
        int dayOfTheMonth = currentTime.get(Calendar.DAY_OF_MONTH);
        long currentTimestamp = (new Date()).getTime();
        long aDay = 24 * 60 * 60 * 1000;
        long aMonth = dayOfTheMonth * aDay;
        int monthlyAgg = 0;
        for (Transaction t : mTransactions) {
            if((currentTimestamp - t.getTimeStamp()) < aMonth){
                monthlyAgg += t.getDataValue();
            }
        }
        return monthlyAgg;
    }

    public int get30daysAggregateValue(){
        long aMonth = 2592000000L;
        long currentTime = (new Date()).getTime();
        int monthlyAgg = 0;
        for (Transaction t : mTransactions) {
            if((currentTime - t.getTimeStamp()) < aMonth){
                monthlyAgg += t.getDataValue();
            }
        }
        return monthlyAgg;
    }

    public String getDataQuantity() {
        if (dataQuantity < 10000) return String.valueOf(dataQuantity);
        else return dataQuantity / 1000 + "GB";
    }

    long getInitTimeStamp() {
        return initTimeStamp;
    }

    public String getInitTime(){
        DateFormat formatter = DateFormat.getDateInstance();
        Date time = new Date(initTimeStamp);
        return formatter.format(time);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdS() {
        return String.valueOf(id);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    void updateTransaction(Transaction transaction){
        for(int i=0; i < mTransactions.size(); i++){
            if(mTransactions.get(i).getTimeStamp() == transaction.getTimeStamp()) mTransactions.set(i, transaction);
        }
    }
}
