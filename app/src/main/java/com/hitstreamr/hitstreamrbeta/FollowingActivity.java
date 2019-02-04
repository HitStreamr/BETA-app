package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowingActivity extends AppCompatActivity {
    private static final String TAG = "FollowingActivity";
    DatabaseReference myFollowingRef;
    ArrayList<String> followingUsers;
    String userId;
    private RecyclerView recyclerView_Following;
    private  FollowingAdapter adapter_Following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        userId = getIntent().getStringExtra("USER");
        recyclerView_Following = findViewById(R.id.recyclerView_following);

        followingUsers = new ArrayList<>();
        setupAdapter();
        myFollowingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(userId);

        myFollowingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot each : dataSnapshot.getChildren()) {
                    followingUsers.add(each.getKey());
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
        Log.e(TAG, "Entered Setup recycler view " + userId + " " +followingUsers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_Following.setLayoutManager(layoutManager);
        adapter_Following = new FollowingAdapter(this, followingUsers);
        recyclerView_Following.setAdapter(adapter_Following);
    }


}
