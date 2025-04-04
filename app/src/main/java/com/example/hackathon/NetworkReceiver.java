package com.example.hackathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
    private FirebaseHelper firebaseHelper;
    private static long lastSyncTime = 0;
    private static final long SYNC_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private Handler handler = new Handler();

    public NetworkReceiver(USSDDatabaseHelper dbHelper) {
        this.firebaseHelper = new FirebaseHelper(dbHelper);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isNetworkAvailable(context)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSyncTime >= SYNC_INTERVAL) {
                lastSyncTime = currentTime;
                handler.postDelayed(() -> {
                    Log.d("NetworkReceiver", "Syncing transactions to Firebase...");
                    firebaseHelper.syncTransactionsToFirebase();
                }, 3000); // Add a small delay to ensure stable network before sync
            } else {
                Log.d("NetworkReceiver", "Skipping sync (too soon)");
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
