package com.example.navigationdrawer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewHome;
    private HomeItemAdapter homeAdapter;
    private List<HomeItem> homeItemList;

    // Firebase reference
    private DatabaseReference databaseRef;

    // Notification constants
    private static final String CHANNEL_ID = "VideoUploadChannel";
    private static final int NOTIFICATION_ID = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("videos");

        // Initialize homeItemList
        homeItemList = new ArrayList<>();

        // Add a sample video from the raw folder (optional)
//        Uri rawVideoUri = Uri.parse("android.resource://" + requireActivity().getPackageName() + "/" + R.raw.dj);
//        HomeItem localVideoItem = new HomeItem("Local Video", "This is a local video", rawVideoUri.toString());
//        homeItemList.add(localVideoItem);

        // Retrieve videos from Firebase
        retrieveVideos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home, container, false);

        // Initialize RecyclerView
        recyclerViewHome = view.findViewById(R.id.home_recycler);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewHome.setHasFixedSize(true);

        // Initialize adapter
        homeAdapter = new HomeItemAdapter(requireActivity(), homeItemList);
        recyclerViewHome.setAdapter(homeAdapter);

        return view;
    }

    private void retrieveVideos() {
        // Read data from Firebase Realtime Database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear existing list
                homeItemList.clear();

                // Iterate through Firebase data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve video metadata
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String url = snapshot.child("url").getValue(String.class);

                    // Create HomeItem object and add to list
                    HomeItem videoItem = new HomeItem(title, description, url);
                    homeItemList.add(videoItem);
                }

                // Notify adapter of data change outside of the onDataChange method
                updateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void updateAdapter() {
        // Check if fragment is added to its activity
        if (!isAdded() || getActivity() == null) {
            return;
        }

        // Notify adapter of data change on the main thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeAdapter.notifyDataSetChanged();
            }
        });

        // Show notification for successful video upload
        showUploadNotification();
    }

    private void showUploadNotification() {
        // Create an explicit notification channel if the device is running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle("Video Upload")
                .setContentText("A new video has been uploaded successfully!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
