package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Library extends AppCompatActivity {

    private static final String TAG = "LibraryActivity";

    private String accountType;
    private FirebaseUser current_user;
    private ExpandableRelativeLayout expandableLayout_history, expandableLayout_watchLater, expandableLayout_playlists;
    private BottomNavigationView bottomNavView;
    private RecyclerView recyclerView_watchLater, recyclerView_playlists;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bookRef = db.collection("Videos");

    private BookAdapter bookAdapter_watchLater, bookAdapter_playlists;

    private Long WatchListCount;
    private ArrayList<String> WatchLaterList;

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

        WatchLaterList = new ArrayList<>();

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

        getWatchLaterList();
    }

    Query query;

    private void setUpRecyclerView(){

        Log.e(TAG, "Entered recycler view" + WatchLaterList.get(0));

        for (int i = 0; i < WatchLaterList.size(); i++) {
            query = bookRef.whereEqualTo("videoId", WatchLaterList.get(i));
        }
        Log.e(TAG, "query 1:" +bookRef.whereEqualTo("title", "ABC Song").getClass());
        /*Log.e(TAG, "query 2:" +query.getClass());
        Log.e(TAG, "query 3:" +query.getFirestore().toString());
        Log.e(TAG, "query 4:" +query.getFirestore());*/

        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        Log.e(TAG, "query 4:" +options.getSnapshots());

        bookAdapter_watchLater = new BookAdapter(options);
        recyclerView_watchLater = (RecyclerView) findViewById(R.id.recyclerView_watchLater);
        recyclerView_watchLater.setHasFixedSize(true);
        //recyclerView_watchLater.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView_watchLater.setItemAnimator(new DefaultItemAnimator());
        //bookAdapter_watchLater.notifyDataSetChanged();
        recyclerView_watchLater.setAdapter(bookAdapter_watchLater);
        //bookAdapter_watchLater.startListening();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        bookAdapter_watchLater.startListening();
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        bookAdapter_watchLater.stopListening();
    }

    /**
     * Drop Down Menu - History
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
        expandableLayout_playlists = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout_playlists);
        expandableLayout_playlists.toggle(); // toggle expand and collapse
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

    //Object temp;

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
                            for(DataSnapshot each : dataSnapshot.getChildren()){
                                //temp = each.getValue().toString();
                                WatchLaterList.add(String.valueOf(each.getKey()));
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
}
