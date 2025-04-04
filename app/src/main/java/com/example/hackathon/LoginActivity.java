package com.example.hackathon;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    EditText phoneEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure this matches your login XML

        // Find the "Create Account" TextView
        TextView createAccountText = findViewById(R.id.tv_create_account);

        // Set click listener to navigate to CreateAccount activity
        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        // Find the "Sign In" button
        Button signInButton = findViewById(R.id.btn_login);

        // Set click listener to navigate to MainActivity
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();  // THIS runs your login logic!
            }
        });

    }
    private void loginUser() {
        phoneEditText = findViewById(R.id.et_mobile);
        passwordEditText = findViewById(R.id.et_password);
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isNetworkAvailable()) {
            // No internet - Check local SQLite
            USSDDatabaseHelper dbHelper = new USSDDatabaseHelper(LoginActivity.this);
            boolean valid = dbHelper.validateOfflineLogin(phone, password);
            if (valid) {
                Toast.makeText(this, "Offline login successful", Toast.LENGTH_SHORT).show();
                // ✅ Navigate to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid offline credentials", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Online - Check Firebase
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(phone);
            userRef.get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    String storedPassword = dataSnapshot.child("password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Sync to local SQLite
                        String firstName = dataSnapshot.child("firstName").getValue(String.class);
                        String lastName = dataSnapshot.child("lastName").getValue(String.class);
                        String accountNumber = dataSnapshot.child("accountNumber").getValue(String.class);

                        USSDDatabaseHelper dbHelper = new USSDDatabaseHelper(LoginActivity.this);
                        dbHelper.insertUser(phone, password, firstName, lastName, accountNumber);

                        // ✅ Navigate to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "User not found. Please register first.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

}