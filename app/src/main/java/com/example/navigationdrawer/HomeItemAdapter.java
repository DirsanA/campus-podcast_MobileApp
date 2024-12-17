package com.example.navigationdrawer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.HomeItemViewHolder> {
    private Activity mActivity;
    private List<HomeItem> homeItemList;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    public HomeItemAdapter(Activity activity, List<HomeItem> homeItemList) {
        this.mActivity = activity;
        this.homeItemList = homeItemList;
    }

    @NonNull
    @Override
    public HomeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home, parent, false);
        return new HomeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemViewHolder holder, int position) {
        HomeItem homeItem = homeItemList.get(position);
        holder.title.setText(homeItem.getTitle());
        holder.description.setText(homeItem.getDescription());

        Uri videoUri = Uri.parse(homeItem.getVideo());
        holder.videoView.setVideoURI(videoUri);

        holder.videoView.setOnPreparedListener(mp -> {
            holder.videoView.seekTo(1);
            holder.videoView.pause();
            holder.seekBar.setMax(mp.getDuration());
        });

        holder.videoView.setOnClickListener(v -> {
            if (holder.videoView.isPlaying()) {
                holder.videoView.pause();
                handler.removeCallbacks(updateSeekBar);
            } else {
                holder.videoView.start();
                updateSeekBar = new Runnable() {
                    @Override
                    public void run() {
                        if (holder.videoView.isPlaying()) {
                            holder.seekBar.setProgress(holder.videoView.getCurrentPosition());
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.post(updateSeekBar);
            }
        });

        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    holder.videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                holder.videoView.pause();
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                holder.videoView.start();
                handler.post(updateSeekBar);
            }
        });

        holder.videoView.setOnCompletionListener(mp -> {
            handler.removeCallbacks(updateSeekBar); // Ensure handler is cleared on video completion
            holder.seekBar.setProgress(0); // Reset SeekBar to start
        });

        holder.fullscreenButton.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, FullScreenVideoActivity.class);
            intent.setData(videoUri);
            mActivity.startActivity(intent);
        });
        holder.more.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, SubscribeActivity.class);
            mActivity.startActivity(intent);
        });


        holder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, homeItem.getTitle() + "\n" + homeItem.getDescription());
            mActivity.startActivity(Intent.createChooser(shareIntent, "Share video via"));
        });
    }

    @Override
    public int getItemCount() {
        return homeItemList.size();
    }

    static class HomeItemViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        SeekBar seekBar;
        TextView title, description;
        Button fullscreenButton;
        ImageView more, share;

        public HomeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            videoView = itemView.findViewById(R.id.video);
            seekBar = itemView.findViewById(R.id.videoSeekBar);
            fullscreenButton = itemView.findViewById(R.id.fullscreenButton);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.more);
            share = itemView.findViewById(R.id.share);
        }
    }
}
