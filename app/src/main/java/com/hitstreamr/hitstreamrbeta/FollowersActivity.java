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

public class FollowersActivity extends AppCompatActivity {

    private static final String TAG = "FollowersActivity";
    DatabaseReference myFollowersRef;
    ArrayList<String> followersUsers;
    String userId, type;
    private RecyclerView recyclerView_Followers;
    private FollowersAdapter adapter_Followers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Followers");
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
        recyclerView_Followers = findViewById(R.id.recyclerView_followers);

        followersUsers = new ArrayList<>();
        setupAdapter();
        myFollowersRef = FirebaseDatabase.getInstance().getReference("followers")
                .child(userId);


        myFollowersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot each : dataSnapshot.getChildren()) {
                    followersUsers.add(each.getKey());
                }
                setupAdapter();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    /**
     * Set up the adapter for the followers list.
     */
    private void setupAdapter() {
        Log.e(TAG, "Entered Setup recycler view " + userId + " " +followersUsers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_Followers.setLayoutManager(layoutManager);
        adapter_Followers = new FollowersAdapter(this, followersUsers, getIntent());
        recyclerView_Followers.setAdapter(adapter_Followers);
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
