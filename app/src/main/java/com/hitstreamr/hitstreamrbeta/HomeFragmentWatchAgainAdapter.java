package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class HomeFragmentWatchAgainAdapter extends RecyclerView.Adapter<HomeFragmentWatchAgainAdapter.WatchAgainViewHolder> {

    private List<Video> videoList;
    private Context mContext;
    private Intent mIntent;
    private HomeFragment.ItemClickListener mListener;

    /**
     * Constructor
     */
    public HomeFragmentWatchAgainAdapter(List<Video> videoList, Context mContext, Intent mIntent, HomeFragment.ItemClickListener mListener) {
        this.videoList = videoList;
        this.mContext = mContext;
        this.mIntent = mIntent;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public WatchAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_categoried_video, parent, false);
        return new WatchAgainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchAgainViewHolder holder, int position) {
        holder.title.setText(videoList.get(position).getTitle());
        holder.username.setText(videoList.get(position).getUsername());
        holder.views.setText(String.valueOf(videoList.get(position).getViews()));
        holder.duration.setText(videoList.get(position).getDuration());

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.published.setText(dateFormat.format(videoList.get(position).getTimestamp().toDate()));

        // Get the video's thumbnail
        Glide.with(holder.thumbnail).load(videoList.get(position).getThumbnailUrl()).into(holder.thumbnail);

        // Get the number of likes
        FirebaseDatabase.getInstance().getReference("VideoLikes").child(videoList.get(position).getVideoId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.likes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // Get the number of re-posts
        FirebaseDatabase.getInstance().getReference("Repost").child(videoList.get(position).getVideoId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.reposts.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // onClick listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResultClick(videoList.get(position));
            }
        });

        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOverflowClick(videoList.get(position), holder.overflowMenu);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (videoList != null) {
            return videoList.size();
        } else {
            return 0;
        }
    }

    /**
     * Inner Class - Watch Again View Holder
     */
    public class WatchAgainViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail, overflowMenu;
        LinearLayout cardView;
        TextView title, username, views, likes, reposts, published, duration;

        /**
         * Constructor
         */
        public WatchAgainViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.scrollingTitle);
            username = itemView.findViewById(R.id.videoUsername);
            views = itemView.findViewById(R.id.videoViews);
            likes = itemView.findViewById(R.id.faveAmount);
            reposts = itemView.findViewById(R.id.repostAmount);
            published = itemView.findViewById(R.id.published);
            duration = itemView.findViewById(R.id.duration);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
            cardView = itemView.findViewById(R.id.mainBody);
            overflowMenu = itemView.findViewById(R.id.moreMenu);

            title.setSelected(true);
            thumbnail.setSelected(true);
        }
    }
}
