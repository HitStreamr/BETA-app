package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddToPlaylist extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddToPlayList";

    private Button cancel, ok;
    private TextView createPlaylist;
    private PlaylistAdapter adapter;
    private RecyclerView recyclerView_playlist;

    private ItemClickListener mlistner;

    //Video vid;

    String videoId;

    private String playlistSelected;

    private FirebaseUser current_user;
    private ArrayList<String> playlistArraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlist);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        mlistner = new ItemClickListener() {
            @Override
            public void onResultClick(String selectedPlayList) {
                playlistSelected = selectedPlayList;
            }
        };

        Video video = getIntent().getParcelableExtra("VIDEO");
        videoId = video.getVideoId();

        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.confirm);
        createPlaylist = findViewById(R.id.createplaylist);

        recyclerView_playlist = findViewById(R.id.recyclerViewPlaylist);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        createPlaylist.setOnClickListener(this);

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
//        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getWindow().setLayout((int) (width), (int) (height));
        getWindow().setBackgroundDrawable(new ColorDrawable(0x4b000000));

        getPlaylists();
    }

    private void getPlaylists(){
        FirebaseDatabase.getInstance().getReference("Playlists")
                .child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        playlistArraylist = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot each : dataSnapshot.getChildren()) {
                                //temp = each.getValue().toString();
                                playlistArraylist.add(String.valueOf(each.getKey()));
                            }
                            Log.e(TAG, "Playlists : " + playlistArraylist);
                        }
                        setUpRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void setUpRecyclerView(){
        Log.e(TAG,"entered setup recycler view method");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_playlist.setLayoutManager(layoutManager);
        adapter = new PlaylistAdapter(this, playlistArraylist, mlistner);
        recyclerView_playlist.setAdapter(adapter);

    }

    /**
     * Add the video to the selected playlist.
     * Check if a playlist is selected.
     * If none is selected, display a message and cancel the action.
     */
    private void registerVideoToPlaylist() {
        // Check if a playlist has been selected
        if (playlistSelected != null) {
            FirebaseDatabase.getInstance()
                    .getReference("PlaylistVideos")
                    .child(current_user.getUid())
                    .child(playlistSelected)
                    .child(videoId)
                    .setValue(videoId)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Video is added to " + playlistSelected,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please select a playlist.", Toast.LENGTH_LONG).show();
        }
    }


    public interface ItemClickListener {
        void onResultClick(String selectedPlayList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                registerVideoToPlaylist();
                break;
            case R.id.createplaylist:
                startActivity(new Intent(getApplicationContext(), CreateNewPlaylist.class));
                break;
        }
    }
}