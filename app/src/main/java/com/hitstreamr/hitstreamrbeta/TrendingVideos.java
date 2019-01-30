package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TrendingVideos extends AppCompatActivity {
    private RecyclerView recyclerView_TrendingNow;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser current_user;
    private CollectionReference trendingNowRef = db.collection("Videos");
    private TrendingVideosAdapter adapter;
    private ItemClickListener mlistner;
    private String CreditVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_videos);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if (!Strings.isNullOrEmpty(currentCredit)) {

                            CreditVal = currentCredit;
                        } else
                            CreditVal = "0";
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mlistner = selectedVideo -> {
            Intent videoPlayerIntent = new Intent(TrendingVideos.this, VideoPlayer.class);
            videoPlayerIntent.putExtra("VIDEO", selectedVideo);
            videoPlayerIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
            videoPlayerIntent.putExtra("CREDIT", CreditVal);
            startActivity(videoPlayerIntent);
        };

        recyclerView_TrendingNow = findViewById(R.id.RecyclerView_TrendingNow);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Query query = trendingNowRef.orderBy("views", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        adapter = new TrendingVideosAdapter(options, mlistner);
        recyclerView_TrendingNow.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_TrendingNow.setLayoutManager(layoutManager);
        recyclerView_TrendingNow.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public interface ItemClickListener {
        void onTrendingMoreClick(Video selectedVideo);

    }
}

