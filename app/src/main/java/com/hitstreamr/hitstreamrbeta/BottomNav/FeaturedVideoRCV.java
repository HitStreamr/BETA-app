package com.hitstreamr.hitstreamrbeta.BottomNav;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.AddToPlaylist;
import com.hitstreamr.hitstreamrbeta.FeaturedVideoResultAdapter;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.ReportVideoPopup;
import com.hitstreamr.hitstreamrbeta.Video;
import com.hitstreamr.hitstreamrbeta.VideoClickListener;
import com.hitstreamr.hitstreamrbeta.VideoPlayer;

import java.util.ArrayList;

public class FeaturedVideoRCV extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, VideoClickListener {
    private static final String TAG = "FEATURED_VIDEOS_RCV" ;
    //Featured Artist
    RecyclerView featuredArtistRCV;
    Video selectedFeaturedVideo;
    Query featuredVideosQuery;
    Task<QuerySnapshot> featuredResults;
    ArrayList<Video> featuredVideos;
    private FeaturedVideoResultAdapter resultAdapter;
    private final int MAX_PRELOAD = 5;
    RequestManager glideRequests;
    String userCredits;
    String type;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_video_rcv);

        // Get the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        /**
         * Featured Artist Start
         */
        //userCredits = getIntent().getStringExtra("CREDITS");
        type = getIntent().getStringExtra("TYPE");


        featuredVideos = new ArrayList<>();

        glideRequests = Glide.with(this);

        //Recycler View
        featuredArtistRCV = findViewById(R.id.featuredVideosRCV);

        featuredVideosQuery = FirebaseFirestore.getInstance()
                .collection("FeaturedVideo")
                .orderBy("views", Query.Direction.DESCENDING )
                .whereEqualTo("privacy","Public (everyone can see)");

        featuredResults = featuredVideosQuery.get();

        //See if successful
        featuredResults.addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot docs : queryDocumentSnapshots) {
                if (docs.exists()) {
                    featuredVideos.add(docs.toObject(Video.class));
                } else {
                    Log.e(TAG, "Document " + docs.toString() + "does not exist");
                }
            }

            VideoClickListener mListener = this;

            resultAdapter = new FeaturedVideoResultAdapter(featuredVideos, mListener, glideRequests);
            RecyclerViewPreloader<Video> preloader = new RecyclerViewPreloader<>( glideRequests, resultAdapter, resultAdapter, MAX_PRELOAD /*maxPreload*/);
            resultAdapter.notifyDataSetChanged();
            featuredArtistRCV.addOnScrollListener(preloader);
            featuredArtistRCV.setAdapter(resultAdapter);
            featuredArtistRCV.setLayoutManager(new LinearLayoutManager(this));

        });

        /*
         *  Featured Artist End
         */
    }

    public void showOverflow(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.video_menu_pop_up);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addToWatchLater_videoMenu:
                FirebaseDatabase.getInstance()
                        .getReference("WatchLater")
                        .child(current_user.getUid())
                        .child(selectedFeaturedVideo.getVideoId())
                        .child("VideoId")
                        .setValue(selectedFeaturedVideo.getVideoId())
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
                playlistIntent.putExtra("VIDEO", selectedFeaturedVideo);
                playlistIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
                startActivity(playlistIntent);
                break;

            case R.id.report_videoMenu:
                Intent reportVideo = new Intent(getApplicationContext(), ReportVideoPopup.class);
                reportVideo.putExtra("VideoId", selectedFeaturedVideo.getVideoId());
                startActivity(reportVideo);
                break;
        }
        return true;
    }

    //Implementation of VideoClickListener
    @Override
    public void onResultClick(Video video) {
        //Open Video Player for song
        Intent videoPlayerIntent = new Intent(this, VideoPlayer.class);
        videoPlayerIntent.putExtra("VIDEO", video);
        videoPlayerIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
        //videoPlayerIntent.putExtra("CREDIT", userCredits);
        startActivity(videoPlayerIntent);

    }

    @Override
    public void onOverflowClick(Video video, View view) {
        selectedFeaturedVideo = video;
        showOverflow(view);
    }

}


