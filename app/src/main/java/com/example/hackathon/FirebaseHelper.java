package com.example.hackathon;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private DatabaseReference databaseReference;
    private USSDDatabaseHelper databaseHelper;

    public FirebaseHelper(USSDDatabaseHelper dbHelper) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("transactions");
        this.databaseHelper = dbHelper;
    }

    /**
     * Sync transactions from SQLite to Firebase using batch writes & delete locally after sync
     */
    public void syncTransactionsToFirebase() {
        List<Transaction> transactions = databaseHelper.getAllTransits();
        if (transactions.isEmpty()) {
            Log.d("FirebaseSync", "No transactions to sync.");
            return;
        }

        // Use batch write (updateChildren) instead of individual writes
        Map<String, Object> transactionMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            transactionMap.put(transaction.getT_id(), transaction);
        }

        databaseReference.updateChildren(transactionMap)
                .addOnSuccessListener(aVoid -> {
                    databaseHelper.deleteOldTransactions(); // Deletes transactions older than 30 days
                    Log.d("FirebaseSync", "Synced transactions successfully.");
                })
                .addOnFailureListener(e -> Log.e("FirebaseSync", "Failed to sync transactions", e));
    }
}
