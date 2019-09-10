package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EditPlaylistContentAdapter extends RecyclerView.Adapter<EditPlaylistContentAdapter.EditPlaylistContentViewHolder>  {

    private static final String TAG = "PlaylistContentAdapter";

    private Playlist playlist;
    private Context mContext;

    public EditPlaylistContentAdapter(Context context, Playlist playlist) {
        Log.e(TAG, "Playlist Content adapter constructor entered " + playlist);
        this.playlist = playlist;
        this.mContext = context;
    }

    @NonNull
    @Override
    public EditPlaylistContentAdapter.EditPlaylistContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_playlist_video_order, parent, false);
        Log.e(TAG, "Playlist Content adapter holder entered " + playlist);

        return new EditPlaylistContentAdapter.EditPlaylistContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EditPlaylistContentAdapter.EditPlaylistContentViewHolder holder, int position) {
        holder.videoTitle.setText(playlist.getPlayVideos().get(position).getTitle());
        Glide.with(getApplicationContext()).load(Uri.parse((playlist.getPlayVideos().get(position).getUrl()))).into(holder.videoThumbnail);

    }

    @Override
    public int getItemCount() {
        return playlist.getPlayVideos().size();
    }

    public class EditPlaylistContentViewHolder extends RecyclerView.ViewHolder {
        private TextView videoTitle;
        private TextView videoUsername;
        private ImageView videoThumbnail;
        private LinearLayout parentLayout;

        public EditPlaylistContentViewHolder(View view) {
            super(view);

            videoTitle = view.findViewById(R.id.editPlaylistTitle);
            videoUsername = view.findViewById(R.id.editPlaylistUsername);
            videoThumbnail = view.findViewById(R.id.editPlaylistThumbnail);
            parentLayout = view.findViewById(R.id.editPlaylistCard);
        }
    }

}
