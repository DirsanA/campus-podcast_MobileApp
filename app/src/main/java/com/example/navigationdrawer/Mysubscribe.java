package com.example.navigationdrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Mysubscribe extends Fragment {

    private RecyclerView recyclerView;
    private SubscriberAdapter adapter;
    private List<Subscription> subscriptionList;

    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    public Mysubscribe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("subscriptions");
        subscriptionList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mysubscribe, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SubscriberAdapter(getContext(), subscriptionList); // Pass subscriptionList here
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        attachDatabaseListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachDatabaseListener();
    }

    private void attachDatabaseListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Subscription subscription = dataSnapshot.getValue(Subscription.class);
                    if (subscription != null) {
                        subscriptionList.add(subscription);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    // Handle updated data if needed
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    // Handle removed data if needed
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    // Handle moved data if needed
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            };

            databaseReference.addChildEventListener(childEventListener);
        }
    }

    private void detachDatabaseListener() {
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
}
