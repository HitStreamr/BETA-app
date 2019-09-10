package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TrendingVideos extends AppCompatActivity {
    private RecyclerView recyclerView_TrendingNow;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser current_user;
    private CollectionReference trendingNowRef = db.collection("Videos");
    private TrendingVideosAdapter adapter;
    private ItemClickListener mlistner;
    private String CreditVal;
    private Video onClickedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_videos);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        mlistner = new ItemClickListener() {
            @Override
            public void onTrendingMoreClick(Video selectedVideo) {
                Intent videoPlayerIntent = new Intent(TrendingVideos.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectedVideo);
                videoPlayerIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
                videoPlayerIntent.putExtra("CREDIT", CreditVal);
                startActivity(videoPlayerIntent);
            }

            @Override
            public void onOverflowClick(Video video, View view) {
                onClickedVideo = video;
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.video_menu_pop_up);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addToWatchLater_videoMenu:
                                FirebaseDatabase.getInstance()
                                        .getReference("WatchLater")
                                        .child(current_user.getUid())
                                        .child(onClickedVideo.getVideoId())
                                        .child("VideoId")
                                        .setValue(onClickedVideo.getVideoId())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Video has been added to Watch Later",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;

                            case R.id.addToPlaylist_videoMenu:
                                Intent playlistIntent = new Intent(getApplicationContext(), AddToPlaylist.class);
                                playlistIntent.putExtra("VIDEO", onClickedVideo);
                                playlistIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
                                startActivity(playlistIntent);
                                break;

                            case R.id.report_videoMenu:
                                Intent reportVideo = new Intent(getApplicationContext(), ReportVideoPopup.class);
                                reportVideo.putExtra("VideoId", onClickedVideo.getVideoId());
                                startActivity(reportVideo);
                                break;
                        }
                        return true;
                    }
                });
            }
        };


        /*mlistner = selectedVideo -> {
            Intent videoPlayerIntent = new Intent(TrendingVideos.this, VideoPlayer.class);
            videoPlayerIntent.putExtra("VIDEO", selectedVideo);
            videoPlayerIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
            videoPlayerIntent.putExtra("CREDIT", CreditVal);
            startActivity(videoPlayerIntent);
        };*/

        recyclerView_TrendingNow = findViewById(R.id.RecyclerView_TrendingNow);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Query query = trendingNowRef
                .whereEqualTo("delete", "N")
                .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0])
                .orderBy("views", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        adapter = new TrendingVideosAdapter(options, mlistner);
        recyclerView_TrendingNow.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_TrendingNow.setLayoutManager(layoutManager);
        recyclerView_TrendingNow.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public interface ItemClickListener {
        void onTrendingMoreClick(Video selectedVideo);
        void onOverflowClick(Video video, View view);

    }
}

