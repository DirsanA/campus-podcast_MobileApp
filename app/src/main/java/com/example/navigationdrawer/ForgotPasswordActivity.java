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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText favouriteWordEditText;
    private EditText newPasswordEditText;
    private Button resetButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Find the views by their IDs
        emailEditText = findViewById(R.id.email);
        favouriteWordEditText = findViewById(R.id.favourite);
        newPasswordEditText = findViewById(R.id.newPwd);
        resetButton = findViewById(R.id.login);

        // Set up click listener for the reset button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        // Get the values entered by the user
        String email = emailEditText.getText().toString().trim();
        String favouriteWord = favouriteWordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        // Validate the input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(favouriteWord) || TextUtils.isEmpty(newPassword)) {
            // Display an error message to the user
            Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email exists in the database
        mDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String storedFavouriteWord = userSnapshot.child("favouriteWord").getValue(String.class);
                        if (favouriteWord.equals(storedFavouriteWord)) {
                            // User validation successful, proceed with password update
                            String userId = userSnapshot.getKey();
                            updatePassword(userId, newPassword);
                        } else {
                            // Favorite word does not match
                            Toast.makeText(ForgotPasswordActivity.this, "Favorite word does not match.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Email not found
                    Toast.makeText(ForgotPasswordActivity.this, "Email not registered.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword(String userId, String newPassword) {
        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(passwordUpdateTask -> {
                if (passwordUpdateTask.isSuccessful()) {
                    // Update the password in the database
                    mDatabase.child(userId).child("password").setValue(newPassword)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Failed to update password in database: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to update password: " + passwordUpdateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ForgotPasswordActivity.this, "User not signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
