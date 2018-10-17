package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";

    private FirebaseUser current_user;
    private CircleImageView circleImageView;
    private String accountType;

    private Toolbar toolbar;

    private String userClicked;
    private String userUserID;

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myNewRef;

    /**
     * Set up and initialize layouts and variables
     *
     * @param savedInstanceState state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        database = FirebaseDatabase.getInstance();
        //myRef.setValue("Hello, World!");

        getUserType();
        getUsername();

        if (userClicked.equals("")) {
            getCurrentProfile();
        }else{
            getUserClickedUserId();
            //getSearchUser();
        }


    }

    private void getCurrentProfile(){
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        //Log.e(TAG, "current user is :::"+current_user.getPhotoUrl());
        // Show username on toolbar
        myRef = FirebaseDatabase.getInstance().getReference().child(accountType)
                .child(current_user.getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                getSupportActionBar().setTitle(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);

        // Profile Picture
        if (current_user.getPhotoUrl() != null) {
            circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            CircleImageView profileImageView = findViewById(R.id.profileImage);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
            Glide.with(getApplicationContext()).load(photoURL).into(profileImageView);
        }
    }

    private void getSearchProfile(){
            myRef = FirebaseDatabase.getInstance().getReference().child(accountType)
                    .child(userUserID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    getSupportActionBar().setTitle(username);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            myRef.addListenerForSingleValueEvent(eventListener);

           /* // Profile Picture
            if (current_user.getPhotoUrl() != null) {
                circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
                circleImageView.setVisibility(View.VISIBLE);
                CircleImageView profileImageView = findViewById(R.id.profileImage);
                Uri photoURL = current_user.getPhotoUrl();
                Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
                Glide.with(getApplicationContext()).load(photoURL).into(profileImageView);
            }*/
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

    private void getUsername() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("artistUsername") && getIntent().getStringExtra("artistUsername") != null) {
            userClicked = getIntent().getStringExtra("artistUsername");
            Log.e(TAG, "username clicked is:::"+userClicked);
        } else if (extras.containsKey("basicUsername") && getIntent().getStringExtra("basicUsername") != null) {
            userClicked = getIntent().getStringExtra("basicUsername");
        } else {
            userClicked = "";
        }
    }


    private void getUserClickedUserId() {

        myNewRef = FirebaseDatabase.getInstance().getReference().child("UsernameUserId").child(userClicked);
        myNewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userUserID = dataSnapshot.child("tempUserId").getValue(String.class);
                Log.e(TAG, "userid is :::"+userUserID);
                Log.e(TAG, "data snapshot values :::" +dataSnapshot);
                getSearchProfile();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //myNewRef.addListenerForSingleValueEvent(eventListener);
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
}
