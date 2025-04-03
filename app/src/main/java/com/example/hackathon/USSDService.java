package com.example.hackathon;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;
import android.widget.Toast;

public class USSDService extends AccessibilityService {
    private USSDDatabaseHelper databaseHelper;
    public void onCreate() {
        super.onCreate();
        databaseHelper = new USSDDatabaseHelper(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            AccessibilityNodeInfo nodeInfo = event.getSource();
            if (nodeInfo != null) {
                String response = extractUSSDText(nodeInfo);
                if (response != null) {
                    Log.d("USSD", "Response: " + response);
                    Toast.makeText(this, "USSD Response: " + response, Toast.LENGTH_LONG).show();
//                    databaseHelper.insertUSSDResponse(response);
                }
            }
            // Introduce a delay before extracting the USSD response
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                extractUSSDText(nodeInfo);
            }, 2000); // 2-second delay

        }



    }

//    private String extractUSSDText(AccessibilityNodeInfo node) {
//        if (node == null) return null;
//        if (node.getText() != null) return node.getText().toString();
//
//        for (int i = 0; i < node.getChildCount(); i++) {
//            String text = extractUSSDText(node.getChild(i));
//            if (text != null) return text;
//        }
//        return null;

        private String extractUSSDText(AccessibilityNodeInfo node) {
            if (node == null) return null;

            if (node.getText() != null) {
                String text = node.getText().toString();
                Log.d("USSD Debug", "Extracted Text: " + text);
                return text;
            }

            for (int i = 0; i < node.getChildCount(); i++) {
                String text = extractUSSDText(node.getChild(i));
                if (text != null) return text;
            }
            return null;
        }


    @Override
    public void onInterrupt() {
        Toast.makeText(this, "USSD Service Interrupted", Toast.LENGTH_SHORT).show();
    }
}
