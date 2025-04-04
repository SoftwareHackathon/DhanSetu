package com.example.hackathon;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SharedExpenses extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_expenses);

        // Get references to buttons
        Button viewSplitHistoryButton = findViewById(R.id.viewSplitHistoryButton);
        Button splitMoneyButton = findViewById(R.id.splitButton);

        // Set Click Listeners
        viewSplitHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SharedExpenses.this, SplitActivity.class);
                startActivity(intent);
            }
        });

        splitMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open SplitMoney Activity
//                showSplitDialog()
                Log.d("Going","Split Money");
                Intent intent = new Intent(SharedExpenses.this, SplitMoney.class);
                startActivity(intent);
            }
        });


    }
}