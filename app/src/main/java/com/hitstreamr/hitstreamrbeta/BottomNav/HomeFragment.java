package com.hitstreamr.hitstreamrbeta.BottomNav;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.FeaturedVideoResultAdapter;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.Video;
import com.hitstreamr.hitstreamrbeta.VideoClickListener;
import com.hitstreamr.hitstreamrbeta.VideoPlayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HomeFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{
    private static final String TAG = "HomeFragment";
    private static final int MAX_PRELOAD = 10 ;
    //Featured Artist
    RecyclerView featuredArtistRCV;
    Button featuredMore;
    Video selectedFeaturedVideo;
    Query featuredVideosQuery;
    Task<QuerySnapshot> featuredResults;
    ArrayList<Video> featuredVideos;
    FirestoreRecyclerOptions<Video> featuredArtistOptions;
    private FeaturedVideoResultAdapter  resultAdapter;
    private final int FEATURED_LOAD = 5;
    RequestManager glideRequests;
    String userCredits;
    String type;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        featuredVideosSetup(view);
    }

    public void featuredVideosSetup(View view){
        /**
         * Featured Artist Start
         */
        Bundle bundle = this.getArguments();
        if (bundle != null){
            //userCredits = bundle.getString("CREDITS", "0");
            type = bundle.getString("TYPE", "basic");
        }

        featuredVideos = new ArrayList<>();

        glideRequests = Glide.with(this);

        //Recycler View
        featuredArtistRCV = view.findViewById(R.id.featuredVideosRCV);

        //"More" Button
        featuredMore = view.findViewById(R.id.featuredMore);
        featuredMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Video Player for song
                Intent videoPlayerIntent = new Intent(getActivity(), FeaturedVideoRCV.class);
                videoPlayerIntent.putExtra("TYPE", getActivity().getIntent().getExtras().getString("TYPE"));
                //videoPlayerIntent.putExtra("CREDIT", userCredits);
                startActivity(videoPlayerIntent);
            }
        });

        featuredVideosQuery = FirebaseFirestore.getInstance()
                .collection("FeaturedVideo")
                .orderBy("views", Query.Direction.DESCENDING )
                .whereEqualTo("privacy","Public (everyone can see)")
                .limit(FEATURED_LOAD);

        featuredResults = featuredVideosQuery.get();

        //See if successful
        featuredResults.addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot docs : queryDocumentSnapshots) {
                if (docs.exists()) {
                    featuredVideos.add(docs.toObject(Video.class));
                    Log.e(TAG,docs.toObject(Video.class).toString());
                } else {
                    Log.e(TAG, "Document " + docs.toString() + "does not exist");
                }
            }

            VideoClickListener mListener = new VideoClickListener() {
                @Override
                public void onResultClick(Video video) {
                    //Open Video Player for song
                    Intent videoPlayerIntent = new Intent(getActivity(), VideoPlayer.class);
                    videoPlayerIntent.putExtra("VIDEO", video);
                    videoPlayerIntent.putExtra("TYPE", getActivity().getIntent().getExtras().getString("TYPE"));
                    //videoPlayerIntent.putExtra("CREDIT", userCredits);
                    startActivity(videoPlayerIntent);

                }

                @Override
                public void onOverflowClick(Video video, View v) {
                    selectedFeaturedVideo = video;
                    showOverflow(v);
                }
            };

            resultAdapter = new FeaturedVideoResultAdapter(featuredVideos, mListener, glideRequests);
            RecyclerViewPreloader<Video> preloader = new RecyclerViewPreloader<>( glideRequests, resultAdapter, resultAdapter, MAX_PRELOAD /*maxPreload*/);
            resultAdapter.notifyDataSetChanged();
            featuredArtistRCV.addOnScrollListener(preloader);
            featuredArtistRCV.setAdapter(resultAdapter);
            featuredArtistRCV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        });

        /*
         *  Featured Artist End
         */
    }

    public void showOverflow(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.video_overflow_menu);
        popupMenu.show();
    }
    /**
     * Timestamp utility functions
     */
    public static Date dateFromUTC(Date date){
        return new Date(date.getTime() + Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public static Date dateToUTC(Date date){
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fave_result:
                return true;
            case R.id.addLibrary_result:
                return true;
        }
        return false;
    }

}
