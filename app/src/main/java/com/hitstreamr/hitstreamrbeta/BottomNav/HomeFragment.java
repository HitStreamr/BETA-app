package com.hitstreamr.hitstreamrbeta.BottomNav;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.ArtistsToWatch;
import com.hitstreamr.hitstreamrbeta.HomeFragmentPopularPeopleAdapter;
import com.hitstreamr.hitstreamrbeta.HomeFragmentTopArtistsAdapter;
import com.hitstreamr.hitstreamrbeta.MorePopularPeople;
import com.hitstreamr.hitstreamrbeta.NewReleaseAdapter;
import com.hitstreamr.hitstreamrbeta.NewReleases;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.FeaturedVideoResultAdapter;
import com.hitstreamr.hitstreamrbeta.TrendingAdapter;
import com.hitstreamr.hitstreamrbeta.TrendingVideos;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;
import com.hitstreamr.hitstreamrbeta.Video;
import com.hitstreamr.hitstreamrbeta.VideoClickListener;
import com.hitstreamr.hitstreamrbeta.VideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

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
    String userCredits;
    String userID;
    String type;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser current_user;
    private CollectionReference trendingNowRef = db.collection("Videos");
    private TrendingAdapter adapter;
    private RecyclerView recyclerView_Trending;
    private Button trendingMoreBtn, newReleaseBtn;
    public final String TAG = "HomePage";
    private CollectionReference newReleaseRef = db.collection("Videos");
    RequestManager glideRequests;
    private NewReleaseAdapter newReleaseadapter;
    private RecyclerView recyclerView_newRelease;
    private ArrayList<String> userGenreList;
    private ArrayList<Video> UserGenreVideos;
    private ArrayList<Video> UserNewVideos;
    private ItemClickListener mListener;
    private TrendingItemClickListener tlistner;
    private String CreditVal;

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        featuredVideosSetup(view);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        userGenreList = new ArrayList<>();
        UserGenreVideos = new ArrayList<>();
        UserNewVideos = new ArrayList<>();

        recyclerView_Trending = view.findViewById(R.id.trendingNowRCV);
        trendingMoreBtn = view.findViewById(R.id.trendingMore);

        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });

        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if(!Strings.isNullOrEmpty(currentCredit)){
                            CreditVal = currentCredit;
                        }
                        else
                            CreditVal = "0";
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        tlistner = selectedVideo -> {
            Log.e(TAG,"entered video trenf=ding" + selectedVideo.getVideoId());
            Intent videoPlayerIntent = new Intent(getContext(), VideoPlayer.class);
            videoPlayerIntent.putExtra("VIDEO", selectedVideo);
            videoPlayerIntent.putExtra("TYPE", getActivity().getIntent().getExtras().getString("TYPE"));
            videoPlayerIntent.putExtra("CREDIT", CreditVal);
            startActivity(videoPlayerIntent);
        };

        trendingMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trending = new Intent(getContext(), TrendingVideos.class);
                startActivity(trending);
            }
        });

        // Populate the Artists To Watch recycler view
        showArtistsToWatch(view);

        // Populate the Popular People recycler view
        loadPopularPeople(view);

        // More top artists
        Button showMoreArtists = view.findViewById(R.id.showMoreArtists);
        showMoreArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moreArtists = new Intent(getContext(), ArtistsToWatch.class);
                moreArtists.putExtra("TYPE", getActivity().getIntent().getStringExtra("TYPE"));
                startActivity(moreArtists);
            }
        });

        // More popular people
        Button showMorePopularPeople = view.findViewById(R.id.morePopularPeople);
        showMorePopularPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent morePopularPeople = new Intent(getContext(), MorePopularPeople.class);
                morePopularPeople.putExtra("TYPE", getActivity().getIntent().getStringExtra("TYPE"));
                startActivity(morePopularPeople);
            }
        });

        glideRequests = Glide.with(this);
        recyclerView_newRelease = (RecyclerView) view.findViewById(R.id.freshReleasesRCV);
        newReleaseBtn = view.findViewById(R.id.newReleaseMore);
        getUserGenre();

        newReleaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newReleaseVideo = new Intent(getContext(), NewReleases.class);
                newReleaseVideo.putExtra("TYPE", getArguments().getString("TYPE"));
                startActivity(newReleaseVideo);
            }
        });

        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if(!Strings.isNullOrEmpty(currentCredit)){

                            CreditVal = currentCredit;
                        }
                        else
                            CreditVal = "0";

                        // Log.e(TAG, "Profile credit val inside change" + CreditVal);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mListener = new ItemClickListener() {
            @Override
            public void onResultClick(Video video) {
                Intent videoPlayerIntent = new Intent(getActivity(), VideoPlayer.class);
                videoPlayerIntent.putExtra("TYPE", getArguments().getString("TYPE"));
                videoPlayerIntent.putExtra("VIDEO", video);
                videoPlayerIntent.putExtra("CREDIT", CreditVal);
                startActivity(videoPlayerIntent);
            }

            @Override
            public void onOverflowClick(Video title, View v) { showOverflow(v);
            }
        };

        setupRecyclerView();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    public void featuredVideosSetup(View view){
        /**
         * Featured Artist Start
         */
        Bundle bundle = this.getArguments();
        if (bundle != null){
            userCredits = bundle.getString("CREDITS", "0");
            type = bundle.getString("TYPE", "basic");
            userID = bundle.getString("USER_ID");
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
                videoPlayerIntent.putExtra("CREDIT", userCredits);
                startActivity(videoPlayerIntent);
            }
        });

        featuredVideosQuery = FirebaseFirestore.getInstance()
                .collection("FeaturedVideo")
                //.orderBy("privacy")
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
                    videoPlayerIntent.putExtra("CREDIT", userCredits);
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

    /**
     * Set up the recycler view for trending videos.
     */
    private void setupRecyclerView() {
        Query query = trendingNowRef.orderBy("views", Query.Direction.DESCENDING).limit(10);

        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        adapter = new TrendingAdapter(options, tlistner);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fave_result:
                break;
            case R.id.addLibrary_result:
                break;

        }
        return true;
    }

    public interface ItemClickListener {
        void onResultClick(Video title);
        void onOverflowClick(Video title, View v);
    }

    public void showOverflow(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.video_overflow_menu);
        popupMenu.show();
    }

    /**
     * Load top artists.
     * @param view view
     */
    private void showArtistsToWatch(View view) {
        RecyclerView recyclerView_topArtists = view.findViewById(R.id.artistWatchRCV);
        recyclerView_topArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("ArtistsLikes").orderBy("likes", Query.Direction.DESCENDING)
                .limit(20).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

                    List<ArtistUser> artistList = new ArrayList<>();
                    HomeFragmentTopArtistsAdapter homeFragmentTopArtistsAdapter =
                            new HomeFragmentTopArtistsAdapter(artistList, getContext(), getActivity().getIntent());

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
                    for (String artistID : artistFirestoreList) {
                        databaseReference.child(artistID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    ArtistUser artist_user = dataSnapshot.getValue(ArtistUser.class);
                                    artistList.add(artist_user);
                                    homeFragmentTopArtistsAdapter.notifyDataSetChanged();
                                    recyclerView_topArtists.setAdapter(homeFragmentTopArtistsAdapter);
                                }
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
     * Load popular people.
     * @param view view
     */
    private void loadPopularPeople(View view) {
        RecyclerView recyclerView_popularPeople = view.findViewById(R.id.popularUsersRCV);
        recyclerView_popularPeople.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("PopularPeople").orderBy("followers", Query.Direction.DESCENDING)
                .limit(15).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> userFirestoreList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    userFirestoreList.add(documentSnapshot.getId());
                }

                // Query to Firebase
                if (getActivity() != null) {

                    List<User> userList = new ArrayList<>();
                    HomeFragmentPopularPeopleAdapter homeFragmentPopularPeopleAdapter =
                            new HomeFragmentPopularPeopleAdapter(userList, getContext(), getActivity().getIntent());

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BasicAccounts");
                    for (String userID : userFirestoreList) {
                        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User basic_user = dataSnapshot.getValue(User.class);
                                    userList.add(basic_user);
                                    homeFragmentPopularPeopleAdapter.notifyDataSetChanged();
                                    recyclerView_popularPeople.setAdapter(homeFragmentPopularPeopleAdapter);
                                }
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
     * Get the user's preferred genres.
     */
    public void getUserGenre() {
        FirebaseDatabase.getInstance().getReference("SelectedGenres")
                .child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for(DataSnapshot each : dataSnapshot.getChildren()){
                                userGenreList.add(String.valueOf(each.getValue()));

                            }
                            // Log.e(TAG, "Home genre List : " + userGenreList);
                        }
                        getFreshReleases();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    public void getFreshReleases() {

        Query queryRef = newReleaseRef.orderBy("timestamp", Query.Direction.DESCENDING);

        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (userGenreList.size() > 0) {
                       for (int itr = 0; itr < userGenreList.size(); itr++) {
                            if (userGenreList.get(itr).contains( document.get("genre").toString().toLowerCase())) {
                                UserGenreVideos.add(document.toObject(Video.class));
                            } else if (userGenreList.get(itr).contains(document.get("subGenre").toString().toLowerCase())) {
                                UserGenreVideos.add(document.toObject(Video.class));
                            }
                        }
                    }
                    else
                    {
                        UserGenreVideos.add(document.toObject(Video.class));
                    }


                }
                callToAdapter();
            }
        });


    }

    private void callToAdapter(){

        if(UserGenreVideos.size() > 10){
            UserNewVideos = new ArrayList (UserGenreVideos.subList(0,10));
        }
        else
        {
            UserNewVideos = new ArrayList (UserGenreVideos);
        }

        recyclerView_newRelease.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newReleaseadapter = new NewReleaseAdapter( R.layout.thumbnail_categoried_video, UserNewVideos, mListener, glideRequests);
        newReleaseadapter.notifyDataSetChanged();
        recyclerView_newRelease.setAdapter(newReleaseadapter);

    }

    public interface TrendingItemClickListener {
        void onTrendingVideoClick(Video selectedVideo);
    }

}
