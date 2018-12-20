package com.hitstreamr.hitstreamrbeta.BottomNav;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.ArtistsToWatch;
import com.hitstreamr.hitstreamrbeta.HomeFragmentTopArtistsAdapter;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ARTISTS TO WATCH SECTION - START */

        // Populate the Artists To Watch recycler view
        showArtistsToWatch(view);

        Button showMoreArtists = view.findViewById(R.id.showMoreArtists);
        showMoreArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moreArtists = new Intent(getContext(), ArtistsToWatch.class);
                moreArtists.putExtra("TYPE", getActivity().getIntent().getStringExtra("TYPE"));
                startActivity(moreArtists);
            }
        });

        /* ARTISTS TO WATCH SECTION - END */
    }

    /**
     * Load popular artists.
     * @param view view
     */
    private void showArtistsToWatch(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.artistWatchRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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
                if (getActivity() != null) {

                    List<ArtistUser> artistList = new ArrayList<>(artistFirestoreList.size());
                    HomeFragmentTopArtistsAdapter homeFragmentTopArtistsAdapter =
                            new HomeFragmentTopArtistsAdapter(artistList, getContext(), getActivity().getIntent());

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            String artistID = dataSnapshot.getKey();

                            // Initialize as many as it will hold
                            while (artistList.size() < artistFirestoreList.size()) {
                                artistList.add(dataSnapshot.getValue(ArtistUser.class));
                            }

                            // Replace the indexes with appropriate values
                            // The indexes will match, and thus sorted
                            if (artistFirestoreList.contains(artistID)) {
                                int index = artistFirestoreList.indexOf(artistID);
                                ArtistUser artistUser = dataSnapshot.getValue(ArtistUser.class);
                                artistList.remove(index);
                                artistList.add(index, artistUser);
                                homeFragmentTopArtistsAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(homeFragmentTopArtistsAdapter);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
