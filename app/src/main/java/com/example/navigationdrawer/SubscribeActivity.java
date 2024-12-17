package com.example.navigationdrawer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubscribeActivity extends Activity {
    private Button sbtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_page);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("subscriptions");
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find views
        sbtn = findViewById(R.id.sbtn);

        // Check subscription status
        checkSubscriptionStatus();

        // Set onClick listener for the subscribe button
        sbtn.setOnClickListener(v -> {
            if (sbtn.getText().toString().equals("Subscribe")) {
                subscribeUser();
            } else {
                unsubscribeUser();
            }
        });
    }

    private void checkSubscriptionStatus() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        sbtn.setText("Unsubscribe");
                    } else {
                        sbtn.setText("Subscribe");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SubscribeActivity.this, "Failed to check subscription status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SubscribeActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void subscribeUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            if (name == null || name.isEmpty()) {
                name = "Anonymous"; // Fallback if the user's name is not set
            }

            String userId = currentUser.getUid(); // Use user's UID from FirebaseAuth

            // Create Subscription object
            Subscription subscription = new Subscription(userId, email, name);

            // Save to Firebase Database
            databaseReference.child(userId).setValue(subscription)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SubscribeActivity.this, "Subscribed successfully", Toast.LENGTH_SHORT).show();
                        sbtn.setText("Unsubscribe");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SubscribeActivity.this, "Subscription failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(SubscribeActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void unsubscribeUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Use user's UID from FirebaseAuth

            // Remove from Firebase Database
            databaseReference.child(userId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SubscribeActivity.this, "Unsubscribed successfully", Toast.LENGTH_SHORT).show();
                        sbtn.setText("Subscribe");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SubscribeActivity.this, "Unsubscription failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(SubscribeActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
