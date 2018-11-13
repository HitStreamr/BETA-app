package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.BottomNav.ActivityFragment;
import com.hitstreamr.hitstreamrbeta.BottomNav.DiscoverFragment;
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.DashboardFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.GeneralSettingsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.HelpCenterFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.InviteAFriendFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.LegalAgreementsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.NotificationSettingsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.PaymentPrefFragment;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener{
    private Button logout;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavView;
    private Toolbar toolbar;
    private String type;


    FloatingActionButton fab;
    FloatingActionButton vv;

    private ItemClickListener mListener;
    //private ImageButton userbtn;

    private MenuItem profileItem;

    private TextView TextViewUsername;

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
    private FirebaseRecyclerAdapter<ArtistUser, ArtistAccountViewHolder> firebaseRecyclerAdapter_artist;
    private FirebaseRecyclerAdapter<User, BasicAccountViewHolder> firebaseRecyclerAdapter_basic;

    private TabLayout mTabLayout;
    private int tab_position;
    private String search_input, accountType;

    /**
     * Set up and initialize layouts and variables
     *
     * @param savedInstanceState state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Adding toolbar to the home activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.new_hitstreamr_h_logo_wht_w_);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitleTextAppearance(this, R.style.MyTitleTextApperance);
        getSupportActionBar().setTitle("BETA");

        // Adding tabs for searching, initially invisible
        mTabLayout = (TabLayout) findViewById(R.id.search_tabs);
        mTabLayout.setVisibility(View.GONE);

        // Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        glideRequests = Glide.with(this);
        db = FirebaseFirestore.getInstance();

        noRes = findViewById(R.id.emptyView);
        searching = findViewById(R.id.loadingSearch);

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
        vv = findViewById(R.id.videoScreen);

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
        if(name.equals("")){
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
                vv.setVisibility(View.GONE);
            } else {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, VideoUploadActivity.class));
                    }
                });
                vv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, VideoPlayer.class));
                    }
                });
            }
        }

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavView.setOnNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
        // Send a query to the database
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

        BasicAccountViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setUserName(final String userName) {
            TextView textView = view.findViewById(R.id.user_name);
            textView.setText(userName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();
                    Intent basicProfile = new Intent(getApplicationContext(), Profile.class);
                    basicProfile.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                    basicProfile.putExtra("artistUsername", userName);
                    startActivity(basicProfile);
                }
            });


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Artist Accounts Holder - Inner Class
     */
    public class ArtistAccountViewHolder extends RecyclerView.ViewHolder {
        private View view;

        ArtistAccountViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setUserName(final String userName) {
            TextView textView = view.findViewById(R.id.user_name);
            textView.setText(userName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();
                    Intent artistProfile = new Intent(getApplicationContext(), Profile.class);
                    artistProfile.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                    artistProfile.putExtra("artistUsername", userName);
                    startActivity(artistProfile);

                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT).show();
                }
            });

            Button follow = view.findViewById(R.id.follow_button);
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Followed", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        if(resultAdapter != null){
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
        FragmentTransaction transaction;
        Bundle bundle;

        switch (item.getItemId()) {
            case R.id.dashboard:
                getSupportActionBar().hide();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                DashboardFragment dashFrag = new DashboardFragment();
                dashFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, dashFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.general_setting:
                getSupportActionBar().hide();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                GeneralSettingsFragment genSettingsFrag = new GeneralSettingsFragment();
                genSettingsFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, genSettingsFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.notification_settings:
                getSupportActionBar().hide();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                NotificationSettingsFragment notifSettingsFrag = new NotificationSettingsFragment();
                notifSettingsFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, notifSettingsFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.payment_pref:
                getSupportActionBar().hide();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                PaymentPrefFragment payPrefFrag = new PaymentPrefFragment();
                payPrefFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, payPrefFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.invite_a_friend:
                getSupportActionBar().hide();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                InviteAFriendFragment inviteFrag = new InviteAFriendFragment();
                inviteFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, inviteFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.help_center:
                getSupportActionBar().hide();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                HelpCenterFragment helpFrag = new HelpCenterFragment();
                helpFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, helpFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.legal_agreements:
                getSupportActionBar().hide();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                LegalAgreementsFragment legalFrag = new LegalAgreementsFragment();
                legalFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, legalFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.logout:
                startActivity(new Intent(this, Pop.class));
                drawer.closeDrawer(GravityCompat.START);
                break;
            //Bottom Navigation Cases
            //TODO on which screen should the floating action bar be accessible
            case R.id.home:
                if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))){
                    fab.setVisibility(View.VISIBLE);
                }else{
                    fab.setVisibility(View.GONE);
                }
                getSupportActionBar();
                bottomNavView.setVisibility(View.VISIBLE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                HomeFragment homeFrag = new HomeFragment();
                homeFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, homeFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.discover:
                getSupportActionBar();
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.VISIBLE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                DiscoverFragment discFrag = new DiscoverFragment();
                discFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, discFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(MainActivity.this, "Discover", Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity:
                fab.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.VISIBLE);
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                ActivityFragment actFrag = new ActivityFragment();
                actFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, actFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(MainActivity.this, "Activity", Toast.LENGTH_SHORT).show();
                break;
            case R.id.library:
                fab.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Library", Toast.LENGTH_SHORT).show();
                Intent libraryIntent = new Intent(getApplicationContext(), Library.class);
                libraryIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(libraryIntent);
                break;
        }


        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //reset fab and bottom bar when going back

            //TODO does not properly handle going back with fragments and the dashboard
            fab.setVisibility(View.VISIBLE);
            bottomNavView.setVisibility(View.VISIBLE);
            getSupportActionBar().show();
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

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