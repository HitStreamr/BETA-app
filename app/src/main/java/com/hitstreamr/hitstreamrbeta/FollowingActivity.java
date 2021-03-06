package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingActivity extends AppCompatActivity {
    private static final String TAG = "FollowingActivity";
    DatabaseReference myFollowingRef;
    ArrayList<String> followingUsers;
    String userId, type;
    private RecyclerView recyclerView_Following;
    private  FollowingAdapter adapter_Following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Following");
        toolbar.setTitleTextColor(0xFFFFFFFF);

        // Set toolbar profile picture to always be the current user
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user.getPhotoUrl() != null) {
            CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }

        userId = getIntent().getStringExtra("USER");
        type = getIntent().getStringExtra("TYPE");
        recyclerView_Following = findViewById(R.id.recyclerView_following);

        //followingUsers = new ArrayList<>();
        setupAdapter();
        myFollowingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(userId);

        myFollowingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingUsers = new ArrayList<>();
                for(DataSnapshot each : dataSnapshot.getChildren()) {
                    followingUsers.add(each.getKey());
                }
                if(followingUsers.size()>0) {
                    setupAdapter();
                    //adapter_Following.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    /**
     * Set up the adapter for the following list.
     */
    private void setupAdapter() {
        Log.e(TAG, "Entered Setup recycler view " + userId + " " +followingUsers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_Following.setLayoutManager(layoutManager);
        adapter_Following = new FollowingAdapter(this, followingUsers, getIntent());
        recyclerView_Following.setAdapter(adapter_Following);
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
