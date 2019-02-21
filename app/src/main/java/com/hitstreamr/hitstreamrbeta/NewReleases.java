package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;

import java.util.ArrayList;

public class NewReleases extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser current_user;
    private CollectionReference newReleaseRef = db.collection("Videos");
    RequestManager glideRequests;
    private NewReleasesListAdapter newReleaseadapter;
    private RecyclerView recyclerView_newRelease;
    private ArrayList<String> userGenreList;
    private ArrayList<Video> UserGenreVideos;
    private ItemClickListener mListener;
    private String CreditVal;
    public final String TAG = "NewReleasePage";
    private Video onClickedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_releases);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView_newRelease = (RecyclerView) findViewById(R.id.RecyclerView_NewRelease);

        userGenreList = new ArrayList<>();
        UserGenreVideos = new ArrayList<>();

        glideRequests = Glide.with(this);

        getUserGenre();

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

        mListener = new ItemClickListener() {
            @Override
            public void onResultClick(Video video) {
                Intent videoPlayerIntent = new Intent(NewReleases.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                videoPlayerIntent.putExtra("VIDEO", video);
                videoPlayerIntent.putExtra("CREDIT", CreditVal);

                startActivity(videoPlayerIntent);
            }

            @Override
            public void onOverflowClick(Video video, View v) {
                onClickedVideo = video;
                showOverflow(v);
            }
        };
    }

    /**
     * Video menu popup options.
     * @param item item
     * @return true
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
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
                playlistIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
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

    public interface ItemClickListener {
        void onResultClick(Video title);
        void onOverflowClick(Video title, View v);
    }

    public void showOverflow(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.video_menu_pop_up);
        popupMenu.show();
    }

    public void getUserGenre(){
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

    public void getFreshReleases(){

        Query queryRef = newReleaseRef.whereEqualTo("delete", "N").orderBy("timestamp", Query.Direction.DESCENDING);

        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if ((document.get("privacy").equals(getResources().getStringArray(R.array.Privacy)[0]))) {
                        if (userGenreList.size() > 0) {
                            for (int itr = 0; itr < userGenreList.size(); itr++) {
                                String userGenre = userGenreList.get(itr);
                                userGenre = userGenre.replaceAll("_"," ");

                                if (userGenre.contains(document.get("genre").toString().toLowerCase())) {
                                    UserGenreVideos.add(document.toObject(Video.class));
                                    break;
                                } else if (userGenre.contains(document.get("subGenre").toString().toLowerCase())) {
                                    UserGenreVideos.add(document.toObject(Video.class));
                                    break;
                                }
                            }
                        } else {
                            UserGenreVideos.add(document.toObject(Video.class));
                        }
                    }
                }
                callToAdapter();
            }
        });


    }

    private void callToAdapter(){
        recyclerView_newRelease.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        newReleaseadapter = new NewReleasesListAdapter( R.layout.search_results_video, UserGenreVideos, mListener, glideRequests);
        newReleaseadapter.notifyDataSetChanged();
        recyclerView_newRelease.setAdapter(newReleaseadapter);
    }

}
