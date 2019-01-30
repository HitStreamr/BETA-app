package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference videosCollectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_videos);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        playlistName = findViewById(R.id.playlist_Name);
        playlist = new Playlist();
        temp = getIntent().getParcelableExtra("PlaylistVideos");
        playlistName.setText(temp.getPlaylistname());
        videosCollectionRef = db.collection("Videos");


        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if (!Strings.isNullOrEmpty(currentCredit)) {

                            CreditVal = currentCredit;
                        } else
                            CreditVal = "0";
                        // Log.e(TAG, "Profile credit val inside change" + CreditVal);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Log.e(TAG, "Profile credit val " + CreditVal);

        Log.e(TAG, "temp value is " + temp.getPlayVideoIds());

        mlistner = new ItemClickListener() {
            @Override
            public void onPlaylistVideoClick(Video selectVideo) {
                Intent videoPlayerIntent = new Intent(PlaylistVideosActivity.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectVideo);
                videoPlayerIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
                videoPlayerIntent.putExtra("CREDIT", CreditVal);
                startActivity(videoPlayerIntent);
            }
        };

        Log.e(TAG, "Playlist Content activity" + getIntent().getStringExtra("PlaylistName") + getIntent().getExtras().getParcelableArrayList("PlaylistVideos"));
        recyclerView_PlaylistVideos = findViewById(R.id.recyclerView_singlePlaylistContent);
        getPlayVideos();
        //setupRecyclerView();
    }



    private void getPlayVideos() {
        //Log.e(TAG, "Entered onsuceess" +Play);
        ArrayList<Task<QuerySnapshot>> queryy = new ArrayList<>();
        for (int j = 0; j < temp.getPlayVideoIds().size(); j++) {
            queryy.add(videosCollectionRef.whereEqualTo("videoId", temp.getPlayVideoIds().get(j)).get());
        }

        Task<List<QuerySnapshot>> task = Tasks.whenAllSuccess(queryy);
        task.addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                ArrayList<Video> bb = new ArrayList<>();
                for (QuerySnapshot document : task.getResult()) {
                    for (DocumentSnapshot docume : document.getDocuments()) {
                        Log.e(TAG, "Playlist videos " + docume.toObject(Video.class).getVideoId());
                        bb.add(docume.toObject(Video.class));
                    }
                }
                temp.setPlayVideos(bb);
                setupRecyclerView();
            }
        });
    }

    private void setupRecyclerView() {
        if (temp.getPlayVideos().size() > 0) {
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
