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
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hitstreamr.hitstreamrbeta.PostVideoFeedAdapter;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.Video;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActivityFragment extends Fragment {
    private static final String TAG = "ActivityFragment";

    FirebaseDatabase database;
    private FirebaseFirestore db;
    private FirebaseUser current_user;
    DatabaseReference myFollowingRef, myArtistRef;
    ArrayList<String> followingUsers, artistfollowing, eachArtistVideos;
    ArrayList<Object> artistVideos;
    private CollectionReference videosCollectionRef;
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
        myFollowingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(current_user.getUid());
        myArtistRef = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
        videosCollectionRef = db.collection("Videos");
        followingUsers = new ArrayList<>();
        artistfollowing = new ArrayList<>();
        artistVideos = new ArrayList<>();
        eachArtistVideos = new ArrayList<>();
        UserVideos = new ArrayList<>();
        //myListCollection = new ArrayList<Object>();
        activityRecyclerView = view.findViewById(R.id.activityRecyclerView);
        Log.e(TAG, "Entered On create activity");
        getArtistUsers();
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
                        if (artistfollowing.contains(each.getKey())) {
                            followingUsers.add(each.getKey());
                        }
                    }
                }
                Log.e(TAG, "following arraylist values" + followingUsers);
                getArtistVideos();
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
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /*private void getPostResults(){
        com.google.firebase.firestore.Query allVideos = db.collection("Videos");

        for (int i = 0; i < followingUsers.size(); i++) {
            allVideos = allVideos.whereEqualTo("UserId" , followingUsers.get(i));
    }

        Task<QuerySnapshot> allVideosTask = allVideos.whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0]).get();

        //allResultsRetrieved is only successful, when all are successful
        Task<List<QuerySnapshot>> allVideosRetrieved = Tasks.whenAllSuccess(allVideosTask);

        allVideosRetrieved.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {

            }
        });/*

    }*/


    private void getArtistVideos() {
        db.collection("ArtistVideo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (followingUsers.contains(document.getId())) {
                                    //Log.e(TAG, "getArtistVideos: "+document.getId() + "id is :"+document.get("videos"));
                                    eachArtistVideos.add(document.get("videos").toString());
                                    //myListCollection.add(eachArtistVideos);
                                }
                            }
                            //artistVideos.add(eachArtistVideos.indexOf(0));
                        }
                        Log.e(TAG, "getArtistVideos: " + eachArtistVideos.get(1) + eachArtistVideos.size());
                       /* ArrayList<String> stringCollection = new ArrayList<String>();
                        for (int i = 0; i < myListCollection.size(); ++i) {
                            stringCollection.addAll(myListCollection.contains());
                        }*/
                        getVideoFirestore();
                    }
                });
    }

    public void getVideoFirestore() {
        Query queryRef = videosCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING);
        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (eachArtistVideos.size() > 0) {
                    for (int itr = 0; itr < eachArtistVideos.size(); itr++) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (eachArtistVideos.get(itr).contains(document.getId())) {
                                UserVideos.add(document.toObject(Video.class));
                            }
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
        adapter = new PostVideoFeedAdapter(UserVideos);
        adapter.notifyDataSetChanged();
        activityRecyclerView.setAdapter(adapter);
    }




}
