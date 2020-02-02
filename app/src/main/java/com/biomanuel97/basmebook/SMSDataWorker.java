package com.biomanuel97.basmebook;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

class SMSDataWorker {

    static void loadSMSTransactions(Context mContext) {
        Uri inboxUri = Uri.parse("content://sms/inbox");
        ArrayList<String[]> transactionList = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();

        final Cursor cursor = contentResolver.query(inboxUri, null, null, null, null);
        while (cursor.moveToNext()) {

            if (cursor.getString(cursor.getColumnIndexOrThrow("address")).toString().equals("131")) {
                String phoneNum = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
                String SMSBody = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();
                String[] SMSBodySplit = SMSBody.split(" ");

                /*
                 * Typical Transaction Message Format:
                 *      You have successfully transferred 1000MB Data to 2348167781726.
                 */

                if (SMSBodySplit.length == 8) {
                    String firstFourWords = SMSBodySplit[0] + SMSBodySplit[1] + SMSBodySplit[2] + SMSBodySplit[3];
                    String expected = "Youhavesuccessfullytransferred";
                    if (firstFourWords.equals(expected)) {
                        String dataQuantity = SMSBodySplit[4];
                        String phone = SMSBodySplit[7].replaceFirst("234", "0").replace('.',' ').trim();
                        transactionList.add(new String[]{phone, " ", dataQuantity, date});
                    }
                }
            }
        }
        int numOfTrans = transactionList.size();
        for (int i = 1; i <= numOfTrans; i++)
            UtilManager.getInstance().registerTransaction(transactionList.get(numOfTrans - i)[0], transactionList.get(numOfTrans - i)[1],
                    transactionList.get(numOfTrans - i)[2], Long.valueOf(transactionList.get(numOfTrans - i)[3]), true);
        cursor.close();
    }
}
