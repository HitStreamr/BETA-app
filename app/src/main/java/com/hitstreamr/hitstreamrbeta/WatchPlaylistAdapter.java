package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;


public class WatchPlaylistAdapter extends RecyclerView.Adapter<WatchPlaylistAdapter.WatchPlaylistViewHolder> {
    private static final String TAG = "WatchPlayListAdapter";
    private ArrayList<Playlist> Playlist;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public WatchPlaylistAdapter(ArrayList<Playlist> playlist) {
        Log.e(TAG, "Entered Watch Playlist recycler view"+ playlist.get(0).getPlaylistname() + "  " + playlist.size());
        this.Playlist = playlist;
    }

    @NonNull
    @Override
    public WatchPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_layout, parent, false);
        Log.e(TAG, "Watch Playlists adapter holder entered ");

        return new WatchPlaylistAdapter.WatchPlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchPlaylistViewHolder holder, int position) {
        holder.singlePlaylist.setText(Playlist.get(position).getPlaylistname());
        holder.videoCountPlaylist.setText(String.valueOf(Playlist.get(position).getPlayVideos().size()));

        /*db.collection("Videos").document(Playlist.get(position).getPlayVideos().get(0)).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                    }
                });*/
    }

    @Override
    public int getItemCount() {
        return Playlist.size();
    }

    public class WatchPlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView singlePlaylist;
        public LinearLayout parentLayout;
        //public TextView userPlaylist;
        public TextView videoCountPlaylist;

        public WatchPlaylistViewHolder(View view) {
            super(view);
            singlePlaylist = view.findViewById(R.id.playlistTitle);
            parentLayout = view.findViewById(R.id.playlistCard);
            //userPlaylist = view.findViewById(R.id.videoUsername);
            videoCountPlaylist = view.findViewById(R.id.videoCount);

        }
    }
}

