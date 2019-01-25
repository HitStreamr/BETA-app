package com.hitstreamr.hitstreamrbeta;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.hitstreamr.hitstreamrbeta.BottomNav.ActivityFragment;
import com.hitstreamr.hitstreamrbeta.BottomNav.DiscoverFragment;
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;
import com.hitstreamr.hitstreamrbeta.Dashboard.Dashboard;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.GeneralSettingsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.HelpCenterFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.InviteAFriendFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.LegalAgreementsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.NotificationSettingsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.PaymentPrefFragment;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener{
    private final String HOME = "home";
    private final String DISCOVER = "discover";
    private final String ACTIVITY = "activity";
    private static final String FRAG_OTHER = "other_fragment";
    private static final String FRAG_HOME = "home_fragment";

    private Button logout;
    private DrawerLayout drawer;
    private LinearLayout contentHolder;
    private ViewGroup.LayoutParams defaultParams;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavView;
    private Toolbar toolbar;
    private String type;
    private Activity main;


    FloatingActionButton fab;

    private ItemClickListener mListener;
    //private ImageButton userbtn;

    private MenuItem profileItem;

    private TextView TextViewUsername;

    private TextView userCredits;

    //private ImageView ImageViewProfilePicture;
    private CircleImageView CirImageViewProPic;

    FirebaseFirestore db;
    FirestoreRecyclerAdapter suggestionAdapter;
    VideoResultAdapter resultAdapter;
    TextView noRes;
    ProgressBar searching;

    String name;
    Uri photoUrl;

    RequestManager  glideRequests;

    FirebaseUser user;
    public final String TAG = "HomeActivity";
    private final int MAX_PRELOAD = 10;
    // Database Purposes
    private RecyclerView recyclerView;
    private com.google.firebase.database.Query myRef; // for Firebase Database
    private FirebaseRecyclerAdapter<ArtistUser, ArtistAccountViewHolder>  firebaseRecyclerAdapter_artist;
    private FirebaseRecyclerAdapter<User, BasicAccountViewHolder> firebaseRecyclerAdapter_basic;

    private TabLayout mTabLayout;
    private int tab_position;
    private String search_input, accountType;

    private String creditValue;

    /**
     * Set up and initialize layouts and variables
     *
     * @param savedInstanceState state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = this;

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Adding toolbar to the home activity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.new_hitstreamr_h_logo_wht_w_);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        //toolbar.setTitleTextAppearance(this, R.style.MyTitleTextApperance);
        //getSupportActionBar().setTitle("BETA");
        toolbar.setTitle("HitStreamr");

        // Adding tabs for searching, initially invisible
        mTabLayout = (TabLayout) findViewById(R.id.search_tabs);
        mTabLayout.setVisibility(View.GONE);

        //Linear Layout
        contentHolder = findViewById(R.id.content_holder);
        defaultParams = contentHolder.getLayoutParams();

        // Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        glideRequests = Glide.with(this);
        //prevent from crashing due to setting the settings again
        if (savedInstanceState == null) {
            db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
        }

        noRes = findViewById(R.id.emptyView);
        searching = findViewById(R.id.loadingSearch);

        getUserType();


        //Listener for RCVs
        mListener = new ItemClickListener() {
            @Override
            public void onSuggestionClick(String title) {
                getVideoResults(title);
            }

            @Override
            public void onResultClick(Video video) {
                //Open Video Player for song
                Intent videoPlayerIntent = new Intent(MainActivity.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", video);
                videoPlayerIntent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
                videoPlayerIntent.putExtra("CREDIT", userCredits.getText());
                startActivity(videoPlayerIntent);
            }

            @Override
            public void onOverflowClick(Video title, View v) {
                showOverflow(v);
            }
        };

        name = user.getDisplayName();
        photoUrl = user.getPhotoUrl();

        Log.e(TAG, "Your profile" + name + photoUrl + user);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavView = findViewById(R.id.bottomNav);
        fab = findViewById(R.id.fab);

        TextViewUsername = navigationView.getHeaderView(0).findViewById(R.id.proUsername);
        CirImageViewProPic = navigationView.getHeaderView(0).findViewById(R.id.profilePicture);

        TextViewUsername.setVisibility(View.VISIBLE);
        CirImageViewProPic.setVisibility(View.VISIBLE);


        if(photoUrl == null){
            //CirImageViewProPic.setImageDrawable(R.drawable.artist);
            Log.e(TAG, "username is::" +name);
            Glide.with(getApplicationContext()).load(R.mipmap.ic_launcher_round).into(CirImageViewProPic);
        }
        else{
            Glide.with(getApplicationContext()).load(photoUrl).into(CirImageViewProPic);
        }
        if(name.equals("") || name.equals(null)){
            String tempname = "Username";
            TextViewUsername.setText(tempname);
        }
        else{
            TextViewUsername.setText(name);
        }
       /* else{
            TextViewUsername.setText(name);
            Glide.with(getApplicationContext()).load(photoUrl).into(CirImageViewProPic);
        }*/

        //get menu & extras
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {
            //check that type exists and set it.
            type = getIntent().getStringExtra("TYPE");

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                //Hide Dash if Basic User & don't show floating action buttton

                Log.d("HIDE_DASH", getIntent().getStringExtra("TYPE"));
                //nav_Menu.findItem(R.id.dashboard).setVisible(false);
                navigationView.getMenu().findItem(R.id.dashboard).setVisible(false);
                fab.setVisibility(View.GONE);
            } else {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, VideoUploadActivity.class));
                    }
                });
//                vv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(new Intent(MainActivity.this, VideoPlayer.class));
//                    }
//                });
            }
        }

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavView.setOnNavigationItemSelectedListener(this);

        //setting the fragments
        if(getIntent().hasExtra("OPTIONAL_FRAG"))
        {
            String frag = getIntent().getStringExtra("OPTIONAL_FRAG");
            switch(frag){
                case HOME:
                    bottomNavView.setSelectedItemId(R.id.home);
                    break;
                case DISCOVER:
                    bottomNavView.setSelectedItemId(R.id.discover);
                    break;
                case ACTIVITY:
                    bottomNavView.setSelectedItemId(R.id.activity);
                    break;
            }
        }else{
            //FRAG not set; default to home
            bottomNavView.setSelectedItemId(R.id.home);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        userCredits = navigationView.getHeaderView(0).findViewById(R.id.credits);
        Log.e(TAG, "Main activity user id  " + user.getUid());

        FirebaseDatabase.getInstance().getReference("Credits")
                .child(user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        Log.e(TAG, "Main activity credit val " + currentCredit);
                        if(!Strings.isNullOrEmpty(currentCredit)){

                            userCredits.setText(currentCredit);
                        }
                        else
                            userCredits.setText("0");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }

    /**
     * A listener for the Add Credits button
     * @param view view
     */
    public void addCredits(View view) {
        Intent creditsPurchaseIntent = new Intent(getApplicationContext(), CreditsPurchase.class);
        creditsPurchaseIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        startActivity(creditsPurchaseIntent);
    }

    /**
     * Firebase Realtime - Basic Accounts
     * @param querySearch the input typed by the user
     */
    private void searchBasicAccounts(String querySearch) {
        FirebaseDatabase database_basic = FirebaseDatabase.getInstance();
        myRef = database_basic.getReference().child("BasicAccounts").orderByChild("username").startAt(querySearch)
                .endAt(querySearch + "\uf8ff");

        FirebaseRecyclerOptions<User> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(myRef, User.class)
                .build();

        firebaseRecyclerAdapter_basic = new FirebaseRecyclerAdapter<User, BasicAccountViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BasicAccountViewHolder holder, int position, @NonNull User model) {
                holder.setUserName(model.getUsername());

                DatabaseReference db = FirebaseDatabase.getInstance().getReference("UsernameUserId")
                        .child(model.getUsername());
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId = dataSnapshot.child("tempUserId").getValue().toString();
                            FirebaseStorage.getInstance().getReference("profilePictures").child(userId)
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        Glide.with(getApplicationContext()).load(uri).into(holder.profilePicture);
                                    }
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // TODO: handle error
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // Followers count
                // TODO: add thousands/millions k/m feature
                db = FirebaseDatabase.getInstance().getReference("UsernameUserId").child(model.getUsername());
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId = dataSnapshot.child("tempUserId").getValue().toString();
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            db.getReference("followers").child(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int follower = (int) dataSnapshot.getChildrenCount();
                                    if (follower > 1) {
                                        holder.count.setText(follower + " Followers");
                                    } else {
                                        holder.count.setText(follower + " Follower");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            holder.count.setText("0 Follower");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //set up UI for following
//                holder.checkFollowing(new VideoPlayer.OnDataReceiveCallback() {
//                    @Override
//                    public void onFollowChecked(boolean following) {
//                        if(following){
//                            //if following == true
//                            holder.followButton.setVisibility(View.GONE);
//                            holder.unfollowButton.setVisibility(View.VISIBLE);
//                        }else{
//                            //if following == false
//                            holder.followButton.setVisibility(View.VISIBLE);
//                            holder.unfollowButton.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCheckUpdateFailed() {
//
//                    }
//                }, model.getUserID());
//
//                holder.followButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        holder.saveFollowing(new VideoPlayer.OnDataReceiveCallback() {
//                            @Override
//                            public void onFollowChecked(boolean following) {
//                                if(following){
//                                    //if following == true
//                                    holder.followButton.setVisibility(View.GONE);
//                                    holder.unfollowButton.setVisibility(View.VISIBLE);
//                                }
//                            }
//
//                            @Override
//                            public void onCheckUpdateFailed() {
//
//                            }
//                        },model.getUserID());
//                    }
//                });
//
//                holder.unfollowButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        holder.saveUnfollowing(new VideoPlayer.OnDataReceiveCallback() {
//                            @Override
//                            public void onFollowChecked(boolean following) {
//                                if(!following){
//                                    //if following == false
//                                    holder.followButton.setVisibility(View.VISIBLE);
//                                    holder.unfollowButton.setVisibility(View.GONE);
//
//                                }
//                            }
//
//                            @Override
//                            public void onCheckUpdateFailed() {
//
//                            }
//                        },model.getUserID());
//                    }
//                });
//
//
//                holder.updateFollowing(new FollowCountUpdateCallback() {
//                    @Override
//                    public void onUpdateCount(long count) {
//                        holder.count.setText(count+" Followers");
//                    }
//                }, model.getUserID());
            }

            @NonNull
            @Override
            public BasicAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_user, parent, false);
                return new BasicAccountViewHolder(view);
            }
        };
        firebaseRecyclerAdapter_basic.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter_basic);
    }

    /**
     * Firebase Realtime - Artist Accounts
     * @param querySearch the input typed by the user
     */
    private void searchArtistAccounts(String querySearch) {
        FirebaseDatabase database_artist = FirebaseDatabase.getInstance();
        myRef = database_artist.getReference().child("ArtistAccounts").orderByChild("username").startAt(querySearch)
                .endAt(querySearch + "\uf8ff");

        FirebaseRecyclerOptions<ArtistUser> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<ArtistUser>()
                .setQuery(myRef, ArtistUser.class)
                .build();

        firebaseRecyclerAdapter_artist = new FirebaseRecyclerAdapter<ArtistUser, ArtistAccountViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ArtistAccountViewHolder holder, int position, @NonNull ArtistUser model) {
                holder.setUserName(model.getUsername());

                DatabaseReference db = FirebaseDatabase.getInstance().getReference("UsernameUserId")
                        .child(model.getUsername());
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId = dataSnapshot.child("tempUserId").getValue().toString();
                            FirebaseStorage.getInstance().getReference("profilePictures").child(userId)
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        Glide.with(getApplicationContext()).load(uri).into(holder.profilePicture);
                                    }
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // TODO: handle error
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // Followers count
                // TODO: add thousands/millions k/m feature
                db = FirebaseDatabase.getInstance().getReference("UsernameUserId").child(model.getUsername());
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId = dataSnapshot.child("tempUserId").getValue().toString();
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            db.getReference("followers").child(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int follower = (int) dataSnapshot.getChildrenCount();
                                    if (follower > 1) {
                                        holder.count.setText(follower + " Followers");
                                    } else {
                                        holder.count.setText(follower + " Follower");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            holder.count.setText("0 Follower");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                holder.checkFollowing(new VideoPlayer.OnDataReceiveCallback() {
//                    @Override
//                    public void onFollowChecked(boolean following) {
//                        if(following){
//                            //if following == true
//                            holder.followButton.setVisibility(View.GONE);
//                            holder.unfollowButton.setVisibility(View.VISIBLE);
//                        }else{
//                            //if following == false
//                            holder.followButton.setVisibility(View.VISIBLE);
//                            holder.unfollowButton.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCheckUpdateFailed() {
//
//                    }
//                }, model.getUserID());
//
//                holder.followButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        holder.saveFollowing(new VideoPlayer.OnDataReceiveCallback() {
//                            @Override
//                            public void onFollowChecked(boolean following) {
//                                if(following){
//                                    //if following == true
//                                    holder.followButton.setVisibility(View.GONE);
//                                    holder.unfollowButton.setVisibility(View.VISIBLE);
//                                }
//                            }
//
//                            @Override
//                            public void onCheckUpdateFailed() {
//
//                            }
//                        },model.getUserID());
//                    }
//                });
//
//                holder.unfollowButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        holder.saveUnfollowing(new VideoPlayer.OnDataReceiveCallback() {
//                            @Override
//                            public void onFollowChecked(boolean following) {
//                                if(!following){
//                                    //if following == false
//                                    holder.followButton.setVisibility(View.VISIBLE);
//                                    holder.unfollowButton.setVisibility(View.GONE);
//
//                                }
//                            }
//
//                            @Override
//                            public void onCheckUpdateFailed() {
//
//                            }
//                        },model.getUserID());
//                    }
//                });
//
//                holder.updateFollowing(new FollowCountUpdateCallback() {
//                    @Override
//                    public void onUpdateCount(long count) {
//                        holder.count.setText(count+" Followers");
//                    }
//                },model.getUserID());

            }

            @NonNull
            @Override
            public ArtistAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_user, parent, false);
                return new ArtistAccountViewHolder(view);
            }
        };
        firebaseRecyclerAdapter_artist.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter_artist);
    }


    /**
     * Handles the search bar and view
     * @param menu menu
     * @return super.onCreateOptionsMenu
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        final MenuItem mSearch = menu.findItem(R.id.search);
        //Items
        profileItem = findViewById(R.id.profile);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");

        // Modify text colors
        EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.WHITE);

        // Profile Picture
        MenuItem profilePicMenu = menu.findItem(R.id.profilePicMenu);
        LinearLayout rootView = (LinearLayout) profilePicMenu.getActionView();
        CircleImageView circleImageView = rootView.findViewById(R.id.profilePictureToolbar);

        getUserType();
        if (user.getPhotoUrl() != null) {
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profilePage = new Intent(MainActivity.this, Profile.class);
                profilePage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(profilePage);
            }
        });

        // Set up the listeners for searching videos, artists, and listeners
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Get the current tab selection to decide which search method to call
                mTabLayout.setVisibility(View.VISIBLE);
                tab_position = mTabLayout.getSelectedTabPosition();
                if (!query.trim().isEmpty()) {
                    search_input = query;
                    switch (tab_position) {
                        case 0:
                            getVideoResults(query);
                            return true;

                        case 1:
                            searchArtistAccounts(query);
                            firebaseRecyclerAdapter_artist.startListening();
                            return true;

                        case 2:
                            searchBasicAccounts(query);
                            firebaseRecyclerAdapter_basic.startListening();
                            return true;
                    }
                }

                // Stop adapters from listening when search field is empty
                // to remove past searches, if there is any
                if (query.trim().isEmpty()) {
                    stopAdapters();
                    search_input = null;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mTabLayout.setVisibility(View.VISIBLE);
                tab_position = mTabLayout.getSelectedTabPosition();
                if (!newText.trim().isEmpty()) {
                    search_input = newText;
                    switch (tab_position) {
                        case 0:
                            //searchVideos(newText);
                            //firestoreRecyclerAdapter_video.startListening();
                            searchVideoFirestore(autocompleteQuery(newText));
                            // Update suggestionAdapter/Set Adapter/Show/Listen
                            suggestionAdapter.startListening();
                            return true;

                        case 1:
                            searchArtistAccounts(newText);
                            firebaseRecyclerAdapter_artist.startListening();
                            return true;

                        case 2:
                            searchBasicAccounts(newText);
                            firebaseRecyclerAdapter_basic.startListening();
                            return true;
                    }
                }

                if (newText.trim().isEmpty()) {
                    stopAdapters();
                    if (suggestionAdapter != null){
                        suggestionAdapter.stopListening();
                    }
                    search_input = null;
                }
                //edited from false/what is this for?
                return true;
            }
        });

        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            // Stop adapters from listening so recent searches are removed
            public boolean onMenuItemActionCollapse(MenuItem item) {
                MainActivity.this.setItemsVisibility(menu, mSearch, true);
                search_input = null;
                stopAdapters();
                bottomNavView.setSelectedItemId(R.id.home);
                MarginLayoutParams params = (MarginLayoutParams) contentHolder.getLayoutParams();
                TypedValue typedValue = new TypedValue();
                (main).getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
                int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
                int indexOfAttrTextSize = 0;
                TypedArray a = main.obtainStyledAttributes(typedValue.data, textSizeAttr);
                int marginSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
                a.recycle();
                params.topMargin = marginSize;
                mSearchView.setQuery("", false);
                mSearchView.clearFocus();
                mTabLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                bottomNavView.setVisibility(View.VISIBLE);
                // Do something when action item collapses
                Log.e("HOME", "On Close Initiated");
                return true;  // return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                MainActivity.this.setItemsVisibility(menu, mSearch, false);
                //hide panels
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                transaction.commit();
                MarginLayoutParams params = (MarginLayoutParams) contentHolder.getLayoutParams();
                params.topMargin = 0;
                mTabLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                return true;  // return true to expand action view
            }
        });

        // Update the search results with the current search input when a different tab is selected
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Get the new tab selection
                tab_position = mTabLayout.getSelectedTabPosition();
                if (search_input != null) {
                    stopAdapters();
                    switch (tab_position) {
                        case 0:
                            searchVideoFirestore(autocompleteQuery(search_input));
                            suggestionAdapter.startListening();
                            break;

                        case 1:
                            searchArtistAccounts(search_input);
                            firebaseRecyclerAdapter_artist.startListening();
                            break;

                        case 2:
                            searchBasicAccounts(search_input);
                            firebaseRecyclerAdapter_basic.startListening();
                            break;
                    }
                } else {
                    stopAdapters();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab_position = mTabLayout.getSelectedTabPosition();
                stopAdapters();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab_position = mTabLayout.getSelectedTabPosition();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public com.google.firebase.firestore.Query autocompleteQuery(String query) {
        int strlength = query.length();
        String strFrontCode = query.substring(0, strlength);
        String strEndCode = query.substring(strlength - 1);

        String endcode = strFrontCode + Character.toString((char) (strEndCode.charAt(0) + 1));

        //Query where the videos are in the correct range and not private
        return db.collection("Videos").whereGreaterThanOrEqualTo("title", query).whereLessThan("title", query + "\uf8ff")
                .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0]);
    }

    /**
     * Firestore - Video Suggestions
     * @param searchRequest the input typed by the user
     */
    public void searchVideoFirestore(com.google.firebase.firestore.Query searchRequest) {

        //New RecyclerOptions and Adapter, based on Query
        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(searchRequest, Video.class)
                .build();

        suggestionAdapter = new FirestoreRecyclerAdapter<Video, MainActivity.VideoSuggestionsHolder>(options) {
            @NonNull
            @Override
            public void onBindViewHolder(MainActivity.VideoSuggestionsHolder holder, int position, Video model) {
                holder.videoTitle.setText(model.getTitle());
            }
            @Override
            public MainActivity.VideoSuggestionsHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.search_suggestion_video, group, false);

                return new MainActivity.VideoSuggestionsHolder(view, mListener);
            }
        };

        suggestionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(suggestionAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }


    public void getVideoResults(String query) {
        //These Tasks are Task<QuerySnapShot>
        ArrayList<String> terms = processQuery(query);
        Log.e(TAG, terms.toString());
        final Task<QuerySnapshot> exactmatch = db.collection("Videos").whereEqualTo("title", query)
                .whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0]).get();

        //Stop using the old adapter
        stopAdapters();

        // build a dynamic query that has all the words

        com.google.firebase.firestore.Query allWords = db.collection("Videos");
        for (int i = 0; i < terms.size(); i++) {
            allWords = allWords.whereEqualTo("terms." + terms.get(i), true);
        }

        Task<QuerySnapshot> allWordsTask = allWords.whereEqualTo("privacy", getResources().getStringArray(R.array.Privacy)[0]).get();

        //allResultsRetrieved is only successful, when all are successful
        Task<List<QuerySnapshot>> allResultsRetrieved = Tasks.whenAllSuccess(exactmatch, allWordsTask);

        searching.setVisibility(View.VISIBLE);
        //When everything is done, then send to adapter
        allResultsRetrieved.addOnSuccessListener(
                new OnSuccessListener<List<QuerySnapshot>>() {
                    @Override
                    public void onSuccess(List<QuerySnapshot> tasks) {
                        ArrayList<Video> videos = new ArrayList<>();
                        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
                        for (QuerySnapshot qTasks : tasks) {
                            if (!qTasks.isEmpty()) {
                                ArrayList<DocumentSnapshot> tmp = new ArrayList<>(qTasks.getDocuments());
                                for (DocumentSnapshot ds : tmp) {
                                    if (!docs.contains(ds)) {
                                        docs.add(ds);
                                    }
                                }
                            }
                        }

                        for (DocumentSnapshot d : docs) {
                            //if doc exists
                            if (d.exists()) {
                                //Log.e(TAG, d.toObject(Video.class).toString());
                                Video currVideo = d.toObject(Video.class);
                                currVideo.setVideoId(d.getId());
                                videos.add(currVideo);
                            } else {
                                Log.e(TAG, "Document " + d.toString() + "does not exist");
                            }
                        }
                        stopAdapters();
                        //set up results

                        resultAdapter = new VideoResultAdapter(videos, mListener, glideRequests);
                        RecyclerViewPreloader<Video> preloader =
                                new RecyclerViewPreloader<>(
                                        Glide.with(getApplicationContext()), resultAdapter, resultAdapter, MAX_PRELOAD /*maxPreload*/);
                        resultAdapter.notifyDataSetChanged();
                        recyclerView.addOnScrollListener(preloader);
                        recyclerView.setAdapter(resultAdapter);
                        searching.setVisibility(View.GONE);
                    }
                }
        );

        allResultsRetrieved.addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Search failed");
                }
                //TODO HANDLE ERRORS
            }
        });
    }

    private ArrayList<String> processQuery(String query) {
        // ArrayList of characters to remove
        ArrayList<String> remove = new ArrayList<>();
        remove.add(" ");

        ArrayList<String> tmp = new ArrayList<>(Arrays.asList(query.trim().toLowerCase().split("\\s+")));
        tmp.removeAll(remove);

        return tmp;
    }

    /**
     *
     * @param v view
     */
    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.acct_profile);
        popupMenu.show();
    }

    public void showOverflow(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.video_overflow_menu);
        popupMenu.show();
    }


    /**
     *
     * @param item item
     * @return true if clicked
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account:
                Intent acct = new Intent(getApplicationContext(), Account.class);
                acct.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(acct);
                break;
            case R.id.profile:
                Intent prof = new Intent(getApplicationContext(), Profile.class);
                prof.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(prof);
                break;
            case R.id.fave_result:
                break;
            case R.id.addLibrary_result:
                break;

        }
        return true;
    }

    /**
     * Basic Accounts Holder - Inner Class
     */
    public class BasicAccountViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView name;
        private Button followButton;
        private Button unfollowButton;

        public TextView count;
        public CircleImageView profilePicture;

        BasicAccountViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = view.findViewById(R.id.user_name);
            count = view.findViewById(R.id.count);
            followButton = view.findViewById(R.id.follow_button);
            unfollowButton = view.findViewById(R.id.unfollow_button);
            profilePicture = view.findViewById(R.id.searchImage);
        }

        void setUserName(final String userName) {
            name.setText(userName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();
                    Intent basicProfile = new Intent(getApplicationContext(), Profile.class);
                    basicProfile.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                    basicProfile.putExtra("basicUsername", userName);
                    basicProfile.putExtra("SearchType", "BasicAccounts");
                    startActivity(basicProfile);
                }
            });

        }
        private void checkFollowing(VideoPlayer.OnDataReceiveCallback callback,String followingId){
            //get where the following state would be
            // check who the user is following
            FirebaseDatabase.getInstance().getReference().child("following").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(followingId).exists()){
                        Log.e(TAG, "Following");
                        callback.onFollowChecked(true);
                    }else{
                        Log.e(TAG, "Not Following");
                        callback.onFollowChecked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //TODO update these to more fault tolerant firebase updates?
        private void saveFollowing(VideoPlayer.OnDataReceiveCallback callback, String followedID){
            FirebaseDatabase.getInstance().getReference("following")
                    .child(user.getUid())
                    .child(followedID)
                    .setValue(followedID)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // do the Reciprocal on Success
                            // artistFollowers -> user
                            FirebaseDatabase.getInstance().getReference("followers")
                                    .child(followedID)
                                    .child(user.getUid())
                                    .setValue(user.getUid())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // hide/show the UI
                                            callback.onFollowChecked(true);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //TODO Error for following failing
                                    callback.onCheckUpdateFailed();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO Error for following failing
                    callback.onCheckUpdateFailed();
                }
            });
        }

        private void saveUnfollowing(VideoPlayer.OnDataReceiveCallback callback,String unfollowedID){
            FirebaseDatabase.getInstance()
                    .getReference("following")
                    .child(user.getUid())
                    .child(unfollowedID)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //remove user from artist's list of followers
                            FirebaseDatabase.getInstance()
                                    .getReference("followers")
                                    .child(unfollowedID)
                                    .child(user.getUid())
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //update UI
                                            callback.onFollowChecked(false);
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onCheckUpdateFailed();
                }
            });
        }

        private void updateFollowing(MainActivity.FollowCountUpdateCallback callback, String userID){
            DatabaseReference myFollowersRef = FirebaseDatabase.getInstance().getReference("followers")
                    .child(userID);
            myFollowersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long followerscount = dataSnapshot.getChildrenCount();
                   callback.onUpdateCount(followerscount);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }


    /**
     * Artist Accounts Holder - Inner Class
     */
    public class ArtistAccountViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView name;
        private Button followButton;
        private Button unfollowButton;

        public TextView count;
        public CircleImageView profilePicture;

        ArtistAccountViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = view.findViewById(R.id.user_name);
            count = view.findViewById(R.id.count);
            followButton = view.findViewById(R.id.follow_button);
            unfollowButton = view.findViewById(R.id.unfollow_button);
            //profileItem = view.findViewById(R.id.searchImage);
            profilePicture = view.findViewById(R.id.searchImage);
        }

        void setUserName(final String userName) {
            name.setText(userName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();
                    Intent artistProfile = new Intent(getApplicationContext(), Profile.class);
                    artistProfile.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                    artistProfile.putExtra("artistUsername", userName);
                    artistProfile.putExtra("SearchType", "ArtistAccounts");
                    startActivity(artistProfile);

                }
            });

        }

        private void checkFollowing(VideoPlayer.OnDataReceiveCallback callback,String followingId){
            //get where the following state would be
            // check who the user is following
            FirebaseDatabase.getInstance().getReference().child("following").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(followingId).exists()){
                        Log.e(TAG, "Following");
                        callback.onFollowChecked(true);
                    }else{
                        Log.e(TAG, "Not Following");
                        callback.onFollowChecked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //TODO update these to more fault tolerant firebase updates?
        private void saveFollowing(VideoPlayer.OnDataReceiveCallback callback, String followedID){
            FirebaseDatabase.getInstance().getReference("following")
                    .child(user.getUid())
                    .child(followedID)
                    .setValue(followedID)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // do the Reciprocal on Success
                            // artistFollowers -> user
                            FirebaseDatabase.getInstance().getReference("followers")
                                    .child(followedID)
                                    .child(user.getUid())
                                    .setValue(user.getUid())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // hide/show the UI
                                            callback.onFollowChecked(true);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //TODO Error for following failing
                                    callback.onCheckUpdateFailed();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO Error for following failing
                    callback.onCheckUpdateFailed();
                }
            });
        }

        private void saveUnfollowing(VideoPlayer.OnDataReceiveCallback callback,String unfollowedID){
            FirebaseDatabase.getInstance()
                    .getReference("following")
                    .child(user.getUid())
                    .child(unfollowedID)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //remove user from artist's list of followers
                            FirebaseDatabase.getInstance()
                                    .getReference("followers")
                                    .child(unfollowedID)
                                    .child(user.getUid())
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //update UI
                                            callback.onFollowChecked(false);
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onCheckUpdateFailed();
                }
            });
        }

        private void updateFollowing(MainActivity.FollowCountUpdateCallback callback, String userID){
            DatabaseReference myFollowersRef = FirebaseDatabase.getInstance().getReference("followers")
                    .child(userID);
            myFollowersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long followerscount = dataSnapshot.getChildrenCount();
                    callback.onUpdateCount(followerscount);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    /*
    Provides interface for the callback for Async call to Firebase
    */
    public interface FollowCountUpdateCallback{
        /*
         *   Method that notifies the ui that the Data was received
         */
        void onUpdateCount(long count);

    }

    /**
     * Video Suggestions Holder - Inner Class
     */
    class VideoSuggestionsHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;

        public VideoSuggestionsHolder(View itemView, final ItemClickListener mListener) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.video_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSuggestionClick(videoTitle.getText().toString());
                }
            });
        }
    }

    public interface ItemClickListener {
        void onSuggestionClick(String title);
        void onResultClick(Video title);
        void onOverflowClick(Video title, View v);
    }

    /**
     * Stop any of the working adapters
     */
    private void stopAdapters() {
        if (firebaseRecyclerAdapter_artist != null) {
            firebaseRecyclerAdapter_artist.stopListening();
        }
        if (suggestionAdapter != null) {
            suggestionAdapter.stopListening();
        }
        if (firebaseRecyclerAdapter_basic != null) {
            firebaseRecyclerAdapter_basic.stopListening();
        }
        if (resultAdapter != null) {
            resultAdapter.clear();
            resultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Handles fragment items
     * @param item menu item
     * @return true to show fragments
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle bundle;


        switch (item.getItemId()) {
            case R.id.dashboard:
                Intent dashIntent = new Intent(getApplicationContext(), Dashboard.class);
                dashIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(dashIntent);
                return true;

            case R.id.getVerified:
                Intent verifiedIntent = new Intent(getApplicationContext(), GetVerifiedPopUp.class);
                verifiedIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(verifiedIntent);
                return true;

            case R.id.general_setting:
                sideNavSetup();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                GeneralSettingsFragment genSettingsFrag = new GeneralSettingsFragment();
                genSettingsFrag.setArguments(bundle);
                viewFragment(genSettingsFrag,FRAG_OTHER);
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.notification_settings:
                sideNavSetup();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                bundle.putString("ID", user.getUid());
                NotificationSettingsFragment notifSettingsFrag = new NotificationSettingsFragment();
                notifSettingsFrag.setArguments(bundle);
                viewFragment(notifSettingsFrag,FRAG_OTHER);
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.payment_pref:
                sideNavSetup();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                PaymentPrefFragment payPrefFrag = new PaymentPrefFragment();
                payPrefFrag.setArguments(bundle);
                viewFragment(payPrefFrag,FRAG_OTHER);
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.invite_a_friend:
                sideNavSetup();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                InviteAFriendFragment inviteFrag = new InviteAFriendFragment();
                inviteFrag.setArguments(bundle);
                viewFragment(inviteFrag,FRAG_OTHER);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.help_center:
                sideNavSetup();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                HelpCenterFragment helpFrag = new HelpCenterFragment();
                helpFrag.setArguments(bundle);
                viewFragment(helpFrag,FRAG_OTHER);
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.legal_agreements:
                sideNavSetup();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                LegalAgreementsFragment legalFrag = new LegalAgreementsFragment();
                legalFrag.setArguments(bundle);
                viewFragment(legalFrag,FRAG_OTHER);
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.logout:
                startActivity(new Intent(this, Pop.class));
                drawer.closeDrawer(GravityCompat.START);
                return true;
            //Bottom Navigation Cases
            //TODO on which screen should the floating action bar be accessible
            case R.id.home:

                if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                    bottomNavSetUp(true);
                } else {
                    bottomNavSetUp(false);
                }
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                HomeFragment homeFrag = new HomeFragment();
                homeFrag.setArguments(bundle);
                viewFragment(homeFrag,FRAG_HOME);
                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.discover:
                bottomNavSetUp(false);
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                DiscoverFragment discFrag = new DiscoverFragment();
                discFrag.setArguments(bundle);
                viewFragment(discFrag,FRAG_OTHER);
                Toast.makeText(MainActivity.this, "Discover", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.activity:
                bottomNavSetUp(false);
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                ActivityFragment actFrag = new ActivityFragment();
                actFrag.setArguments(bundle);
                viewFragment(actFrag,FRAG_OTHER);
                Toast.makeText(MainActivity.this, "Activity", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.library:
                Toast.makeText(MainActivity.this, "Library", Toast.LENGTH_SHORT).show();
                Intent libraryIntent = new Intent(getApplicationContext(), Library.class);
                libraryIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(libraryIntent);
                return true;
        }


        return false;
    }

    private void bottomNavSetUp(boolean fabvisibility){
        getSupportActionBar().show();
        if(fabvisibility){
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }
        bottomNavView.setVisibility(View.VISIBLE);
        MarginLayoutParams params = (MarginLayoutParams) contentHolder.getLayoutParams();
        TypedValue typedValue = new TypedValue();
        (main).getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
        int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
        int indexOfAttrTextSize = 0;
        TypedArray a = main.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int marginSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        params.topMargin = marginSize;
    }


    private void sideNavSetup(){
        MarginLayoutParams params;
        params = (MarginLayoutParams) contentHolder.getLayoutParams();
        params.topMargin = 0;
        getSupportActionBar().hide();
        fab.setVisibility(View.GONE);
        bottomNavView.setVisibility(View.GONE);
    }

    private void viewFragment(Fragment fragment, String name){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // 1. Know how many fragments there are in the stack
        final int count = fragmentManager.getBackStackEntryCount();
        // 2. If the fragment is **not** "home type", save it to the stack
        if( name.equals(FRAG_OTHER) ) {
            fragmentTransaction.addToBackStack(name);
        }
        // Commit !
        fragmentTransaction.commit();
        // 3. After the commit, if the fragment is not an "home type" the back stack is changed, triggering the
        // OnBackStackChanged callback
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // If the stack decreases it means I clicked the back button
                if( fragmentManager.getBackStackEntryCount() <= count){
                    // pop all the fragment and remove the listener
                    fragmentManager.popBackStack(FRAG_OTHER, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    // set the home button selected
                    bottomNavView.setSelectedItemId(R.id.home);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() { super.onResume(); }

    @Override
    protected void onStop() {
        super.onStop();
        stopAdapters();
    }

    /**
     * Decide to show/hide the items in the toolbar
     * @param menu menu
     * @param exception menu item
     * @param visible visibility
     */
    private void setItemsVisibility(final Menu menu, final MenuItem exception,
                                    final boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception)
                item.setVisible(visible);
        }
    }

    /**
     * Get the account type of the current user
     */
    private void getUserType() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                accountType = "BasicAccounts";
            } else if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                accountType = "ArtistAccounts";
            } else {
                accountType = "LabelAccounts";
            }
        }
    }

    /**
     * Open account page from navigation bar.
     * @param view view
     */
    public void viewAccount(View view) {
        Intent accountPage = new Intent(MainActivity.this, Account.class);
        accountPage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        startActivity(accountPage);
    }

    /**
     * Open profile page from navigation bar.
     * @param view view
     */
    public void openProfile(View view) {
        Intent profilePage = new Intent(MainActivity.this, Profile.class);
        profilePage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        startActivity(profilePage);
    }
}