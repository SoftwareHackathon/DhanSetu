package com.example.hackathon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private String ussdCode = "*99#";  // Change to required USSD
    private USSDDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get references to icon buttons
        LinearLayout checkBalanceButton = findViewById(R.id.checkBalanceButton);
        LinearLayout sendMoneyButton = findViewById(R.id.sendMoneyButton);
        LinearLayout transactionsButton = findViewById(R.id.viewTransHistoryButton);
        LinearLayout splitExpensesButton = findViewById(R.id.sharedExpensesButton);
        // Set Click Listeners
        checkBalanceButton.setOnClickListener(v -> checkBalance(v));
        sendMoneyButton.setOnClickListener(v -> sendMoney(v));
        transactionsButton.setOnClickListener(v -> showTransaction(v));
        splitExpensesButton.setOnClickListener(v -> split(v));

        dbHelper = new USSDDatabaseHelper(this);
//        listView = findViewById(R.id.listView);



        LinearLayout viewTransHistoryButton = findViewById(R.id.viewTransHistoryButton);
        viewTransHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransHistory.class);
            startActivity(intent);
        });

        loadTransactionsFromCSV();  // Load data in background

//        // Schedule periodic sync every 1 minutes
//        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
//                FirebaseSyncWorker.class,
//                15, TimeUnit.MINUTES) // Set interval (minimum 15 min)
//                .build();
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//                "FirebaseSync",
//                ExistingPeriodicWorkPolicy.KEEP, // Prevents duplicate work
//                syncWorkRequest
//        );

        Button syncButton = findViewById(R.id.btnSync);
        syncButton.setOnClickListener(v -> {
            WorkManager.getInstance(this).enqueue(
                    new OneTimeWorkRequest.Builder(FirebaseSyncWorker.class).build()
            );
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // or finish();
        return true;
    }

    /** 🔹 Load CSV transactions (Runs in Background) */
    private void loadTransactionsFromCSV() {
        Log.d("CSV Import", "Calling loadTransactionsFromCSV()");
        new Thread(() -> {
            Log.d("CSV Import", "Starting background thread to load transactions.");

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            BufferedReader reader = null;

            try {
                InputStream inputStream = getAssets().open("transactions.csv"); // Ensure the file is placed in 'assets' folder
                reader = new BufferedReader(new InputStreamReader(inputStream));

                db.beginTransaction();
                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false; // Skip the first line (header row)
                        continue;
                    }

                    String[] values = line.split(",");
                    if (values.length < 5) {
                        Log.e("CSV Import", "Skipping malformed line: " + line);
                        continue;
                    }

//                    int _id = Integer.parseInt(values[0].trim());
                    double amount = Double.parseDouble(values[0].trim());
                    String sender = values[1].trim();
                    String receiver = values[2].trim();
                    String date = values[3].trim();
                    String description = values[4].trim();

                    // Validate and format date if needed
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        try {
                            java.util.Date parsedDate = sdf.parse(date);
                            date = sdf.format(parsedDate);
                        } catch (Exception e) {
                            Log.e("CSV Import", "Date parsing error: " + date, e);
                            date = "2025-01-01"; // Default fallback date
                        }
                    }

                    db.execSQL("INSERT INTO transactions (amount, sender, receiver, date, description) VALUES (?, ?, ?, ?, ?)",
                            new Object[]{amount, sender, receiver, date, description});

                    Log.d("CSV Import", "Inserted transaction: " + line);
                }

                db.setTransactionSuccessful();
                Log.d("CSV Import", "CSV import completed successfully.");

            } catch (Exception e) {
                Log.e("CSV Import", "Error reading CSV file", e);
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                db.endTransaction();
                db.close();
            }
        }).start();
    }



    /** 🔹 Fetch Transactions Asynchronously */


    /** 🔹 Split Money Dialog */


    /** 🔹 USSD Handling */
    private void triggerUSSD(View view,String ussdCode) {
        String encodedUssd = Uri.encode(ussdCode);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedUssd));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                Snackbar.make(view, "USSD request failed. Try again.", Snackbar.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }
    }

    private void makeUSSDCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(ussdCode)));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeUSSDCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void split(View view) {


            Intent intent = new Intent(this, SharedExpenses.class);
            startActivity(intent);


    }

    /** 🔹 USSD Shortcuts */
    public void sendMoney(View view) {
        triggerUSSD(view,"*99*1#");  // Send Money USSD Code
    }

    public void checkBalance(View view) {
        triggerUSSD(view,"*99*3#");  // Check Balance USSD Code
    }

    public void showTransaction(View view) {
        triggerUSSD(view,"*99*6#");  // Show Transaction History USSD Code
    }
}

