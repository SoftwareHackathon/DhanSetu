package com.example.hackathon;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cursoradapter.widget.CursorAdapter;

public class TransactionAdapter extends CursorAdapter {
    public TransactionAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views in the layout
        TextView amountView = view.findViewById(R.id.amountText);
        TextView senderView = view.findViewById(R.id.senderText);
        TextView receiverView = view.findViewById(R.id.receiverText);
        TextView idView = view.findViewById(R.id.idText);
        TextView dateView = view.findViewById(R.id.dateText);
        TextView descriptionView = view.findViewById(R.id.descriptionText);

        // Get data from the cursor
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
        String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
        String receiver = cursor.getString(cursor.getColumnIndexOrThrow("receiver"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

        // Set formatted text
        idView.setText("ID: " +id);
        amountView.setText("Amount: ₹" + amount);
        senderView.setText("Sender: " + sender);
        receiverView.setText("Receiver: " + receiver);
        dateView.setText("Date: " + date);
        descriptionView.setText("Description: " + description);
    }
}
