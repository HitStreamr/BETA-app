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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hitstreamr.hitstreamrbeta.Library;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.TrendingAdapter;
import com.hitstreamr.hitstreamrbeta.TrendingVideos;
import com.hitstreamr.hitstreamrbeta.Video;
import com.hitstreamr.hitstreamrbeta.VideoPlayer;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser current_user;
    private CollectionReference trendingNowRef = db.collection("Videos");
    private TrendingAdapter adapter;
    private RecyclerView recyclerView_Trending;
    private Button trendingMoreBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView_Trending = view.findViewById(R.id.trendingNowRCV);
        trendingMoreBtn = view.findViewById(R.id.trendingMore);
        setupRecyclerView();

        trendingMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trending = new Intent(getContext(), TrendingVideos.class);
                startActivity(trending);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }


    private void setupRecyclerView(){
        Query query = trendingNowRef.orderBy("views", Query.Direction.DESCENDING).limit(10);

        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        adapter = new TrendingAdapter(options);
        recyclerView_Trending.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_Trending.setLayoutManager(layoutManager);
        recyclerView_Trending.setAdapter(adapter);

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
}
