package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class VideoResultAdapter extends RecyclerView.Adapter<VideoResultAdapter.VideoResultsHolder> {
    ArrayList<Video> vids;
    MainActivity.ItemClickListener mListener;
    public VideoResultAdapter(ArrayList<Video> videos, MainActivity.ItemClickListener mListener) {
        this.vids = videos;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public VideoResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_video, parent, false);

        return new VideoResultsHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoResultsHolder holder, int position) {

        // TODO holder.videoThumbnail. ;
        holder.videoTitle.setText(vids.get(position).getTitle());
        holder.videoUsername.setText(vids.get(position).getUserId());
        // TODO holder.videoViews;
        // TODO holder.videoYear.setText(model.); = ;
        // TODO videoTime
    }

    @Override
    public int getItemCount() {
        return vids.size();
    }

    public void clear() {
        final int size = vids.size();
        vids.clear();
        notifyItemRangeRemoved(0, size);
    }

    class VideoResultsHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUsername;
        TextView videoViews;
        TextView videoYear;
        TextView videoTime;
        ImageView moreMenu;

        public VideoResultsHolder(View itemView, final MainActivity.ItemClickListener mListener) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoYear = itemView.findViewById(R.id.videoYear);
            videoTime = itemView.findViewById(R.id.videoTime);
            moreMenu = itemView.findViewById(R.id.moreMenu);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onResultClick(videoTitle.toString());
                }
            });

        }
    }

}
