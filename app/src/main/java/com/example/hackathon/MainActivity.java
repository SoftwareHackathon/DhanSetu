package com.example.hackathon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private String ussdCode = "*99#";  // Change to required USSD
    private USSDDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new USSDDatabaseHelper(this);
//        listView = findViewById(R.id.listView);
        Button splitButton = findViewById(R.id.splitButton);
        splitButton.setOnClickListener(v -> showSplitDialog());

        Button viewSplitHistoryButton = findViewById(R.id.viewSplitHistoryButton);
        viewSplitHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        Button viewTransHistoryButton = findViewById(R.id.viewTransHistoryButton);
        viewTransHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransHistory.class);
            startActivity(intent);
        });

        loadTransactionsFromCSV();  // Load data in background
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
                    if (values.length < 6) {
                        Log.e("CSV Import", "Skipping malformed line: " + line);
                        continue;
                    }

                    int _id = Integer.parseInt(values[0].trim());
                    double amount = Double.parseDouble(values[1].trim());
                    String sender = values[2].trim();
                    String receiver = values[3].trim();
                    String date = values[4].trim();
                    String description = values[5].trim();

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

                    db.execSQL("INSERT INTO transactions (_id,amount, sender, receiver, date, description) VALUES (?,?, ?, ?, ?, ?)",
                            new Object[]{_id,amount, sender, receiver, date, description});

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
    private void showSplitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Split Money");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText totalAmountInput = new EditText(this);
        totalAmountInput.setHint("Total Amount");
        layout.addView(totalAmountInput);

        EditText splitAmountInput = new EditText(this);
        splitAmountInput.setHint("Amount to Split");
        layout.addView(splitAmountInput);

        EditText friendNameInput = new EditText(this);
        friendNameInput.setHint("Friend's Name");
        layout.addView(friendNameInput);

        EditText friendPhoneInput = new EditText(this);
        friendPhoneInput.setHint("Friend's Phone");
        layout.addView(friendPhoneInput);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            try {
                double totalAmount = Double.parseDouble(totalAmountInput.getText().toString().trim());
                double splitAmount = Double.parseDouble(splitAmountInput.getText().toString().trim());
                String friendName = friendNameInput.getText().toString().trim();
                String friendPhone = friendPhoneInput.getText().toString().trim();

                if (friendName.isEmpty() || friendPhone.isEmpty()) {
                    Toast.makeText(this, "Please enter all details!", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelper.insertSplitTransaction(totalAmount, splitAmount, friendName, friendPhone);
                Toast.makeText(this, "Split saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Invalid input. Try again!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /** 🔹 USSD Handling */
    private void triggerUSSD(String ussdCode) {
        String encodedUssd = Uri.encode(ussdCode);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedUssd));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
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

    /** 🔹 USSD Shortcuts */
    public void sendMoney(View view) {
        triggerUSSD("*99*1#");  // Send Money USSD Code
    }

    public void checkBalance(View view) {
        triggerUSSD("*99*3#");  // Check Balance USSD Code
    }

    public void showTransaction(View view) {
        triggerUSSD("*99*6#");  // Show Transaction History USSD Code
    }
}

