

package com.example.hackathon;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplitActivity extends AppCompatActivity {
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
