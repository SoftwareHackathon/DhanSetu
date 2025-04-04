package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathon.R;

public class SplitMoney extends AppCompatActivity {

    private EditText totalAmountEditText, amountToSplitEditText, friendNameEditText, friendPhoneEditText;
    private Button cancelButton, saveButton;
    private TextView splitMoneyTab, viewHistoryTab;
    private USSDDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("aa","gye");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.split_money);

        dbHelper = new USSDDatabaseHelper(this);

        // Initialize views
        totalAmountEditText = findViewById(R.id.totalAmountEditText);
        amountToSplitEditText = findViewById(R.id.amountToSplitEditText);
        friendNameEditText = findViewById(R.id.friendNameEditText);
        friendPhoneEditText = findViewById(R.id.friendPhoneEditText);

        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);



        // Set click listeners
        cancelButton.setOnClickListener(v -> {
            // Clear all fields
            totalAmountEditText.setText("");
            amountToSplitEditText.setText("");
            friendNameEditText.setText("");
            friendPhoneEditText.setText("");
        });

        saveButton.setOnClickListener(v -> {
            // Validate and save the split money request
            if (validateInputs()) {
                saveSplitRequest();
            }
        });


    }

    private boolean validateInputs() {
        String totalAmount = totalAmountEditText.getText().toString().trim();
        String amountToSplit = amountToSplitEditText.getText().toString().trim();
        String friendName = friendNameEditText.getText().toString().trim();
        String friendPhone = friendPhoneEditText.getText().toString().trim();

        if (totalAmount.isEmpty()) {
            totalAmountEditText.setError("Please enter total amount");
            return false;
        }

        if (amountToSplit.isEmpty()) {
            amountToSplitEditText.setError("Please enter amount to split");
            return false;
        }

        if (friendName.isEmpty()) {
            friendNameEditText.setError("Please enter friend's name");
            return false;
        }

        if (friendPhone.isEmpty()) {
            friendPhoneEditText.setError("Please enter friend's phone");
            return false;
        }

        return true;
    }

    private void saveSplitRequest() {
        try {
            double totalAmount = Double.parseDouble(totalAmountEditText.getText().toString().trim());
            double splitAmount = Double.parseDouble(amountToSplitEditText.getText().toString().trim());
            String friendName = friendNameEditText.getText().toString().trim();
            String friendPhone = friendPhoneEditText.getText().toString().trim();

            if (splitAmount > totalAmount) {
                Toast.makeText(this, "Split amount cannot exceed total amount!", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.insertSplitTransaction(totalAmount, splitAmount, friendName, friendPhone);
            Toast.makeText(this, "Split request saved successfully", Toast.LENGTH_SHORT).show();

            // Optionally clear fields after save
            cancelButton.performClick();
        } catch (Exception e) {
            Log.e("SplitMoney", "Exception in saveSplitRequest", e);
            Toast.makeText(this, "Invalid input. Try again!", Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshScreen() {
        // Refresh the current screen
        // Could reload data if needed
    }
}
