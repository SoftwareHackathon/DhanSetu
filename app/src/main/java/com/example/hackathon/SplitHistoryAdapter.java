package com.example.hackathon;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cursoradapter.widget.CursorAdapter;

public class SplitHistoryAdapter extends CursorAdapter {
    public SplitHistoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views in the layout
        TextView totalAmountView = view.findViewById(R.id.totalAmount);
        TextView splitAmountView = view.findViewById(R.id.splitAmount);
        TextView friendNameView = view.findViewById(R.id.friendName);
        TextView friendPhoneView = view.findViewById(R.id.friendPhone);

        // Get data from the cursor
        double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
        double splitAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("split_amount"));
        String friendName = cursor.getString(cursor.getColumnIndexOrThrow("friend_name"));
        String friendPhone = cursor.getString(cursor.getColumnIndexOrThrow("friend_phone"));

        // Set formatted text
        totalAmountView.setText("Total Amount: ₹" + totalAmount);
        splitAmountView.setText("Splitted Amount: ₹" + splitAmount);
        friendNameView.setText("Friend Name: " + friendName);
        friendPhoneView.setText("Friend Phone: " + friendPhone);
    }
}
