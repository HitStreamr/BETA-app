package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfilePlaylistAdapter extends RecyclerView.Adapter<ProfilePlaylistAdapter.ProfilePlaylistViewHolder> {
    private static final String TAG = "WatchPlayListAdapter";
    private ArrayList<Playlist> Playlist;
    private Context mContext;
    private Profile.ItemClickListener mlistner;

    public ProfilePlaylistAdapter(Context context, ArrayList<Playlist> playlist, Profile.ItemClickListener mlistner) {
        Log.e(TAG, "Entered Watch Playlist recycler view"+ playlist.get(0).getPlaylistname() + "  " + playlist.size());
        this.Playlist = playlist;
        this.mContext = context;
        this.mlistner = mlistner;
    }

    @NonNull
    @Override
    public ProfilePlaylistAdapter.ProfilePlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_layout, parent, false);
        Log.e(TAG, "Watch Playlists adapter holder entered ");

        return new ProfilePlaylistAdapter.ProfilePlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePlaylistAdapter.ProfilePlaylistViewHolder holder, int position) {
        ArrayList<Task<QuerySnapshot>> queryy = new ArrayList<>();
        for (int j = 0; j < Playlist.get(position).getPlayVideoIds().size(); j++) {
            queryy.add(FirebaseFirestore.getInstance()
                    .collection("Videos")
                    .whereEqualTo("videoId", Playlist.get(position).getPlayVideoIds().get(j))
                    .whereEqualTo("delete", "N")
                    .whereEqualTo("privacy","Public (everyone can see)")
                    .get());
        }

        Task<List<QuerySnapshot>> task = Tasks.whenAllSuccess(queryy);
        task.addOnCompleteListener(task1 -> {
            int temp = 0;
            for (QuerySnapshot document : task1.getResult()) {
                for (DocumentSnapshot docume : document.getDocuments()) {
                    temp++;
                }
            }
            holder.videoCountPlaylist.setText(String.valueOf(temp));
            holder.videoCount.setText(String.valueOf(temp) + " videos");
        });
        holder.singlePlaylist.setText(Playlist.get(position).getPlaylistname());
        if(!Playlist.get(position).getPlayVideoIds().isEmpty()) {
            if (!Playlist.get(position).getPlayThumbnails().equals("empty")) {
                Glide.with(getApplicationContext()).load(Uri.parse(Playlist.get(position).getPlayThumbnails())).into(holder.thumbnailPlaylist);
            }
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onPlaylistClick(Playlist.get(position));
            }
        });
    }
    @Override
    public int getItemCount() {
        return Playlist.size();
    }

    public void clear(){
        final int size = Playlist.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Playlist.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    public class ProfilePlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView singlePlaylist;
        public LinearLayout parentLayout;
        public TextView videoCountPlaylist;
        public TextView videoCount;
        public TextView username;
        public ImageView thumbnailPlaylist;
        private ImageView MoreBtn;

        public ProfilePlaylistViewHolder(View view) {
            super(view);
            singlePlaylist = view.findViewById(R.id.playlistTitle);
            parentLayout = view.findViewById(R.id.playlistCard);
            videoCountPlaylist = view.findViewById(R.id.videoCount);
            videoCount = view.findViewById(R.id.videoPViews);
            username = view.findViewById(R.id.videoUsername);
            thumbnailPlaylist = view.findViewById(R.id.videoThumbnail);
            MoreBtn = view.findViewById(R.id.moreMenu);
        }
    }
}
