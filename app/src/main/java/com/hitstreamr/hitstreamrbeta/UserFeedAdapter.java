package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.UserFeedHolder> {
    private Context mContext;
    private int mResource;
    private ArrayList<Video> objects1 = new ArrayList<>();
    private ArrayList<Feed> objects2 = new ArrayList<>();
    LinearLayout FeedLikes, FeedRepost;
    Profile.ItemClickListener mListener;
    RequestBuilder<Drawable> requestBuilder;
    String userLike;
    String UserRepost;

    public UserFeedAdapter(@NonNull ArrayList<Video> objects, @NonNull ArrayList<Feed> UserFeedDtls, Profile.ItemClickListener mListener, RequestManager gRequests) {
        this.objects1 = objects;
        this.objects2 = UserFeedDtls;
        this.mListener = mListener;
        requestBuilder = gRequests.asDrawable();
    }

    @NonNull
    @Override
    public UserFeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_feed_layout, parent, false);

        return new UserFeedHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFeedHolder holder, int position) {
        requestBuilder.load(objects1.get(position).getThumbnailUrl()).into(holder.videoThumbnail);
        holder.videoTitle.setText(objects1.get(position).getTitle());
        holder.videoUsername.setText(objects1.get(position).getUsername());
        userLike = objects2.get(position).getFeedLike();
        UserRepost = objects2.get(position).getFeedRepost();

        if (userLike.equals("Y")) {
            FeedLikes.setVisibility(View.VISIBLE);
        }

        if (UserRepost.equals("Y")) {
            FeedRepost.setVisibility(View.VISIBLE);
        }

        holder.mainSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResultClick(objects1.get(position));
            }
        });

        holder.videoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResultClick(objects1.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects1.size();
    }

    public void clear() {
        final int size = objects1.size();
        objects1.clear();
        notifyItemRangeRemoved(0, size);
    }

    class UserFeedHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUsername;
        LinearLayout mainSection;
        Profile.ItemClickListener mListener;

        public UserFeedHolder(View itemView, final Profile.ItemClickListener mListener) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            FeedLikes =itemView.findViewById(R.id.Like);
            FeedRepost = itemView.findViewById(R.id.Repost);
            mainSection = itemView.findViewById(R.id.mainBody);
        }
    }

}


