package com.example.hackathon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class USSDDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ussd_transactions.db";
    private static final int DATABASE_VERSION = 7;

    private static final String SPLIT_TABLE = "split_transactions";
    private static final String SPLIT_ID = "_id";
    private static final String SPLIT_TOTAL_AMOUNT = "total_amount";
    private static final String SPLIT_AMOUNT = "split_amount";
    private static final String SPLIT_FRIEND_NAME = "friend_name";
    private static final String SPLIT_FRIEND_PHONE = "friend_phone";
    private static final String SPLIT_STATUS = "status"; // "Pending" or "Paid"

    // General Transactions Table
    private static final String TRANSACTIONS_TABLE = "transactions";
    private static final String TRANSACTION_ID = "_id";
    private static final String TRANSACTION_AMOUNT = "amount";
    private static final String TRANSACTION_SENDER_P = "sender";

    private static final String DESCRIPTION="description";
    private static final String TRANSACTION_RECEIVER_P = "receiver";
    private static final String TRANSACTION_DATE = "date";

    public USSDDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getAllSplitTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + SPLIT_TABLE, null);
    }
//    public Cursor getAllTransactions() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE, null);
//    }
public Cursor getAllTransactions() {
    SQLiteDatabase db = this.getReadableDatabase();
//    Cursor cursor = db.rawQuery("SELECT * FROM "+TRANSACTIONS_TABLE, null);
    return db.rawQuery("SELECT _id, amount, sender, receiver, description, date FROM transactions", null);

//    Log.d("DB Query", "Total rows in transactions: " + cursor.getCount());
//
//    return cursor;
}



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SPLIT_TABLE = "CREATE TABLE IF NOT EXISTS " + SPLIT_TABLE + " (" +
                SPLIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SPLIT_TOTAL_AMOUNT + " REAL, " +
                SPLIT_AMOUNT + " REAL, " +
                SPLIT_FRIEND_NAME + " TEXT, " +
                SPLIT_FRIEND_PHONE + " TEXT, " +
                SPLIT_STATUS + " TEXT DEFAULT 'Pending')";



        String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE + " (" +
                TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TRANSACTION_AMOUNT + " REAL, " +
                TRANSACTION_SENDER_P + " TEXT, " +
                TRANSACTION_RECEIVER_P + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                TRANSACTION_DATE + " TEXT)";
        db.execSQL(CREATE_SPLIT_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion < 2) {
//            // Version 2 introduced the description column
//            db.execSQL("ALTER TABLE " + TRANSACTIONS_TABLE + " ADD COLUMN " + DESCRIPTION + " TEXT;");
//        }
//
//        if (oldVersion < 3) {
//            // Future changes: Add more columns or tables
//        }

        db.execSQL("DROP TABLE IF EXISTS transactions");
        // Recreate table with new schema
        onCreate(db);
    }

    public void deleteTransaction(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TRANSACTIONS_TABLE, TRANSACTION_ID + "=?", new String[]{_id});
        db.close();
    }

    public List<Transaction> getAllTransits() {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TRANSACTIONS_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getString(0),
                        cursor.getDouble(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactionList;
    }

    public void insertSplitTransaction(double totalAmount, double splitAmount, String friendName, String friendPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SPLIT_TOTAL_AMOUNT, totalAmount);
        values.put(SPLIT_AMOUNT, splitAmount);
        values.put(SPLIT_FRIEND_NAME, friendName);
        values.put(SPLIT_FRIEND_PHONE, friendPhone);
        values.put(SPLIT_STATUS, "Pending");

        db.insert(SPLIT_TABLE, null, values);
        db.close();
    }




//    public void insertUSSDResponse(String response) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("response", response);  // Make sure the column exists in the table
//
//        db.insert("ussd_responses", null, values);
//        db.close();
//
//
//    }
}
