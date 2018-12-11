package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PlaylistContentAdapter extends RecyclerView.Adapter<PlaylistContentAdapter.PlaylistContentViewHolder> {
    private static final String TAG = "PlaylistContentAdapter";

    private Playlist playlist;
    private Context mContext;
    private PlaylistVideosActivity.ItemClickListener mlistner;

    public PlaylistContentAdapter(Context context, Playlist playlist, PlaylistVideosActivity.ItemClickListener mlistner) {
        Log.e(TAG, "Playlist Content adapter constructor entered " + playlist);
        this.playlist = playlist;
        this.mContext = context;
        this.mlistner = mlistner;
    }

    @NonNull
    @Override
    public PlaylistContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_video, parent, false);
        Log.e(TAG, "Playlist Content adapter holder entered " + playlist);

        return new PlaylistContentAdapter.PlaylistContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistContentViewHolder holder, int position) {
        holder.videoTitle.setText(playlist.getPlayVideos().get(position).getTitle());
        holder.videoUsername.setText(playlist.getPlayVideos().get(position).getUsername());
        //holder.videoViews.setText(playlist.getPlayVideos().get(position).getvideoViews);
        Glide.with(getApplicationContext()).load(Uri.parse((playlist.getPlayVideos().get(position).getThumbnailUrl()))).into(holder.videoThumbnail);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onPlaylistVideoClick(playlist.getPlayVideos().get(position));

            }
        });



    }

    @Override
    public int getItemCount() {
        return playlist.playVideos.size();
    }

    public class PlaylistContentViewHolder extends RecyclerView.ViewHolder {
        private TextView videoTitle;
        private TextView videoUsername;
        private TextView videoViews;
        private ImageView videoThumbnail;
        private LinearLayout parentLayout;

        public PlaylistContentViewHolder(View view) {
            super(view);

            videoTitle = view.findViewById(R.id.videoTitle);
            videoUsername = view.findViewById(R.id.videoUsername);
            videoViews = view.findViewById(R.id.videoViews);
            videoThumbnail = view.findViewById(R.id.videoThumbnail);
            parentLayout = view.findViewById(R.id.videoCard);
        }
    }

}
