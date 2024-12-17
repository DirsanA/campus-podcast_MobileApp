package com.example.navigationdrawer;

public class Subscription {
    private String userId;
    private String email;
    private String name;

    // Default constructor (required for Firebase)
    public Subscription() {
        // Default constructor required for calls to DataSnapshot.getValue(Subscription.class)
    }

    public Subscription(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    // Getters and setters (required for Firebase)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
