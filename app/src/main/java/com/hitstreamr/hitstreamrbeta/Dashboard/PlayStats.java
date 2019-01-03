package com.hitstreamr.hitstreamrbeta.Dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.R;

public class PlayStats extends Fragment {

    private FirebaseUser current_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_playstats, container, false);

        // Get the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        getComments(rootView);
        getFaves(rootView);
        getFollows(rootView);
        getReposts(rootView);
        getTotalViews(rootView);
        getWeeklyViews(rootView);

        return rootView;
    }

    /**
     * Get the total number of comments.
     * @param view view
     */
    private void getComments(View view) {
        TextView comments = view.findViewById(R.id.playStats_comments);
        comments.setText("0");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    if (doc.get("userId").equals(current_user.getUid())) {
                        String videoId = doc.getId();

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference("Comments").child(videoId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int commentCount = (int) dataSnapshot.getChildrenCount();
                                int temp = Integer.parseInt(comments.getText().toString());
                                int result = commentCount + temp;
                                comments.setText(result + "");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        firebaseDatabase.getReference("CommentReplies").child(videoId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int commentCount = (int) dataSnapshot.getChildrenCount();
                                int temp = Integer.parseInt(comments.getText().toString());
                                int result = commentCount + temp;
                                comments.setText(result + "");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Get the total number of favorites.
     * @param view view
     */
    private void getFaves(View view) {
        TextView faves = view.findViewById(R.id.playStats_faves);
        faves.setText("0");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    if (doc.get("userId").equals(current_user.getUid())) {
                        String videoId = doc.getId();

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference("VideoLikes").child(videoId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int faveCount = (int) dataSnapshot.getChildrenCount();
                                int temp = Integer.parseInt(faves.getText().toString());
                                int result = faveCount + temp;
                                faves.setText(result + "");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Get the total number of follows.
     * @param view view
     */
    private void getFollows(View view) {
        TextView follows = view.findViewById(R.id.playStats_follows);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("followers").child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String followCount = dataSnapshot.getChildrenCount() + "";
                follows.setText(followCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get the total number of reposts.
     * @param view view
     */
    private void getReposts(View view) {
        TextView reposts = view.findViewById(R.id.playStats_reposts);
        reposts.setText("0");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    if (doc.get("userId").equals(current_user.getUid())) {
                        String videoId = doc.getId();

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference("Repost").child(videoId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int repostCount = (int) dataSnapshot.getChildrenCount();
                                int temp = Integer.parseInt(reposts.getText().toString());
                                int result = repostCount + temp;
                                reposts.setText(result + "");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Get the total number of views.
     * @param view view
     */
    private void getTotalViews(View view) {
        TextView totalViews = view.findViewById(R.id.playStats_totalViews);
        totalViews.setText("0");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    if (doc.get("userId").equals(current_user.getUid())) {
                        if (doc.get("views") != null) {
                            int viewCount = Integer.parseInt(doc.get("views").toString());
                            int temp = Integer.parseInt(totalViews.getText().toString());
                            int result = viewCount + temp;
                            totalViews.setText(result + "");
                        }
                    }
                }
            }
        });
    }

    /**
     * Get the total number of weekly views.
     * @param view view
     */
    private void getWeeklyViews(View view) {
        TextView weeklyViews = view.findViewById(R.id.playStats_weeklyViews);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("ArtistsViews").document(current_user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            weeklyViews.setText(documentSnapshot.get("views").toString());
                        }
                    }
                });
    }
}
