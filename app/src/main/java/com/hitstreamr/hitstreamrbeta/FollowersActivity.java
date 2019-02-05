package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowersActivity extends AppCompatActivity {

    private static final String TAG = "FollowersActivity";
    DatabaseReference myFollowersRef;
    ArrayList<String> followersUsers;
    String userId;
    private RecyclerView recyclerView_Followers;
    private  FollowersAdapter adapter_Followers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        userId = getIntent().getStringExtra("USER");
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

    private void setupAdapter(){
        Log.e(TAG, "Entered Setup recycler view " + userId + " " +followersUsers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_Followers.setLayoutManager(layoutManager);
        adapter_Followers = new FollowersAdapter(this, followersUsers);
        recyclerView_Followers.setAdapter(adapter_Followers);
    }



}
