package com.example.navigationdrawer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userEmail;
    private EditText pwd;
    private EditText favouriteWord;
    private Button register;

    // Firebase authentication and database references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Find the views by their IDs
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.user_email);
        pwd = findViewById(R.id.pwd);
        favouriteWord = findViewById(R.id.favouriteWord);
        register = findViewById(R.id.register);

        // Set up click listener for the register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });
    }

    private void onRegister() {
        // Get the values entered by the user
        String name = userName.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String password = pwd.getText().toString().trim();
        String favouriteWordInput = favouriteWord.getText().toString().trim();

        // Validate the input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(favouriteWordInput)) {
            // Display an error message to the user
            Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success, save user information in the database
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            createUserInDatabase(user.getUid(), name, email, favouriteWordInput);
                        }
                    } else {
                        // If registration fails, display a message to the user
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUserInDatabase(String userId, String name, String email, String favouriteWord) {
        // Create a User object to store in the database
        User user = new User(name, email, favouriteWord);

        // Save the user information in the Firebase Realtime Database
        mDatabase.child(userId).setValue(user)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User information saved successfully, navigate to the login screen or display a success message
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the registration activity
                    } else {
                        // If saving fails, display a message to the user
                        Toast.makeText(RegisterActivity.this, "Failed to save user information: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class User {
        public String name;
        public String email;
        public String favouriteWord;

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        public User() {
        }

        public User(String name, String email, String favouriteWord) {
            this.name = name;
            this.email = email;
            this.favouriteWord = favouriteWord;
        }
    }
}
