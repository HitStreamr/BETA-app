package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.firestore.Query;
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
    //private RecyclerView recyclerView_watchLater, recyclerView_playlists;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bookRef = db.collection("Videos");
    Query query;

    private BookAdapter bookAdapter_watchLater, bookAdapter_playlists;

    private Long WatchListCount;
    private ArrayList<String> WatchLaterList;
    private ArrayList<Video> Watch;

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

        listView_watchLater = findViewById(R.id.listView_watchLater);

        WatchLaterList = new ArrayList<>();
        Watch = new ArrayList<>();

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

    private void setUpRecyclerView() {
        Log.e(TAG, "Entered recycler view" + WatchLaterList.get(0));
        bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (WatchLaterList.contains(document.getId())) {
                        Log.e(TAG, "entered    :::" + document.getId() + document.getData());
                        Watch.add(document.toObject(Video.class));
                    }
                }
                Log.e(TAG, "objects :::" + Watch);
                call();
            }
        });
    }

    private void call(){
        bookAdapter_watchLater = new BookAdapter(this, R.layout.watch_later_results, Watch);
        listView_watchLater.setAdapter(bookAdapter_watchLater);
        bookAdapter_watchLater.notifyDataSetChanged();
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
                            for (DataSnapshot each : dataSnapshot.getChildren()) {
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
