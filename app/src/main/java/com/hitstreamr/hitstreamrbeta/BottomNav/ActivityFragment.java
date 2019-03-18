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
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.hitstreamr.hitstreamrbeta.AddToPlaylist;
import com.hitstreamr.hitstreamrbeta.FeedData;
import com.hitstreamr.hitstreamrbeta.Library;
import com.hitstreamr.hitstreamrbeta.PostVideoFeedAdapter;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.RecentArtistsUploadedAdapter;
import com.hitstreamr.hitstreamrbeta.ReportVideoPopup;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;
import com.hitstreamr.hitstreamrbeta.Video;
import com.hitstreamr.hitstreamrbeta.VideoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ActivityFragment extends Fragment {
    private static final String TAG = "ActivityFragment";

    private DatabaseReference database, myRef, myFeedRef, myVideosRef;
    private FirebaseFirestore db;
    private FirebaseUser current_user;
    DatabaseReference myFollowingRef, myArtistRef, myLikesRef, myRepostRef;
    ArrayList<String> followingUsers, artistfollowing, eachArtistVideos, videoTypeFeed, videoFeed, typeFeed, likesCount, userFeed;
    ArrayList<Object> artistVideos;
    ArrayList<FeedData> feeddocs;
    DataSnapshot likesDatasnapshot, repostDatasnapshot, videoDatasnapshot;
    private CollectionReference videosCollectionRef, feedDataCollectionRef;
    private ArrayList<Video> UserVideos;
    //private  ArrayList<Video> myListCollection;
    private RecyclerView activityRecyclerView;
    private PostVideoFeedAdapter adapter;
    private ItemClickListener mlistner;
    private String CreditVal;
    private Video onClickedVideo;

    SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference("ArtistVideo");
        myFeedRef = FirebaseDatabase.getInstance().getReference("FeedData");
        myFollowingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(current_user.getUid());
        myLikesRef = FirebaseDatabase.getInstance().getReference("VideoLikes");
        myRepostRef = FirebaseDatabase.getInstance().getReference("Repost");
        myArtistRef = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
        videosCollectionRef = db.collection("Videos");
        feedDataCollectionRef = db.collection("FeedData");
        myVideosRef = FirebaseDatabase.getInstance().getReference("Videos");
        followingUsers = new ArrayList<>();
        feeddocs = new ArrayList<>();
        artistfollowing = new ArrayList<>();
        artistVideos = new ArrayList<>();
        videoTypeFeed = new ArrayList<>();
        eachArtistVideos = new ArrayList<>();
        videoFeed = new ArrayList<>();
        typeFeed = new ArrayList<>();
        UserVideos = new ArrayList<>();
        likesCount = new ArrayList<>();
        userFeed = new ArrayList<>();
        activityRecyclerView = view.findViewById(R.id.activityRecyclerView);
        Log.e(TAG, "Entered On create activity");
        //getArtistUsers();
        getFollowing();
        getVideoLikes();
        getVideoReposted();
        //getVideos();

        showRecentArtists(view);

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

        mlistner = new ItemClickListener() {
            @Override
            public void onActivityClick(Video selectedVideo) {
                Log.e(TAG,"entered video trenf=ding" + selectedVideo.getVideoId());
                Intent videoPlayerIntent = new Intent(getContext(), VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", selectedVideo);
                videoPlayerIntent.putExtra("TYPE", getActivity().getIntent().getExtras().getString("TYPE"));
                videoPlayerIntent.putExtra("CREDIT", CreditVal);
                startActivity(videoPlayerIntent);
            }

            @Override
            public void onOverflowClick(Video video, View view) {
                onClickedVideo = video;
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.video_menu_pop_up);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addToWatchLater_videoMenu:
                                FirebaseDatabase.getInstance()
                                        .getReference("WatchLater")
                                        .child(current_user.getUid())
                                        .child(onClickedVideo.getVideoId())
                                        .child("VideoId")
                                        .setValue(onClickedVideo.getVideoId())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Video has been added to Watch Later",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;

                            case R.id.addToPlaylist_videoMenu:
                                Intent playlistIntent = new Intent(getApplicationContext(), AddToPlaylist.class);
                                playlistIntent.putExtra("VIDEO", onClickedVideo);
                                playlistIntent.putExtra("TYPE", getActivity().getIntent().getExtras().getString("TYPE"));
                                startActivity(playlistIntent);
                                break;

                            case R.id.report_videoMenu:
                                Intent reportVideo = new Intent(getApplicationContext(), ReportVideoPopup.class);
                                reportVideo.putExtra("VideoId", onClickedVideo.getVideoId());
                                startActivity(reportVideo);
                                break;
                        }
                        return true;
                    }
                });
            }
        };
    }

    /**
     * Get the most recent artists that uploads music videos.
     * @param view view
     */
    public void showRecentArtists(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.artistsActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> followingList = new ArrayList<>();
        List<ArtistUser> artistList = new ArrayList<>();
        RecentArtistsUploadedAdapter adapter = new RecentArtistsUploadedAdapter(artistList, getContext(), getActivity().getIntent());

        FirebaseDatabase.getInstance().getReference("following").child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                followingList.add(ds.getKey());
                            }

                            Log.d("FOLLOWING", followingList.toString());

                            FirebaseFirestore.getInstance().collection("RecentArtistsUploaded")
                                    .orderBy("upload_time", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                        if (followingList.contains(ds.getId())) {
                                            // adapter stuff
                                            FirebaseDatabase.getInstance().getReference("ArtistAccounts")
                                                    .child(ds.getId()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        ArtistUser artistUser = dataSnapshot.getValue(ArtistUser.class);
                                                        artistList.add(artistUser);
                                                        adapter.notifyDataSetChanged();
                                                        recyclerView.setAdapter(adapter);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void getVideoLikes() {
        myLikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likesDatasnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void getVideoReposted() {
        myRepostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                repostDatasnapshot = dataSnapshot;

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void getFollowing() {
        Log.e(TAG, "Entered getfollowing method");
        myFollowingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "Entered getfollowing onDataChange");
                if (dataSnapshot.exists()) {
                    Log.e(TAG, "Entered datasnapshot exists");
                    for (DataSnapshot each : dataSnapshot.getChildren()) {
                        //if (artistfollowing.contains(each.getKey())) {
                        followingUsers.add(each.getKey());
                        //}
                    }
                }
                Log.e(TAG, "following arraylist values" + followingUsers);
                //getArtistVideos();
                getFeedData();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void getFeedData() {
        Query queryRef = feedDataCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING);
        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (followingUsers.size() > 0) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.e(TAG, "userId is :" + document.get("userId"));
                        if (document.get("userId") != null) {
                            if (followingUsers.contains(document.get("userId"))) {
                                feeddocs.add(document.toObject(FeedData.class));
                                if (!(videoTypeFeed.contains(document.get("videoId") + " " + document.get("type").toString()))) {
                                    userFeed.add(document.get("userId").toString());
                                    videoTypeFeed.add(document.get("videoId").toString() + " " + document.get("type").toString());
                                    videoFeed.add(document.get("videoId").toString());
                                    typeFeed.add(document.get("type").toString());
                                }
                            }
                        }
                    }
                }
                getVideoFirestore();
                Log.e(TAG, "feed object is :" + videoFeed + videoTypeFeed + videoFeed.size() + videoTypeFeed.size());
            }
        });
    }

    public void getVideos() {
        myVideosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoDatasnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*public void getVideoFirestore() {
        if (videoFeed.size() > 0) {
            for (int i = 0; i < videoFeed.size(); i++) {
                videoDatasnapshot.child(videoFeed.get(i)).getValue();
                UserVideos.add(videoDatasnapshot.child(videoFeed.get(i)).getValue(Video.class));
                long VideoLikesCount = likesDatasnapshot.child(videoFeed.get(i)).getChildrenCount();
                String temp = formatt(VideoLikesCount);
                likesCount.add(temp);
                Log.e(TAG, "Video Count likes : " + likesCount);
            }
        }
        callToAdapter();
    }*/

    public void getVideoFirestore() {
        Query queryRef = videosCollectionRef
                .whereEqualTo("delete", "N")
                .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0]);
        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (videoFeed.size() > 0) {
                    for (int i = 0; i < videoFeed.size(); i++) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //if (videoFeed.contains(document.getId())) {
                            if ((videoFeed.get(i)).contains(document.getId())) {
                                UserVideos.add(document.toObject(Video.class));
                                Log.e(TAG, "video Ids are " + document.getId());

                                long VideoLikesCount = likesDatasnapshot.child(videoFeed.get(i)).getChildrenCount();
                                String temp;
                                if(VideoLikesCount == 0){
                                    temp = formatt(VideoLikesCount);
                                }
                                else {
                                    temp = formatt(VideoLikesCount-1);
                                }
                                likesCount.add(temp);
                                Log.e(TAG, "Video Count likes : " + likesCount);
                            }
                        }
                    }
                    callToAdapter();
                }
                Log.e(TAG, "Video objects are" + UserVideos);

            }
        });
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatt(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatt(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatt(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    private void callToAdapter() {

        activityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new PostVideoFeedAdapter(UserVideos, typeFeed, likesCount, userFeed, mlistner);
        adapter.notifyDataSetChanged();
        activityRecyclerView.setAdapter(adapter);
    }

    public interface ItemClickListener {
        void onActivityClick(Video selectedVideo);
        void onOverflowClick(Video video, View view);
    }


}
