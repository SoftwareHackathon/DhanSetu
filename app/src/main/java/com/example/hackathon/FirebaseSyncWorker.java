package com.example.hackathon;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class FirebaseSyncWorker extends Worker {
    private USSDDatabaseHelper dbHelper;
    private FirebaseHelper firebaseHelper;

    public FirebaseSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dbHelper = new USSDDatabaseHelper(context);
        firebaseHelper = new FirebaseHelper(dbHelper);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("FirebaseSyncWorker", "Syncing transactions to Firebase...");
        firebaseHelper.syncTransactionsToFirebase();

        return Result.success(); // Work finished successfully
    }
}
