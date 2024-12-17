package com.example.navigationdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscriberAdapter extends RecyclerView.Adapter<SubscriberAdapter.SubscriberViewHolder> {

    private Context context;
    private List<Subscription> subscriptionList;

    public SubscriberAdapter(Context context, List<Subscription> subscriptionList) {
        this.context = context;
        this.subscriptionList = subscriptionList;
    }

    @NonNull
    @Override
    public SubscriberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscriber_item, parent, false);
        return new SubscriberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriberViewHolder holder, int position) {
        Subscription subscription = subscriptionList.get(position);

        holder.emailTextView.setText(subscription.getEmail());
    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    public static class SubscriberViewHolder extends RecyclerView.ViewHolder {

        TextView emailTextView;

        public SubscriberViewHolder(@NonNull View itemView) {
            super(itemView);

            emailTextView = itemView.findViewById(R.id.emailTextView);
        }
    }
}