package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
            public void onResultClick(String selectedPlaytList) {
                playlistSelected = selectedPlaytList;
                Log.e(TAG, "playlist selected using interface" +playlistSelected);

            }
        };

        Video video = getIntent().getParcelableExtra("VIDEO");
        videoId = video.getVideoId();
        Log.e(TAG, "Video id is :" +videoId);

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

    private  void registerVideoToPlaylist(){
        FirebaseDatabase.getInstance()
                .getReference("PlaylistVideos")
                .child(current_user.getUid())
                .child(playlistSelected)
                .child(videoId)
                .setValue(videoId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Video is added to playlist and registered");
                    }
                });
    }


    public interface ItemClickListener {
        void onResultClick(String selectedPlaytList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                registerVideoToPlaylist();
                Toast.makeText(this, "video added to " + playlistSelected, Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.createplaylist:
                startActivity(new Intent(getApplicationContext(), CreateNewPlaylist.class));
        }
    }
}
//