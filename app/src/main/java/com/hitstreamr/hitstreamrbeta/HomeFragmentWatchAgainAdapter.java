package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HomeFragmentWatchAgainAdapter extends RecyclerView.Adapter<HomeFragmentWatchAgainAdapter.WatchAgainViewHolder> {

    private List<Video> videoList;
    private Context mContext;
    private Intent mIntent;

    /**
     * Constructor
     */
    public HomeFragmentWatchAgainAdapter(List<Video> videoList, Context mContext, Intent mIntent) {
        this.videoList = videoList;
        this.mContext = mContext;
        this.mIntent = mIntent;
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
    }

    @Override
    public int getItemCount() {
        if (videoList != null) {
            return videoList.size();
        } else {
            return 0;
        }
    }

    public class WatchAgainViewHolder extends RecyclerView.ViewHolder {

        TextView title, username, views, likes, reposts, published, duration;

        /**
         * Constructor
         */
        public WatchAgainViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.scrollingTitle);
        }
    }
}
