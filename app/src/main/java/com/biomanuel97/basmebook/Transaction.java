package com.biomanuel97.basmebook;

import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.cardview.widget.CardView;
import androidx.databinding.BindingAdapter;

import java.util.Date;

public class Transaction {

    private static int count = 0;
    String phone;
    long id;
    private String customerAlias;
    private String dataQuantity;
    private long timeStamp;
    private int index;
    private int cardColorRes;
    private Style mStyle = new Style();
    private Customer mCustomer;
    private boolean paid = false;
    private boolean notPaid = true;
    public Transaction(String phone, String customerAlias, String dataQuantity, long timeStamp, long id) {
        count++;
        this.phone = phone;
        this.customerAlias = customerAlias;
        this.dataQuantity = dataQuantity;
        this.timeStamp = timeStamp;
        this.id = id;
        this.notPaid = !paid;
        this.cardColorRes = R.color.unpaid_transaction_card_color;
    }

    public Transaction(String phone, String customerAlias, String dataQuantity, long timeStamp) {
        count++;
        this.phone = phone;
        this.customerAlias = customerAlias;
        this.dataQuantity = dataQuantity;
        this.timeStamp = timeStamp;
        this.id = count;
        this.notPaid = !paid;
        this.cardColorRes = R.color.unpaid_transaction_card_color;
    }

    public static int getCount() {
        return count;
    }

    @BindingAdapter({"android:color"})
    public static void setColor(View view, Transaction t) {
        if(view instanceof CardView) ((CardView) view).setBackgroundColor(view.getContext().getColor(t.mStyle.cardColorRes));
        if(view instanceof TextView){
            switch(view.getId()){
                case R.id.phone_number:
                    ((TextView) view).setTextColor(view.getContext().getColor(t.mStyle.phoneColorRes));
                    break;
                case R.id.customer_alias:
                    ((TextView) view).setTextColor(view.getContext().getColor(t.mStyle.aliasColorRes));
                    break;
            }
        }
    }


    //region SettersAndGetters

    public String getTime() {
        long currentTime = (new Date()).getTime();
        long timeDifference = currentTime - this.timeStamp;
        long aDay = 86400000;
        long aWeek = aDay * 7;
        long aMonth = aDay * 30;
        long aYear = aWeek * 52;

        String time = (new Date(this.timeStamp)).toString();
        String[] timeSplit = time.split(" ");
        if(timeDifference < aDay) {
            time = timeSplit[3];
        }
        else if(timeDifference < aWeek && timeDifference > aDay){
            time = String.valueOf(timeSplit[3]).substring(0,5) + ", " + timeSplit[0];
        }else if(timeDifference < aMonth && timeDifference > aWeek){
            time = timeSplit[0] + ", " + timeSplit[1]+ " " + timeSplit[2];
        }else if(timeDifference > aMonth && timeDifference < aYear){
            time = timeSplit[2] + ", " + timeSplit[1];
        }else if(timeDifference > aYear){
            time = timeSplit[1] + " " + timeSplit[2]+ ", " + timeSplit[5];
        }
        return time;
    }

    public Customer getCustomer() {
        return mCustomer;
    }

    void setCustomer(Customer customer){
        this.mCustomer = customer;
        this.customerAlias = customer.getAlias();
        this.mCustomer.addTransaction(this);
    }

    public String getCustomerAlias() {
        return this.customerAlias;
    }

    void setCustomerAlias(String customerAlias) {
        this.customerAlias = customerAlias;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDataQuantity() {
        return dataQuantity;
    }

    void setDataQuantity(String dataQuantity) {
        this.dataQuantity = dataQuantity;
    }

    int getDataValue(){
        switch (dataQuantity){
            case "1000MB": return 1000;
            case "2000MB": return 2000;
            case "3000MB": return 3000;
            case "4000MB": return 4000;
            case "5000MB": return 5000;
            default: return 0;
        }
    }

    long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
        if(paid) this.mStyle.setCardColorRes(R.color.paid_transaction_card_color);
        if(!paid) this.mStyle.setCardColorRes(R.color.unpaid_transaction_card_color);
        this.notPaid = !paid;
    }

    boolean getNotPaid(){
        return notPaid;
    }

    int getIndex(){
        return UtilManager.getInstance().getTransactionIndex(this);
    }

    void setIndex(int index){
        this.index = index;
    }

    public String getIndexS(){
        return String.valueOf(getIndex());
    }
    //endregion

    public enum dataQuantity{
        ONE_GB, TWO_GB, THREE_GB, FOUR_GB, FIVE_GB
    }

    private class Style{
        int cardColorRes;
        int phoneColorRes;
        int aliasColorRes;

        Style(){
            this.cardColorRes = R.color.unpaid_transaction_card_color;
            this.phoneColorRes = R.color.default_transaction_phone_text_color;
            this.aliasColorRes = R.color.default_transaction_alias_text_color;
        }

        public int getCardColorRes() {
            return cardColorRes;
        }

        void setCardColorRes(int card_color) {
            this.cardColorRes = card_color;
        }
    }
}
