package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

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
    private String accountType;
    private Video onClickedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_videos);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        // Set toolbar profile picture to always be the current user
        CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
        if (current_user.getPhotoUrl() != null) {
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }

        // onClick listener for the toolbar's profile image
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profilePage = new Intent(getApplicationContext(), Profile.class);
                profilePage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(profilePage);
            }
        });

        playlistName = findViewById(R.id.playlist_Name);
        playlist = new Playlist();
        temp = getIntent().getParcelableExtra("PlaylistVideos");
        playlistName.setText(temp.getPlaylistname());
        getSupportActionBar().setTitle(temp.getPlaylistname());
        videosCollectionRef = db.collection("Videos");

        getUserType();

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

        getUserType();

        mlistner = new ItemClickListener() {
            @Override
            public void onPlaylistVideoClick(Video selectVideo) {
                Intent videoPlayerIntent = new Intent(PlaylistVideosActivity.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectVideo);
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
        //Log.e(TAG, "Playlist Content activity" + getIntent().getStringExtra("PlaylistName") + getIntent().getExtras().getParcelableArrayList("PlaylistVideos"));
        recyclerView_PlaylistVideos = findViewById(R.id.recyclerView_singlePlaylistContent);
        getPlayVideos();
    }

    private void getUserType() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {
            //type = getIntent().getStringExtra("TYPE");

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                accountType = "BasicAccounts";
            } else if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                accountType = "ArtistAccounts";
            } else {
                accountType = "LabelAccounts";
            }
        }
        Log.e(TAG, "account type selected :"+accountType);
    }



    private void getPlayVideos() {
        //Log.e(TAG, "Entered onsuceess" +Play);
        ArrayList<Task<QuerySnapshot>> queryy = new ArrayList<>();
        for (int j = 0; j < temp.getPlayVideoIds().size(); j++) {
            queryy.add(videosCollectionRef.whereEqualTo("videoId", temp.getPlayVideoIds().get(j))
                    .whereEqualTo("delete", "N")
                    .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0])
                    .get());
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
        void onOverflowClick(Video video, View view);
    }

    /**
     * Handles back button on toolbar.
     *
     * @return true if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
