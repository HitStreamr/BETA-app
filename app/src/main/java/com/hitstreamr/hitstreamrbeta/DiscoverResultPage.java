package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoverResultPage extends AppCompatActivity {

    private FirestoreRecyclerAdapter<Video, DiscoverResultHolder> firestoreRecyclerAdapter_videos;
    private FirebaseUser current_user;
    private String accountType, creditValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_result_page);

        // Adding toolbar to the home activity
        Toolbar toolbar = findViewById(R.id.toolbar_discover);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getUserType();
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Profile Picture
        CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
        if (current_user.getPhotoUrl() != null) {
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profilePage = new Intent(DiscoverResultPage.this, Profile.class);
                profilePage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(profilePage);
            }
        });

        // Set the title according to selected category
        // Modify the string
        String category = getIntent().getStringExtra("DISCOVER");
        String[] words = category.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        category = TextUtils.join(" ", words);
        getSupportActionBar().setTitle(category);

        if (category.equals("Artists To Watch")) {
            loadArtistsToWatch();
        } else {
            loadVideos(category);
        }

        // Get the current user's credit value
        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if(!Strings.isNullOrEmpty(currentCredit)){

                            creditValue = currentCredit;
                        }
                        else
                            creditValue = "0";

                        // Log.e(TAG, "Profile credit val inside change" + CreditVal);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Load videos based on genres.
     * @param category category
     */
    private void loadVideos(String category) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_discoverResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Video> videoList = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("Videos");

        // Check for categories
        if (category.equals("Newly Added")) {
            query = firebaseFirestore.collection("Videos").orderBy("timestamp", Query.Direction.DESCENDING);
        } else if (category.equals("Trending Now")) {
            query = firebaseFirestore.collection("Videos").orderBy("views", Query.Direction.DESCENDING);
        } else if (category.equals("Hip-hop/rap")) {
            category = "Hip-Hop/Rap";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("R&b/soul")) {
            category = "R&B/Soul";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("Indie/rock")) {
            category = "Indie/Rock";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("Soul/funk")) {
            category = "Soul/Funk";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("Dance/electronic")) {
            category = "Dance/Electronic";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("K-pop")) {
            category = "K-Pop";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("Reggae/afro")) {
            category = "Reggae/Afro";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("Gospel/inspirational")) {
            category = "Gospel/Inspirational";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("Jazz/blues")) {
            category = "Jazz/Blues";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else {
            // For all other genres
            query = query.whereEqualTo("genre", category);
        }

        // Filter the query for soft-deleted and private videos
        query = query.whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0])
                .whereEqualTo("delete", "N");

        FirestoreRecyclerOptions<Video> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        firestoreRecyclerAdapter_videos = new FirestoreRecyclerAdapter<Video, DiscoverResultHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull DiscoverResultHolder holder, int position, @NonNull Video model) {
                holder.videoTitle.setText(model.getTitle());
                //TODO needs to be a callback (or however follows are done)
//                holder.videoUsername.setText(model.getUsername());
                holder.videoYear.setText(String.valueOf(model.getPubYear()));
                holder.videoDuration.setText(model.getDuration());

                // TODO adjust view/views + use K/M
                String videoViews = model.getViews() + " views";
                holder.videoViews.setText(videoViews);

                // Set the video thumbnail
                String URI = model.getUrl();
                Glide.with(holder.videoThumbnail.getContext()).load(URI).into(holder.videoThumbnail);

                holder.videoCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent videoPlayerPage = new Intent(DiscoverResultPage.this, VideoPlayer.class);
                        videoPlayerPage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                        videoPlayerPage.putExtra("VIDEO", model);
                        videoPlayerPage.putExtra("CREDIT", creditValue);
                        startActivity(videoPlayerPage);
                    }
                });

                holder.videoMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                        MenuInflater menuInflater = popupMenu.getMenuInflater();
                        menuInflater.inflate(R.menu.video_menu_pop_up, popupMenu.getMenu());
                        popupMenu.show();

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.addToWatchLater_videoMenu:
                                        addToWatchLater(model);
                                        break;

                                    case R.id.addToPlaylist_videoMenu:
                                        addToPlaylist(model);
                                        break;

                                    case R.id.report_videoMenu:
                                        Intent reportVideo = new Intent(getApplicationContext(), ReportVideoPopup.class);
                                        reportVideo.putExtra("VideoId", model.getVideoId());
                                        startActivity(reportVideo);
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public DiscoverResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_video, parent, false);
                return new DiscoverResultHolder(view);
            }
        };
        recyclerView.setAdapter(firestoreRecyclerAdapter_videos);
    }

    /**
     * Load artists to watch based on number of likes.
     */
    private void loadArtistsToWatch() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_discoverResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("ArtistsLikes").orderBy("likes", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> artistFireStoreList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    //artistList.add(documentSnapshot.getId());
                    String temp = documentSnapshot.getId();
                    artistFireStoreList.add(temp);
                }

                // Query to Firebase
                List<ArtistUser> artistList = new ArrayList<>();
                DiscoverArtistsResultAdapter discoverArtistsResultAdapter =
                        new DiscoverArtistsResultAdapter(artistList,getApplicationContext(), getIntent());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
                for (String artistID : artistFireStoreList) {
                    databaseReference.child(artistID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ArtistUser artist_user = dataSnapshot.getValue(ArtistUser.class);
                                artistList.add(artist_user);
                                discoverArtistsResultAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(discoverArtistsResultAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    /**
     * Handles back button on toolbar
     * @return true if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Discover Result Holder - Inner Class
     */
    public class DiscoverResultHolder extends RecyclerView.ViewHolder {

        TextView videoTitle, videoUsername, videoYear, videoDuration, videoViews;
        ImageView videoThumbnail, videoMenu;
        LinearLayout videoCard;

        public DiscoverResultHolder(View view) {
            super(view);

            videoTitle = view.findViewById(R.id.videoTitle);
            videoUsername = view.findViewById(R.id.videoUsername);
            videoThumbnail = view.findViewById(R.id.videoThumbnail);
            videoYear = view.findViewById(R.id.videoYear);
            videoDuration = view.findViewById(R.id.videoTime);
            videoCard = view.findViewById(R.id.videoCard);
            videoViews = view.findViewById(R.id.videoViews);
            videoMenu = view.findViewById(R.id.moreMenu);
        }
    }

    /**
     * Start the FireStore Recycler Adapter.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (firestoreRecyclerAdapter_videos != null) {
            firestoreRecyclerAdapter_videos.startListening();
        }
    }

    /**
     * Stop the FireStore Recycler Adapter.
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (firestoreRecyclerAdapter_videos != null) {
            firestoreRecyclerAdapter_videos.stopListening();
        }
    }

    /**
     * Get the account type of the current user
     */
    private void getUserType() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {
            //type = getIntent().getStringExtra("TYPE");

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                accountType = "BasicAccounts";
            } else if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                // sth
                accountType = "ArtistAccounts";
            } else {
                accountType = "LabelAccounts";
            }
        }
    }

    /**
     * Add the video to the watch later list.
     * @param video video
     */
    private void addToWatchLater(Video video) {
        FirebaseDatabase.getInstance()
                .getReference("WatchLater")
                .child(current_user.getUid())
                .child(video.getVideoId())
                .child("VideoId")
                .setValue(video.getVideoId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Video has been added to Watch Later",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Add the video to the playlist.
     * @param video video
     */
    private void addToPlaylist(Video video) {
        Intent playlistIntent = new Intent(getApplicationContext(), AddToPlaylist.class);
        playlistIntent.putExtra("VIDEO", video);
        playlistIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
        startActivity(playlistIntent);
    }
}
