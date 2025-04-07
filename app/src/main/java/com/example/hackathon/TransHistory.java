package com.example.hackathon;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TransHistory extends AppCompatActivity {
    private ListView listView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private USSDDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_listview);  // ✅ Set correct XML layout
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        dbHelper = new USSDDatabaseHelper(this);
        listView = findViewById(R.id.transactionListView); // ✅ Initialize ListView here

        loadTransactionHistory();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // or finish();
        return true;
    }


    private void loadTransactionHistory() {
        Log.d("how","are you");
        Cursor cursor = dbHelper.getAllTransactions();
        if (cursor != null && cursor.getCount() > 0) {
            transactionAdapter = new TransactionAdapter(this, cursor);
            listView.setAdapter(transactionAdapter);
        } else {
            Toast.makeText(this, "No transaction history found.", Toast.LENGTH_SHORT).show();
        }
    }
}
