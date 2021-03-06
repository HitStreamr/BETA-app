package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class WatchPlaylistAdapter extends RecyclerView.Adapter<WatchPlaylistAdapter.WatchPlaylistViewHolder> {
    private static final String TAG = "WatchPlayListAdapter";
    private ArrayList<Playlist> Playlist;
    private Context mContext;
    private Library.ItemClickListener mlistner;
    private String playlistCreatorID;

    public WatchPlaylistAdapter(Context context, ArrayList<Playlist> playlist, Library.ItemClickListener mlistner,
                                String playlistCreatorID) {
        Log.e(TAG, "Entered Watch Playlist recycler view"+ playlist.get(0).getPlaylistname() + "  " + playlist.size());
        this.Playlist = playlist;
        this.mContext = context;
        this.mlistner = mlistner;
        this.playlistCreatorID = playlistCreatorID;
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
        holder.videoCountPlaylist.setText(String.valueOf(Playlist.get(position).getPlayVideoIds().size()));
        holder.videoCount.setText(String.valueOf(Playlist.get(position).getPlayVideoIds().size()) + " videos");
        holder.thumbnailPlaylist.setScaleType(ImageView.ScaleType.CENTER_CROP);
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

        holder.MoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.editPlaylist:
                                //Log.e(TAG, "selected " +Playlist.get(position));
                                Intent editPlaylistIntent = new Intent(mContext, EditPlaylist.class);
                                editPlaylistIntent.putExtra("playlist", Playlist.get(position));
                                mContext.startActivity(editPlaylistIntent);
                                break;

                            case R.id.deletePlaylist:
                                Intent deletePlaylsit = new Intent(mContext, DeletePlaylist.class);
                                deletePlaylsit.putExtra("PLAYLIST", Playlist.get(position));
                                mContext.startActivity(deletePlaylsit);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.playlist_menu);
                popupMenu.show();
            }
        });

        // Find the playlist creator's username
        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(playlistCreatorID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.username.setText(dataSnapshot.child("username").getValue(String.class));
                        } else {
                            FirebaseDatabase.getInstance().getReference("BasicAccounts").child(playlistCreatorID)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                holder.username.setText(dataSnapshot.child("username")
                                                        .getValue(String.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return Playlist.size();
    }

    public class WatchPlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView singlePlaylist;
        public LinearLayout parentLayout;
        public TextView videoCountPlaylist;
        public TextView videoCount;
        public TextView username;
        public ImageView thumbnailPlaylist;
        private ImageView MoreBtn;

        public WatchPlaylistViewHolder(View view) {
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


