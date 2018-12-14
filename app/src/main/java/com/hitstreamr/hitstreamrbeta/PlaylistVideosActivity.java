package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlaylistVideosActivity extends AppCompatActivity {
    private static final String TAG = "PlaylistVideosActivity";
    private Playlist playlist;
    private RecyclerView recyclerView_PlaylistVideos;
    private PlaylistContentAdapter playlistContent;
    Playlist temp;
    private TextView playlistName;
    private ItemClickListener mlistner;
    private String CreditVal;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_videos);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        playlistName = findViewById(R.id.playlist_Name);
        playlist = new Playlist();
        temp = getIntent().getExtras().getParcelable("PlaylistVideos");
        playlistName.setText(temp.getPlaylistname());

        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if(!Strings.isNullOrEmpty(currentCredit)){

                            CreditVal = currentCredit;
                        }
                        else
                            CreditVal = "0";
                        // Log.e(TAG, "Profile credit val inside change" + CreditVal);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Log.e(TAG, "Profile credit val " + CreditVal);

        mlistner = new ItemClickListener() {
            @Override
            public void onPlaylistVideoClick(Video selectVideo) {
                Intent videoPlayerIntent = new Intent(PlaylistVideosActivity.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectVideo);
                videoPlayerIntent.putExtra("CREDIT", CreditVal);
                startActivity(videoPlayerIntent);
            }
        };

        Log.e(TAG,  "Playlist Content activity" +getIntent().getStringExtra("PlaylistName") +getIntent().getExtras().getParcelableArrayList("PlaylistVideos") );
        recyclerView_PlaylistVideos = findViewById(R.id.recyclerView_singlePlaylistContent);
        setupRecyclerView();

    }

    private void setupRecyclerView(){
        if(temp.getPlayVideos().size()>0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView_PlaylistVideos.setLayoutManager(layoutManager);
            playlistContent = new PlaylistContentAdapter(this, temp, mlistner);
            recyclerView_PlaylistVideos.setAdapter(playlistContent);
        }
    }

    public interface ItemClickListener {
        void onPlaylistVideoClick(Video selectVideo);
    }
}
