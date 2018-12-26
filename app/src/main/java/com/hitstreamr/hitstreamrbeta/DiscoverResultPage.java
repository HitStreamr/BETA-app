package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private String accountType;

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
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

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

//        if (category.equals("Newly Added")) {
//            // TODO most recent videos
//            loadMostRecentVideos();
//        } else if (category.equals("Trending Now")) {
//            // TODO most number of views
//            loadTrendingVideos();
//        } else if (category.equals("Popular Playlists")) {
//            // TODO most popular playlist-ed(?)
//
//        } else if (category.equals("Hip-hop/r&b")) {
//            category = "Hip-Hop/R&B";
//            getSupportActionBar().setTitle(category);
//            loadVideosBasedOnGenre(category);
//        } else if (category.equals("R&b/soul")) {
//            category = "R&B/Soul";
//            getSupportActionBar().setTitle(category);
//            loadVideosBasedOnGenre(category);
//        } else if (category.equals("World Music/beats")) {
//            category = "World Music/Beats";
//            getSupportActionBar().setTitle(category);
//            loadVideosBasedOnGenre(category);
//        } else {
//            // For all other genres
//            loadVideosBasedOnGenre(category);
//        }
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
        } else if (category.equals("Popular Playlists")) {
            // TODO most popular playlist-ed(?)
            query = firebaseFirestore.collection("Videos");
            // might need a different adapter/method
        } else if (category.equals("Hip-hop/rap")) {
            category = "Hip-Hop/Rap";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
        } else if (category.equals("R&b/soul")) {
            category = "R&B/Soul";
            getSupportActionBar().setTitle(category);
            query = query.whereEqualTo("genre", category);
//        } else if (category.equals("World Music/beats")) {
//            category = "World Music/Beats";
//            getSupportActionBar().setTitle(category);
//            query = query.whereEqualTo("genre", category);
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

        FirestoreRecyclerOptions<Video> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        firestoreRecyclerAdapter_videos = new FirestoreRecyclerAdapter<Video, DiscoverResultHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull DiscoverResultHolder holder, int position, @NonNull Video model) {
                holder.videoTitle.setText(model.getTitle());
                holder.videoUsername.setText(model.getUsername());
                holder.videoYear.setText(String.valueOf(model.getPubYear()));
                holder.videoDuration.setText(model.getDuration());

                // TODO adjust view/views + use K/M
                String videoViews = model.getViews() + " views";
                holder.videoViews.setText(videoViews);

                // Set the video thumbnail
                String URI = model.getThumbnailUrl();
                Glide.with(holder.videoThumbnail.getContext()).load(URI).into(holder.videoThumbnail);

                holder.videoCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent videoPlayerPage = new Intent(DiscoverResultPage.this, VideoPlayer.class);
                        videoPlayerPage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                        videoPlayerPage.putExtra("VIDEO", model);
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

                        // TODO: implement the video popup menu
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.addToWatchLater_videoMenu:
                                        return true;

                                    case R.id.addToPlaylist_videoMenu:
                                        return true;

                                    case R.id.repost_videoMenu:
                                        return true;

                                    case R.id.report_videoMenu:
                                        return true;
                                }
                                return false;
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
                List<ArtistUser> artistList = new ArrayList<>(artistFireStoreList.size());
                DiscoverArtistsResultAdapter artistAdapter = new DiscoverArtistsResultAdapter(artistList,
                        getApplicationContext(), getIntent());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ArtistAccounts");
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String artistID = dataSnapshot.getKey();

                        // Initialize as many as it will hold
                        while (artistList.size() < artistFireStoreList.size()) {
                            artistList.add(dataSnapshot.getValue(ArtistUser.class));
                        }

                        // Replace the indexes with appropriate values
                        // The indexes will match, and thus sorted
                        if (artistFireStoreList.contains(artistID)) {
                            int index = artistFireStoreList.indexOf(artistID);
                            ArtistUser artistUser = dataSnapshot.getValue(ArtistUser.class);
                            artistList.remove(index);
                            artistList.add(index, artistUser);
                            artistAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(artistAdapter);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

        public TextView videoTitle, videoUsername, videoYear, videoDuration, videoViews;
        public ImageView videoThumbnail, videoMenu;
        public LinearLayout videoCard;

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
}
