package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MoreWatchAgain extends AppCompatActivity {

    private FirebaseUser current_user;
    private ItemClickListener mListener;
    private String credit_value;
    private Video onClickedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_watch_again);

        // Get the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Get the current user's credit value
        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if(!Strings.isNullOrEmpty(currentCredit)){
                            credit_value = currentCredit;
                        }
                        else
                            credit_value = "0";
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        mListener = new ItemClickListener() {
            @Override
            public void onResultClick(Video video) {
                Intent videoPlayerIntent = new Intent(getApplicationContext(), VideoPlayer.class);
                videoPlayerIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                videoPlayerIntent.putExtra("VIDEO", video);
                videoPlayerIntent.putExtra("CREDIT", credit_value);
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

        // Populate the videos to watch again
        moreWatchAgain();
    }

    /**
     * Show a full list of videos to watch again.
     */
    private void moreWatchAgain() {
        RecyclerView recyclerView = findViewById(R.id.moreWatchAgainRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> videoIdList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("History").child(current_user.getUid())
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                videoIdList.add(ds.child("videoId").getValue(String.class));
                            }

                            List<Video> videoList = new ArrayList<>();
                            MoreWatchAgainAdapter moreWatchAgainAdapter =
                                    new MoreWatchAgainAdapter(videoList, getApplicationContext(), getIntent(), mListener);

                            for (String videoId : videoIdList) {
                                FirebaseFirestore.getInstance().collection("Videos")
                                        .document(videoId)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
//                                            if ((documentSnapshot.get("delete").equals("N")) &&
//                                                    (documentSnapshot.get("privacy")
//                                                            .equals(getResources().getStringArray(R.array.Privacy)[0]))) {
                                                videoList.add(documentSnapshot.toObject(Video.class));
                                                moreWatchAgainAdapter.notifyDataSetChanged();
                                                recyclerView.setAdapter(moreWatchAgainAdapter);
//                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Video Item Click Listener
     */
    public interface ItemClickListener {
        void onResultClick(Video video);
        void onOverflowClick(Video video, View view);
    }
}
