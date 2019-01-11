package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PostVideoFeedAdapter extends RecyclerView.Adapter<PostVideoFeedAdapter.VideoPostHolder> {
    private static final String TAG = "PostVideoFeedAdapter";
    private ArrayList<Video> postVideoFeed ;
    private ArrayList<String> postTypeFeed ;
    private ArrayList<String> postLikeFeed;

    public PostVideoFeedAdapter(ArrayList<Video> postVideoFeed, ArrayList<String> postTypeFeed, ArrayList<String> postLikeCountFeed) {
        this.postVideoFeed = postVideoFeed;
        this.postTypeFeed = postTypeFeed;
        this.postLikeFeed = postLikeCountFeed;
        Log.e(TAG, "Post Video Feed constructor entered ");
    }

    @NonNull
    @Override
    public VideoPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_activity, parent, false);
        Log.e(TAG, "Post Video Feed holder entered ");
        return new VideoPostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostVideoFeedAdapter.VideoPostHolder holder, int position) {
        holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getApplicationContext()).load(postVideoFeed.get(position).getThumbnailUrl()).into(holder.thumbnail);
        String viewCount = Long.toString(postVideoFeed.get(position).getViews());
        holder.views.setText(viewCount);
        holder.username.setText(postVideoFeed.get(position).getUsername());
        holder.duration.setText(postVideoFeed.get(position).getDuration());
        holder.title.setText(postVideoFeed.get(position).getTitle());
        if(postLikeFeed.get(position).equals("0")){
            holder.activity.setText(postTypeFeed.get(position) + "  a video");
        }
        else {
            holder.activity.setText(" and " + postLikeFeed.get(position) + " others " + postTypeFeed.get(position) + "ed  a video");
        }

        String sdate = postVideoFeed.get(position).getTimestamp().toString();
        holder.published.setText(sdate);

        /*//Timestamp ts = postVideoFeed.get(position).getTimestamp();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        //Interval interval = new Interval(ts.getTime(), now.getTime());
        Log.e(TAG, "Post Video Feed Time " +now.getTime() );*/
    }

    @Override
    public int getItemCount() {
        return postVideoFeed.size();
    }

    public class VideoPostHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public TextView title;
        public TextView artist;
        public TextView username;
        public TextView duration;
        public TextView views;
        public TextView published;
        public TextView activity;
        public TextView timestampDiff;

        public VideoPostHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.feedThumbnail);
            title = itemView.findViewById(R.id.feedTitle);
            artist = itemView.findViewById(R.id.feedArtist);
            duration = itemView.findViewById(R.id.feedDuration);
            views = itemView.findViewById(R.id.feedViews);
            published = itemView.findViewById(R.id.feedPublished);
            username = itemView.findViewById(R.id.feedUsername);
            activity = itemView.findViewById(R.id.feedActivity);
            timestampDiff = itemView.findViewById(R.id.timestampDifference);
        }
    }

}
