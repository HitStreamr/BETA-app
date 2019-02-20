package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.ArrayList;
import java.util.List;

public class ArtistsToWatch extends AppCompatActivity {

    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_to_watch);
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        showMoreArtistsToWatch();
    }

    /**
     * Show a full list of artists to watch.
     */
    private void showMoreArtistsToWatch() {
        RecyclerView recyclerView = findViewById(R.id.moreArtistsToWatchRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("ArtistsLikes").orderBy("likes", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> artistFirestoreList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    String temp = documentSnapshot.getId();
                    // Sorted based on highest likes
                    artistFirestoreList.add(temp);
                }

                // Query to Firebase
                List<ArtistUser> artistList = new ArrayList<>();
                ArtistsToWatchAdapter artistsToWatchAdapter = new ArtistsToWatchAdapter(artistList,
                        getApplicationContext(), getIntent());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
                for (String artistID : artistFirestoreList) {
                    databaseReference.child(artistID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ArtistUser artist_user = dataSnapshot.getValue(ArtistUser.class);
                                artistList.add(artist_user);
                                artistsToWatchAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(artistsToWatchAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
