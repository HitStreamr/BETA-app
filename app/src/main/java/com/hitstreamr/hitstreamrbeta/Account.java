package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

public class Account extends AppCompatActivity {
    private static final String TAG = "AccountActivity";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;

    private String type;

    private LinearLayout ArtistInfoLayout;

    //EditText
    private EditText EditTextUsername;


    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
    }

    @Override
    public void onStop() {
        super.onStop();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ArtistInfoLayout = findViewById(R.id.privateInfo);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {
            //check that type exists and set it.
            type = getIntent().getStringExtra("TYPE");
            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                ArtistInfoLayout.setVisibility(View.VISIBLE);

            } else if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                ArtistInfoLayout.setVisibility(View.GONE);

            }
        }

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        if (userID != null) {
            myRef = database.getReference("ArtistAccounts").child(userID);
            //myRef = database.getReference("ArtistAccounts");
        }
        else{
        }

        EditTextUsername = findViewById(R.id.Username);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showData(DataSnapshot dataSnapshot) {
        /*for (DataSnapshot d : dataSnapshot.getChildren()) {
            ArtistUser artist = new ArtistUser();
            artist.setFirstname(d.child(userID).getValue(ArtistUser.class).getFirstname());
            artist.setLastname(d.child(userID).getValue(ArtistUser.class).getLastname());
            artist.setEmail(d.child(userID).getValue(ArtistUser.class).getEmail());
            artist.setAddress(d.child(userID).getValue(ArtistUser.class).getAddress());
            artist.setCity(d.child(userID).getValue(ArtistUser.class).getCity());
            artist.setCountry(d.child(userID).getValue(ArtistUser.class).getCountry());
            artist.setPhone(d.child(userID).getValue(ArtistUser.class).getPhone());
            artist.setZip(d.child(userID).getValue(ArtistUser.class).getZip());*/

        ArtistUser artist = new ArtistUser();

        artist.setFirstname(dataSnapshot.getValue(ArtistUser.class).getFirstname());
        artist.setLastname(dataSnapshot.getValue(ArtistUser.class).getLastname());
        artist.setEmail(dataSnapshot.getValue(ArtistUser.class).getEmail());
        artist.setAddress(dataSnapshot.getValue(ArtistUser.class).getAddress());
        artist.setCity(dataSnapshot.getValue(ArtistUser.class).getCity());
        artist.setCountry(dataSnapshot.getValue(ArtistUser.class).getCountry());
        artist.setPhone(dataSnapshot.getValue(ArtistUser.class).getPhone());
        artist.setZip(dataSnapshot.getValue(ArtistUser.class).getZip());

        EditTextUsername.setText(artist.getUsername());
        //}

        Log.e("TAG", "Username: " + artist.getUsername());
        Log.e("TAG", "Uid: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


}
