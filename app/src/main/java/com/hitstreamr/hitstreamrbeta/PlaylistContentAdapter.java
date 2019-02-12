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
        String viewCount = Long.toString(playlist.getPlayVideos().get(position).getViews()) + " views";
        String pubYear = Long.toString(playlist.getPlayVideos().get(position).getPubYear());
        holder.videoViewsCount.setText(viewCount);
        holder.videoPublishedYear.setText(pubYear);
        holder.videoDuration.setText(playlist.getPlayVideos().get(position).getDuration());

        Glide.with(getApplicationContext()).load(Uri.parse((playlist.getPlayVideos().get(position)
                .getThumbnailUrl()))).into(holder.videoThumbnail);

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
        private ImageView videoThumbnail;
        private TextView videoViewsCount;
        private TextView videoPublishedYear;
        private LinearLayout parentLayout;
        private ImageView videoMoreBtn;
        private TextView videoDuration;

        public PlaylistContentViewHolder(View view) {
            super(view);

            videoTitle = view.findViewById(R.id.videoTitle);
            videoUsername = view.findViewById(R.id.videoUsername);
            videoThumbnail = view.findViewById(R.id.videoThumbnail);
            videoViewsCount = itemView.findViewById(R.id.videoViews);
            videoPublishedYear = itemView.findViewById(R.id.videoYear);
            parentLayout = view.findViewById(R.id.videoCard);
            videoMoreBtn = view.findViewById(R.id.moreMenu);
            videoDuration = view.findViewById(R.id.videoTime);
        }
    }

}
