package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Library extends AppCompatActivity {

    private static final String TAG = "LibraryActivity";

    private String accountType;
    private FirebaseUser current_user;
    private ExpandableRelativeLayout expandableLayout_history, expandableLayout_watchLater, expandableLayout_playlists;
    private BottomNavigationView bottomNavView;
    private ListView listView_watchLater;
    private RecyclerView recyclerView_watchLater, recyclerView_playlists;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bookRef = db.collection("Videos");

    private BookAdapter bookAdapter_watchLater;
    private WatchPlaylistAdapter playlistAdapter_playlists;

    private Long WatchListCount;
    private ArrayList<Video> WatchLaterList;
    private ArrayList<Video> Watch;
    private ArrayList<Playlist> Play;

    private ItemClickListener mlistner;
    private Video vid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setTitle("Library");

        bottomNavView = findViewById(R.id.bottomNav);

        //listView_watchLater = findViewById(R.id.listView_watchLater);

        recyclerView_watchLater = findViewById(R.id.recyclerView_watchLater);
        recyclerView_playlists = findViewById(R.id.recyclerView_playlists);

        WatchLaterList = new ArrayList<>();
        Watch = new ArrayList<>();
        Play = new ArrayList<>();

        getUserType();
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Profile Picture
        if (current_user.getPhotoUrl() != null) {
            CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            //ImageView profileImageView = findViewById(R.id.profileImage);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
            //Glide.with(getApplicationContext()).load(photoURL).into(profileImageView);
        }

        mlistner = new ItemClickListener() {
            @Override
            public void onResultClick(Video selectedVideo) {
                Intent videoPlayerIntent = new Intent(Library.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectedVideo);
                startActivity(videoPlayerIntent);
            }
        };



        getWatchLaterList();
        getPlaylistsList();
    }

    private void setUpRecyclerView() {
        Log.e(TAG, "Entered recycler view" + WatchLaterList.get(0));
        /*bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (WatchLaterList.contains(document.getId())) {
                        //Log.e(TAG, "entered    :::" + document.getId() + document.getData());
                        Watch.add(document.toObject(Video.class));
                    }
                }
                //Log.e(TAG, "objects :::" + Watch);
                call();
            }
        });*/

        call();
    }

    private void call(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_watchLater.setLayoutManager(layoutManager);
        bookAdapter_watchLater = new BookAdapter(this,WatchLaterList, mlistner);
        recyclerView_watchLater.setAdapter(bookAdapter_watchLater);
    }

    private void setUpPlaylistRecyclerView() {
        Log.e(TAG, "Entered setup playlist recycler view");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_playlists.setLayoutManager(layoutManager);
        playlistAdapter_playlists = new WatchPlaylistAdapter(Play);
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
     * Drop Down - Playlist
     *
     * @param view view
     */
    public void expandableButton_playlists(View view) {
        //expandableLayout_playlists = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout_playlists);
        //expandableLayout_playlists.toggle(); // toggle expand and collapse
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
                                //temp = each.getValue().toString();
                                WatchLaterList.add(each.getValue(Video.class));
                            }
                            Log.e(TAG, "Watch Later List : " + WatchLaterList);
                        }
                        setUpRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getPlaylistsList() {

        FirebaseDatabase.getInstance().getReference("PlaylistVideos")
                .child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot each : dataSnapshot.getChildren()) {
                                Playlist p = new Playlist();
                                p.setPlaylistname(String.valueOf(each.getKey()));
                                Play.add(p);
                                Log.e(TAG, "each children"+each.getChildren());
                                ArrayList<Video> a = new ArrayList<>();
                                for(DataSnapshot eachplaylist : each.getChildren()){
                                    a.add(eachplaylist.getValue(Video.class));
                                    //eachplaylist.getValue();
                                    Log.e(TAG, "videos in playlist"+eachplaylist.getValue());
                                }
                                p.setPlayVideos(a);
                            }
                            Log.e(TAG, "Playlist List 1 : " + Play.get(0).getPlaylistname() + " " + Play.get(0).getPlayVideos());
                            Log.e(TAG, "Playlist List 2 : " + Play.get(1).getPlaylistname() + " " + Play.get(1).getPlayVideos());
                        }
                        setUpPlaylistRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public interface ItemClickListener {
        void onResultClick(Video selectedVideo);
    }
}
