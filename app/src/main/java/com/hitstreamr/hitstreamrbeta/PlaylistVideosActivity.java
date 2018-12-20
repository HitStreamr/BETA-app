package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistVideosActivity extends AppCompatActivity {
    private static final String TAG = "PlaylistVideosActivity";
    private Playlist playlist;
    private RecyclerView recyclerView_PlaylistVideos;
    private PlaylistContentAdapter playlistContent;
    Playlist temp;
    private TextView playlistName;
    private ItemClickListener mlistner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_videos);

        playlistName = findViewById(R.id.playlist_Name);

        playlist = new Playlist();
        temp = getIntent().getExtras().getParcelable("PlaylistVideos");

        playlistName.setText(temp.getPlaylistname());

        mlistner = new ItemClickListener() {
            @Override
            public void onPlaylistVideoClick(Video selectVideo) {
                Intent videoPlayerIntent = new Intent(PlaylistVideosActivity.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectVideo);
                startActivity(videoPlayerIntent);
            }
        };

        //playlist.setPlayVideos(getIntent().getExtras().getParcelableArrayList("PlaylistVideos"));
        Log.e(TAG,  "Playlist Content activity" +getIntent().getStringExtra("PlaylistName") +getIntent().getExtras().getParcelableArrayList("PlaylistVideos") );

        recyclerView_PlaylistVideos = findViewById(R.id.recyclerView_singlePlaylistContent);
        setupRecyclerView();


    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_PlaylistVideos.setLayoutManager(layoutManager);
        //playlistContent = new PlaylistContentAdapter(playlist.playVideos);
        playlistContent = new PlaylistContentAdapter(this, temp, mlistner);
        recyclerView_PlaylistVideos.setAdapter(playlistContent);
    }

    public interface ItemClickListener {
        void onPlaylistVideoClick(Video selectVideo);
    }
}
