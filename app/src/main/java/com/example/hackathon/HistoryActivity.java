//package com.example.hackathon;
//
//import android.database.Cursor;
//import android.os.Bundle;
//import android.widget.ListView;
//import android.widget.SimpleCursorAdapter;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class HistoryActivity extends AppCompatActivity {
//    private USSDDatabaseHelper databaseHelper;
//    private ListView listView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//
//        listView = findViewById(R.id.listView);
//        databaseHelper = new USSDDatabaseHelper(this);
//
//        loadHistory();
//    }
//
//    private void loadHistory() {
//        Cursor cursor = databaseHelper.getAllTransactions();
//        String[] from = new String[]{"response", "timestamp"};
//        int[] to = new int[]{R.id.response_text, R.id.timestamp_text};
//
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                this, R.layout.history_item, cursor, from, to, 0);
//        listView.setAdapter(adapter);
//    }
//}



//package com.example.hackathon;
//
//import android.database.Cursor;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class HistoryActivity extends AppCompatActivity {
//    private USSDDatabaseHelper dbHelper;
//    private TextView historyView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//
//        dbHelper = new USSDDatabaseHelper(this);
//        historyView = findViewById(R.id.historyView);
//        Button payBackButton = findViewById(R.id.payBackButton);
//
//        loadPendingSplits();
//
//        payBackButton.setOnClickListener(v -> {
//            String friendPhone = "9876543210"; // Replace with dynamic phone retrieval logic
//            dbHelper.updateSplitStatus(friendPhone);
//            Toast.makeText(this, "Marked as Paid!", Toast.LENGTH_SHORT).show();
//            loadPendingSplits();
//        });
//    }
//
//    private void loadPendingSplits() {
//        Cursor cursor = dbHelper.getPendingSplits();
//        StringBuilder splitsText = new StringBuilder();
//
//        while (cursor.moveToNext()) {
//            splitsText.append("Friend: ").append(cursor.getString(3))
//                    .append("\nPhone: ").append(cursor.getString(4))
//                    .append("\nAmount: ₹").append(cursor.getDouble(2))
//                    .append("\nStatus: ").append(cursor.getString(5))
//                    .append("\n\n");
//        }
//
//        historyView.setText(splitsText.toString());
//    }
//}


package com.example.hackathon;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    private USSDDatabaseHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        dbHelper = new USSDDatabaseHelper(this);

        loadSplitHistory();
    }

    private void loadSplitHistory() {
        Cursor cursor = dbHelper.getAllSplitTransactions();

        if (cursor != null && cursor.getCount() > 0) {
            SplitHistoryAdapter adapter = new SplitHistoryAdapter(this, cursor);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No transaction history found.", Toast.LENGTH_SHORT).show();
        }
    }


}
