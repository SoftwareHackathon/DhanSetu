package com.example.hackathon;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccount extends AppCompatActivity {

    EditText phoneEditText, passwordEditText, confirmPasswordEditText, firstNameEditText, lastNameEditText, accountNumberEditText;
    Button signUpButton;
    private Spinner spinner_account_type;
    private CheckBox checkbox_terms;

    USSDDatabaseHelper
 dbHelper;
    DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        // Link UI
        phoneEditText = findViewById(R.id.et_phone_number);
        passwordEditText = findViewById(R.id.et_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        firstNameEditText = findViewById(R.id.et_first_name);
        lastNameEditText = findViewById(R.id.et_last_name);
        accountNumberEditText = findViewById(R.id.et_account_number);
        spinner_account_type = findViewById(R.id.spinner_account_type);
        checkbox_terms = findViewById(R.id.checkbox_terms);
        signUpButton = findViewById(R.id.btn_create_account);

        dbHelper = new USSDDatabaseHelper(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        signUpButton.setOnClickListener(v -> registerUser());

    }

    private void registerUser() {
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String accountNumber = accountNumberEditText.getText().toString().trim();
        boolean isTermsChecked=checkbox_terms.isChecked();

        if (phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                firstName.isEmpty() || lastName.isEmpty() || accountNumber.isEmpty() || !isTermsChecked) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Upload
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("phone", phone);
        userData.put("password", password);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("accountNumber", accountNumber);

        usersRef.child(phone).setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Also store in SQLite for offline login
                dbHelper.insertUser(phone, password, firstName, lastName, accountNumber);

                Toast.makeText(CreateAccount.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateAccount.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(CreateAccount.this, "Firebase Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
