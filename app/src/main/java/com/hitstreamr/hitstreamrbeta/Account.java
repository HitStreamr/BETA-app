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
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

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
    private EditText EditTextEmail;
    private EditText EditTextBio;
    private EditText EditTextFirstName;
    private EditText EditTextLastName;
    private EditText EditTextAddress;
    private EditText EditTextCity;
    private EditText EditTextZip;
    private EditText EditTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        //Layout
        ArtistInfoLayout = findViewById(R.id.privateInfo);

        //EditText
        EditTextUsername = findViewById(R.id.Username);
        EditTextEmail = findViewById(R.id.Email);
        EditTextBio = findViewById(R.id.Bio);
        EditTextFirstName = findViewById(R.id.FirstName);
        EditTextLastName = findViewById(R.id.LastName);
        EditTextAddress = findViewById(R.id.Address);
        EditTextCity = findViewById(R.id.City);
        EditTextZip = findViewById(R.id.Zip);
        EditTextPhone = findViewById(R.id.Phone);

        //Spinners

        type = getIntent().getStringExtra("TYPE");
        if (type.equals(getString(R.string.type_artist))) {

            ArtistInfoLayout.setVisibility(View.VISIBLE);

            if (userID != null) {
                myRef = database.getReference("ArtistAccounts").child(userID);
            }
        } else if (type.equals(getString(R.string.type_basic))) {
            ArtistInfoLayout.setVisibility(View.GONE);
            if (userID != null) {
                myRef = database.getReference("BasicAccounts").child(userID);
            }
        } else {

        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (type.equals(getString(R.string.type_artist))) {
                    showArtistData(dataSnapshot);

                } else if (type.equals(getString(R.string.type_basic))) {
                    showBasicData(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showArtistData(DataSnapshot dataSnapshot) {

        String firstname = dataSnapshot.child("firstname").getValue(String.class);
        String lastname = dataSnapshot.child("lastname").getValue(String.class);
        String email = dataSnapshot.child("email").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);
        String address = dataSnapshot.child("address").getValue(String.class);
        String city = dataSnapshot.child("city").getValue(String.class);
        String state = dataSnapshot.child("state").getValue(String.class);
        String zip = dataSnapshot.child("zip").getValue(String.class);
        String country = dataSnapshot.child("country").getValue(String.class);
        String phone = dataSnapshot.child("phone").getValue(String.class);

        ArtistUser artist = new ArtistUser(firstname, lastname, email, username, address, city, state, country, phone, zip);

        EditTextFirstName.setText(artist.getFirstname());
        EditTextLastName.setText(artist.getLastname());
        EditTextEmail.setText(artist.getEmail());
        EditTextUsername.setText(artist.getUsername());
        EditTextAddress.setText(artist.getAddress());
        EditTextCity.setText(artist.getCity());
        EditTextZip.setText(artist.getZip());
        EditTextPhone.setText(artist.getPhone());


        Log.e("TAG", "Username" + artist.getUsername());
    }

    public void showBasicData(DataSnapshot dataSnapshot) {

        String email = dataSnapshot.child("email").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);

        User basic = new User(username, email);

        EditTextEmail.setText(basic.getEmail());
        EditTextUsername.setText(basic.getUsername());

    }
}