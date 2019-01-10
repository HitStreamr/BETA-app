package com.hitstreamr.hitstreamrbeta.BottomNav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.FeedData;
import com.hitstreamr.hitstreamrbeta.PostVideoFeedAdapter;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.Video;
import java.util.ArrayList;


public class ActivityFragment extends Fragment {
    private static final String TAG = "ActivityFragment";

    private DatabaseReference database, myRef, myFeedRef;
    private FirebaseFirestore db;
    private FirebaseUser current_user;
    DatabaseReference myFollowingRef, myArtistRef;
    ArrayList<String> followingUsers, artistfollowing, eachArtistVideos, videoTypeFeed, videoFeed, typeFeed;
    ArrayList<Object> artistVideos;
    ArrayList<FeedData> feeddocs;
    private CollectionReference videosCollectionRef, feedDataCollectionRef;
    private ArrayList<Video> UserVideos;
    //private  ArrayList<Video> myListCollection;
    private RecyclerView activityRecyclerView;
    private PostVideoFeedAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference("ArtistVideo");
        myFeedRef = FirebaseDatabase.getInstance().getReference("FeedData");
        myFollowingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(current_user.getUid());
        myArtistRef = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
        videosCollectionRef = db.collection("Videos");
        feedDataCollectionRef = db.collection("FeedData");
        followingUsers = new ArrayList<>();
        feeddocs = new ArrayList<>();
        artistfollowing = new ArrayList<>();
        artistVideos = new ArrayList<>();
        videoTypeFeed = new ArrayList<>();
        eachArtistVideos = new ArrayList<>();
        videoFeed = new ArrayList<>();
        typeFeed = new ArrayList<>();
        UserVideos = new ArrayList<>();
        //myListCollection = new ArrayList<Object>();
        activityRecyclerView = view.findViewById(R.id.activityRecyclerView);
        Log.e(TAG, "Entered On create activity");
        //getArtistUsers();
        getFollowing();

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

    private void getArtistUsers() {
        Log.e(TAG, "Entered getArtistUsers method");
        myArtistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "Entered getArtistUsers onDataChange");
                if (dataSnapshot.exists()) {
                    Log.e(TAG, "Entered getArtistUsers datasnapshot exists");
                    for (DataSnapshot each : dataSnapshot.getChildren()) {
                        Log.e(TAG, "Entered getArtistUsers for loop" + each);

                        artistfollowing.add(each.getKey());
                    }
                }
                Log.e(TAG, "getArtistUsers arraylist values" + artistfollowing);
                getFollowing();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void getFeedData(){
        Query queryRef = feedDataCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING);
        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (followingUsers.size() > 0) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.e(TAG, "userId is :" +document.get("userId"));
                        if(document.get("userId") != null) {
                            if (followingUsers.contains(document.get("userId"))) {
                                feeddocs.add(document.toObject(FeedData.class));

                                if(!(videoTypeFeed.contains(document.get("videoId") + " " + document.get("type").toString()) )){
                                    videoTypeFeed.add(document.get("videoId").toString()+ " " + document.get("type").toString());
                                    videoFeed.add(document.get("videoId").toString());
                                    typeFeed.add(document.get("type").toString());
                                }
                               /* else {
                                    if(document.get("type").equals("post")) {
                                        videoFeed.add(document.get("videoId").toString());
                                        typeFeed.add(document.get("type").toString());
                                    }
                                    else{
                                        int inde = videoFeed.indexOf(document.get("videoId"));
                                        if(typeFeed.get(inde).equals(document.get("type"))) {
                                    }
                                        *//*
                                    for(int i=0; i<videoFeed.size(); i++){
                                        if(!(videoFeed.get(i).equals(document.get("videoId")))){
                                            if(!(typeFeed.get(i).equals(document.get("videoId")))) {
                                                videoFeed.add(document.get("videoId").toString());
                                                typeFeed.add(document.get("type").toString());
                                            }*//*
                                        }
                                    }

                                }*/
                            }
                        }
                    }
                }
                getVideoFirestore();
               Log.e(TAG, "feed object is :"+ videoFeed + videoTypeFeed + videoFeed.size() + videoTypeFeed.size());
            }
        });
    }

    private void getArtistVideos() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    if (followingUsers.contains(snapshot.getKey())) {
                        Log.e(TAG, "22222" +snapshot.getKey());
                        for( DataSnapshot childSnapshot : snapshot.getChildren() )
                            eachArtistVideos.add(childSnapshot.getKey());
                    }
                }
                Log.e(TAG, "getArtistVideos: " + eachArtistVideos + eachArtistVideos.size());
                getVideoFirestore();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }


        });
    }

    public void getVideoFirestore() {
        Query queryRef = videosCollectionRef;
        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (videoFeed.size() > 0) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (videoFeed.contains(document.getId())) {
                            UserVideos.add(document.toObject(Video.class));
                            Log.e(TAG,"video Ids are "+document.getId());
                        }
                    }
                    callToAdapter();
                }
                Log.e(TAG,"Video objects are" +UserVideos);

            }
        });
    }

    private void callToAdapter(){

        activityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new PostVideoFeedAdapter(UserVideos, typeFeed);
        adapter.notifyDataSetChanged();
        activityRecyclerView.setAdapter(adapter);
    }


}
