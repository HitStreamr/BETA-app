package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MoreWatchAgainAdapter extends RecyclerView.Adapter<MoreWatchAgainAdapter.MoreWatchAgainViewHolder> {

    private List<Video> videoList;
    private Context mContext;
    private Intent mIntent;
    private MoreWatchAgain.ItemClickListener mListener;

    /**
     * Constructor
     */
    public MoreWatchAgainAdapter(List<Video> videoList, Context mContext, Intent mIntent, MoreWatchAgain.ItemClickListener mListener) {
        this.videoList = videoList;
        this.mContext = mContext;
        this.mIntent = mIntent;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MoreWatchAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_video, parent, false);
        return new MoreWatchAgainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreWatchAgainViewHolder holder, int position) {
        holder.title.setText(videoList.get(position).getTitle());
        holder.duration.setText(videoList.get(position).getDuration());

        String viewCount = String.valueOf(videoList.get(position).getViews()) + " views";
        holder.views.setText(viewCount);

        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        holder.year.setText(dateFormat.format(videoList.get(position).getTimestamp().toDate()));

        // Get the video's thumbnail
        Glide.with(holder.thumbnail).load(videoList.get(position).getUrl()).into(holder.thumbnail);

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

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResultClick(videoList.get(position));
            }
        });

        // Get the uploader's username
        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(videoList.get(position).getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.username.setText(dataSnapshot.child("username").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
    public class MoreWatchAgainViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail, overflowMenu;
        LinearLayout cardView;
        TextView title, username, views, duration, year;

        /**
         * Constructor
         */
        public MoreWatchAgainViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.videoTitle);
            username = itemView.findViewById(R.id.videoUsername);
            views = itemView.findViewById(R.id.videoViews);
            duration = itemView.findViewById(R.id.videoTime);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
            cardView = itemView.findViewById(R.id.mainBody);
            overflowMenu = itemView.findViewById(R.id.moreMenu);
            year = itemView.findViewById(R.id.videoYear);

            title.setSelected(true);
            thumbnail.setSelected(true);
        }
    }
}
