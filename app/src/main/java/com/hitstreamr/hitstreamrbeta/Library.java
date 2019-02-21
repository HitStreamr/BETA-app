package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;


import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Library extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "LibraryActivity";
    private final String HOME = "home";
    private final String DISCOVER = "discover";
    private final String ACTIVITY = "activity";

    private String accountType;
    private ExpandableRelativeLayout expandableLayout_history, expandableLayout_watchLater, expandableLayout_playlists;
    private BottomNavigationView bottomNavView;
    private RecyclerView recyclerView_watchLater, recyclerView_playlists, recyclerView_history;
    private Button playlistBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser current_user;
    private CollectionReference bookRef = db.collection("Videos");

    private BookAdapter bookAdapter_watchLater;
    private WatchPlaylistAdapter playlistAdapter_playlists;
    private HistoryAdapter historyAdapter_history;

    private ArrayList<Video> WatchList;
    private ArrayList<String> WatchLaterList;
    private ArrayList<Playlist> Play;
    private ArrayList<Video> HistoryVideos;
    private ArrayList<String> HistoryList;

    private ItemClickListener mlistner;
    private Video vid;

    private String CreditVal;

    private CollectionReference videosCollectionRef;
    private DataSnapshot videosDatasnapshot;
    private DatabaseReference HistoryRef;
    private Video onClickedVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        videosCollectionRef = db.collection("Videos");
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        HistoryRef = FirebaseDatabase.getInstance().getReference("History").child(current_user.getUid());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setTitle("Library");

        bottomNavView = findViewById(R.id.bottomNav);
        bottomNavView.setOnNavigationItemSelectedListener(this);
        bottomNavView.setSelectedItemId(R.id.library);

        //listView_watchLater = findViewById(R.id.listView_watchLater);

        recyclerView_watchLater = findViewById(R.id.recyclerView_watchLater);
        recyclerView_playlists = findViewById(R.id.recyclerView_playlists);
        recyclerView_history = findViewById(R.id.recyclerView_history);

        playlistBtn = findViewById(R.id.expandableButton_playlists);
        playlistBtn.setVisibility(View.GONE);

        HistoryList = new ArrayList<>();
        HistoryVideos = new ArrayList<>();

        WatchLaterList = new ArrayList<>();
        WatchList = new ArrayList<>();
        Play = new ArrayList<>();

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
        Log.e(TAG, "Watch later credit val " + CreditVal);

        getUserType();

        // Profile Picture
        if (current_user.getPhotoUrl() != null) {
            CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }

        mlistner = new ItemClickListener() {
            @Override
            public void onResultClick(Video selectedVideo) {
                Intent videoPlayerIntent = new Intent(Library.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectedVideo);
                videoPlayerIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
                videoPlayerIntent.putExtra("CREDIT", CreditVal);
                startActivity(videoPlayerIntent);
            }

            @Override
            public void onPlaylistClick(Playlist selectedPlaylist) {
                Log.e(TAG, "on Playlist click" + selectedPlaylist.getPlayVideos());
                Intent PlaylistIntent = new Intent(Library.this, PlaylistVideosActivity.class);
                PlaylistIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
                //PlaylistIntent.putExtra("Account", accountType);
                Log.e(TAG, "playlist value " + selectedPlaylist.getPlayVideoIds());

                PlaylistIntent.putExtra("PlaylistVideos", selectedPlaylist);
                startActivity(PlaylistIntent);
            }

            @Override
            public void onHistoryClick(Video historySelected) {
                Intent videoPlayerIntent = new Intent(Library.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", historySelected);
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
        getWatchLaterList();
        getPlaylistsList();
        getHistoryList();
    }

    private void setUpRecyclerView() {
        if (WatchLaterList.size() > 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView_watchLater.setLayoutManager(layoutManager);
            bookAdapter_watchLater = new BookAdapter(this, WatchList, mlistner);
            recyclerView_watchLater.setAdapter(bookAdapter_watchLater);
        }
    }

    private void setUpPlaylistRecyclerView() {
        Log.e(TAG, "Entered setup playlist recycler view");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_playlists.setLayoutManager(layoutManager);
        playlistAdapter_playlists = new WatchPlaylistAdapter(this, Play, mlistner);
        recyclerView_playlists.setAdapter(playlistAdapter_playlists);
    }

    /**
     * Drop Down Menu - History
     *
     * @param view view
     */
    public void expandableButton_history(View view) {
        expandableLayout_history = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout_history);
        expandableLayout_history.toggle(); // toggle expand and collapse
    }

    /**
     * Drop Down - Watch Later
     *
     * @param view view
     */
    public void expandableButton_watchLater(View view) {
        expandableLayout_watchLater = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout_watchLater);
        expandableLayout_watchLater.toggle(); // toggle expand and collapse
    }

    /**
     * Handles back button on toolbar
     *
     * @return true if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Get the account type of the current user
     */
    private void getUserType() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {
            //type = getIntent().getStringExtra("TYPE");

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                accountType = "BasicAccounts";
            } else if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                // sth
                accountType = "ArtistAccounts";
            } else {
                accountType = "LabelAccounts";
            }
        }
    }


    private void getHistoryList() {
        HistoryRef.orderByChild("timestamp").limitToFirst(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eachVideoObject : dataSnapshot.getChildren()) {
                    HistoryList.add(eachVideoObject.child("videoId").getValue().toString());
                }
                Log.e(TAG, "History oredered and limited" + HistoryList);
                getHistoryVideos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void getHistoryVideos() {
        Log.e(TAG, "Entered getHistory Videos" + HistoryList);
        ArrayList<Task<QuerySnapshot>> queryy = new ArrayList<>();
        for (int i = 0; i < HistoryList.size(); i++) {
            queryy.add(videosCollectionRef.whereEqualTo("videoId", HistoryList.get(i))
                    .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0])
                    .whereEqualTo("delete", "N")
                    .get());
        }
        Task<List<QuerySnapshot>> task = Tasks.whenAllSuccess(queryy);
        task.addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                for (QuerySnapshot document : task.getResult()) {
                    //Log.e(TAG, "aaaaaaaaaaaaa " + document);
                    for (DocumentSnapshot docume : document.getDocuments()) {
                        //Log.e(TAG, "bbbbbbbbbbbbbbb" + docume.toObject(Video.class).getVideoId());
                        HistoryVideos.add(docume.toObject(Video.class));
                    }
                }
                Log.e(TAG, "History Video List : " + HistoryVideos);
                setupHistoryRecyclerView();
            }
        });
    }

    private void setupHistoryRecyclerView() {
        if (HistoryVideos.size() > 0) {
            Log.e(TAG, "Entered setup history" + HistoryVideos);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView_history.setLayoutManager(layoutManager);
            historyAdapter_history = new HistoryAdapter(this, HistoryVideos, mlistner, getIntent());
            recyclerView_history.setAdapter(historyAdapter_history);
        }
    }


    /**
     * RecyclerView Test
     */
    private void getWatchLaterList() {

        FirebaseDatabase.getInstance().getReference("WatchLater")
                .child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot each : dataSnapshot.getChildren()) {
                                WatchLaterList.add(each.getKey());
                            }
                            Log.e(TAG, "Watch Later List : " + WatchLaterList);
                        }
                        getWatchLaterVideos();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getWatchLaterVideos() {
        if (WatchLaterList.size() > 0) {
            for (int i = 0; i < WatchLaterList.size(); i++) {
                Query query = videosCollectionRef.whereEqualTo("videoId", WatchLaterList.get(i))
                        .whereEqualTo("delete", "N")
                        .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0]);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WatchList.add(document.toObject(Video.class));
                            //Log.e(TAG, "Watch Later Video List : " + document.toObject(Video.class).getThumbnailUrl());
                        }
                    }
                });
            }
        }
        setUpRecyclerView();
    }

    private void getPlaylistsList() {
        FirebaseDatabase.getInstance().getReference("PlaylistVideos")
                .child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot each : dataSnapshot.getChildren()) {
                                //playDatasnapshot = dataSnapshot;
                                Playlist p = new Playlist();
                                p.setPlaylistname(String.valueOf(each.getKey()));
                                Log.e(TAG, "each children" + each.getChildren());
                                ArrayList<String> a = new ArrayList<>();
                                for (DataSnapshot eachplaylist : each.getChildren()) {
                                    a.add(eachplaylist.getValue(String.class));
                                }
                                p.setPlayVideoIds(a);
                                Play.add(p);
                            }
                            //Log.e(TAG, "Playlist List 1 : " + Play.get(0).getPlaylistname() + " " + Play.get(0).getPlayVideos() + " " + Play.get(0).getPlayVideoIds());
                            //Log.e(TAG, "Playlist List 2 : " + Play.get(1).getPlayVideoIds() + " " + Play.get(1).getPlaylistname());
                            //Log.e(TAG, "Playlist List 2 : " + Play.get(2).getPlayVideoIds() + " " +Play.get(2).getPlaylistname());

                        }
                        if (Play.size() > 0) {
                            playlistBtn.setVisibility(View.VISIBLE);
                            getaaaPlayVideos();

                        }
                        //setUpPlaylistRecyclerView();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getaaaPlayVideos() {
        //Log.e(TAG, "Entered onsuceess" +Play);
        ArrayList<Task<QuerySnapshot>> queryy = new ArrayList<>();
        for (int j = 0; j < Play.size(); j++) {
            queryy.add(videosCollectionRef.whereEqualTo("videoId", Play.get(j).getPlayVideoIds().get(0))
                    .whereEqualTo("delete", "N")
                    .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0])
                    .get());
        }

        Task<List<QuerySnapshot>> task = Tasks.whenAllSuccess(queryy);
        task.addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                int x = 0;
                ArrayList<String> bb = new ArrayList<>();
                for (QuerySnapshot document : task.getResult()) {
                    for (DocumentSnapshot docume : document.getDocuments()) {
                        //Log.e(TAG, "11111111111111 " + docume.toObject(Video.class).getVideoId());
                        bb.add(docume.toObject(Video.class).getVideoId());
                        Play.get(x).setPlayThumbnails(docume.toObject(Video.class).getUrl());
                    }
                    Play.get(x).setPlayVideoIds(bb);
                    Log.e(TAG, "Entered onsuceess" + Play.get(x).getPlayThumbnails());
                    x++;
                }
                setUpPlaylistRecyclerView();
            }
        });
    }

    /**
     * Handles fragment items
     *
     * @param item menu item
     * @return true to show fragments
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction;
        Bundle bundle;
        switch (item.getItemId()) {
            case R.id.home:
                Toast.makeText(Library.this, "Library", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                homeIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                homeIntent.putExtra("OPTIONAL_FRAG", HOME);
                startActivity(homeIntent);
                break;
            case R.id.discover:
                Toast.makeText(Library.this, "Library", Toast.LENGTH_SHORT).show();
                Intent discoverIntent = new Intent(getApplicationContext(), MainActivity.class);
                discoverIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                discoverIntent.putExtra("OPTIONAL_FRAG", DISCOVER);
                startActivity(discoverIntent);
                break;
            case R.id.activity:
                Toast.makeText(Library.this, "Library", Toast.LENGTH_SHORT).show();
                Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
                activityIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                activityIntent.putExtra("OPTIONAL_FRAG", ACTIVITY);
                startActivity(activityIntent);
                break;
            case R.id.library:
                break;
        }
        return true;
    }

public interface ItemClickListener {
    void onResultClick(Video selectedVideo);

    void onPlaylistClick(Playlist selectedPlaylist);

    void onHistoryClick(Video historySelected);

    void onOverflowClick(Video video, View view);
}

}
