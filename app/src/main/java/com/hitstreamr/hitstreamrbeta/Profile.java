package com.hitstreamr.hitstreamrbeta;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "Profile";

    private FirebaseUser current_user;
    private CircleImageView circleImageView;
    private String accountType;

    private Toolbar toolbar;

    private Button mfollowBtn;
    private Button mUnfollowBtn;
    private Button mEditProfile;

    private TextView mfollowers;
    private TextView mfollowing;
    private TextView mProfileName;
    private TextView mBio;

    private ImageView ImageViewBackground;
    private ImageView verifiedCheckMark;

    private long followerscount = 0;
    private long followingcount = 0;

    private String userClicked;
    private String userUserID;

    private int tab_position;

    FirebaseDatabase database;
    DatabaseReference myFollowersRef, myFollowingRef;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageTask mstorageTask;
    private StorageReference mStorageRef = storage.getReference();
    private StorageReference backgroundRef = null;

    Uri profilePictureDownloadUrl;

    private UserFeedAdapter userAdapter;
    private ArrayList<String> userVideoList;
    private ArrayList<Video> UserVideoId;
    private RecyclerView view_UserFeed;
    private ArrayList<Feed> UserFeedDetails;

    private UserUploadVideoAdapter userVideoAdapter;
    private ArrayList<String> userUploadVideoList;
    private ArrayList<Video> UserUploadVideoId;
    private RecyclerView view_UserUpload;
    private final int MAX_PRELOAD = 10;
    RequestManager glideRequests;
    private ItemClickListener mListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feedRef = db.collection("Videos");

    private CollectionReference videoIdRef = db.collection("ArtistVideo");

    private String CreditVal;
    private String FollowUserId;


    /**
     * Set up and initialize layouts and variables
     *
     * @param savedInstanceState state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        mfollowBtn = findViewById(R.id.followUser);
        mUnfollowBtn = findViewById(R.id.unFollowUser);
        mEditProfile = findViewById(R.id.editUser);

        mfollowers = findViewById(R.id.usersFollowers);
        mfollowing = findViewById(R.id.usersFollowing);
        mProfileName = findViewById(R.id.profileName);
        mBio = findViewById(R.id.bioText);

        ImageViewBackground = findViewById(R.id.profileBackgroundImage);
        verifiedCheckMark = findViewById(R.id.verified);

        mUnfollowBtn.setVisibility(View.GONE);
        mEditProfile.setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        database = FirebaseDatabase.getInstance();
        //myRef.setValue("Hello, World!");

        mfollowBtn.setOnClickListener(this);
        mUnfollowBtn.setOnClickListener(this);
        mEditProfile.setOnClickListener(this);

        //getBackgroundImage();
        getUserType();
        getUsername();



        if (userClicked.equals("")) {
            Log.e(TAG, "Current user selected");

            myFollowersRef = FirebaseDatabase.getInstance().getReference("followers")
                    .child(current_user.getUid());
            myFollowingRef = FirebaseDatabase.getInstance().getReference("following")
                    .child(current_user.getUid());
            getCurrentProfile();
            getFollowersCount();
            getFollowingCount();
            getUserFeedDeatils(current_user.getUid());
            getUserFeed(current_user.getUid());
        } else {
            getUserClickedUserId();
        }

        view_UserFeed = (RecyclerView) findViewById(R.id.listView_Feed);
        view_UserFeed.setLayoutManager(new LinearLayoutManager(this));

        view_UserUpload = (RecyclerView) findViewById(R.id.listView_Upload);
        view_UserUpload.setLayoutManager(new LinearLayoutManager(this));
        glideRequests = Glide.with(this);

        userVideoList = new ArrayList<>();
        UserVideoId = new ArrayList<>();
        UserFeedDetails = new ArrayList<>();

        userUploadVideoList = new ArrayList<>();
        UserUploadVideoId = new ArrayList<>();
        //UserUploadDetails = new ArrayList<>();

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
       // Log.e(TAG, "Profile credit val " + CreditVal);

        mListener = new ItemClickListener() {
            @Override
            public void onResultClick(Video video) {
                Intent videoPlayerIntent = new Intent(Profile.this, VideoPlayer.class);
                videoPlayerIntent.putExtra("VIDEO", video);
                videoPlayerIntent.putExtra("CREDIT", CreditVal);
                startActivity(videoPlayerIntent);
            }

            @Override
            public void onOverflowClick(Video title, View v) { showOverflow(v);
            }
        };

        // Set toolbar profile picture to always be the current user
        if (current_user.getPhotoUrl() != null) {
            circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }
    }

    public interface ItemClickListener {
        void onResultClick(Video title);
        void onOverflowClick(Video title, View v);
    }

    String value;

    private void setFollowButton(){
        FirebaseDatabase.getInstance().getReference("followers")
                .child(userUserID)
                .child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            value = dataSnapshot.getValue(String.class);
                            if (value.equals(current_user.getUid())) {
                                mfollowBtn.setVisibility(View.GONE);
                                mUnfollowBtn.setVisibility(View.VISIBLE);
                                mEditProfile.setVisibility(View.GONE);
                            }
                        }
                        else{
                            mfollowBtn.setVisibility(View.VISIBLE);
                            mUnfollowBtn.setVisibility(View.GONE);
                            mEditProfile.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getCurrentProfile() {
        mfollowBtn.setVisibility(View.GONE);
        mEditProfile.setVisibility(View.VISIBLE);

        if (accountType.equals("BasicAccounts")) {
            verifiedCheckMark.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance()
                .getReference(accountType)
                .child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        getSupportActionBar().setTitle(username);
                        getBackgroundImage(current_user.getUid());



                        if (dataSnapshot.child("artistname").exists()) {
                            String artist_name = dataSnapshot.child("artistname").getValue(String.class);
                            mProfileName.setText(artist_name);
                        }

                        if (dataSnapshot.child("fullname").exists()) {
                            String name = dataSnapshot.child("fullname").getValue(String.class);
                            mProfileName.setText(name);
                        }

                        if (dataSnapshot.child("bio").exists()) {
                            String bio = dataSnapshot.child("bio").getValue(String.class);
                            mBio.setText(bio);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // Profile Picture
        if (current_user.getPhotoUrl() != null) {
            circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            CircleImageView profileImageView = findViewById(R.id.profileImage);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
            Glide.with(getApplicationContext()).load(photoURL).into(profileImageView);
        }
        //getFollowersCount();
        setTabDetails();
    }

    private void setTabDetails(){
        // Set up tab layout & items
        TabLayout mTabLayout = findViewById(R.id.tabLayout_profile);
        TabItem feed_tab = findViewById(R.id.feed_tab);
        TabItem uploads_tab = findViewById(R.id.uploads_tab);
        TabItem playlists_tab = findViewById(R.id.playlists_tab);

        if (Strings.isNullOrEmpty(userUserID)) {
            // Hide uploads for basic users
            if (getIntent().getStringExtra("TYPE").equals("BASIC")) {
                mTabLayout.removeTabAt(1);
            }
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Get the new tab selection
                tab_position = mTabLayout.getSelectedTabPosition();

                switch (tab_position) {
                    case 0:
                        view_UserFeed.setVisibility(View.VISIBLE);
                        view_UserUpload.setVisibility(View.GONE);
                        if (!Strings.isNullOrEmpty(userUserID)) {
                            getUserFeedDeatils(userUserID);
                            getUserFeed(userUserID);
                        }
                        else{
                            getUserFeedDeatils(current_user.getUid());
                            getUserFeed(current_user.getUid());
                        }

                        break;
                    case 1:
                        view_UserFeed.setVisibility(View.GONE);
                        view_UserUpload.setVisibility(View.VISIBLE);
                        if (!Strings.isNullOrEmpty(userUserID)) {
                            getUserUploadVideoId(userUserID);
                        }
                        else{
                            getUserUploadVideoId(current_user.getUid());
                        }

                        break;

                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                userAdapter.clear();
                if (userVideoAdapter != null) {
                    userVideoAdapter.clear();
                }
                tab_position = mTabLayout.getSelectedTabPosition();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab_position = mTabLayout.getSelectedTabPosition();
            }
        });

    }

    // Update the search results with the current search input when a different tab is selected

    private void getSearchProfile() {
       String searchType = getIntent().getStringExtra("SearchType");

        if (searchType.equals("BasicAccounts")) {
            verifiedCheckMark.setVisibility(View.GONE);

            // Hide uploads for basic users
            TabLayout mTabLayout = findViewById(R.id.tabLayout_profile);
            mTabLayout.removeTabAt(1);
        }

        FirebaseDatabase.getInstance()
                .getReference(searchType)
                .child(userUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        getSupportActionBar().setTitle(username);
                        getBackgroundImage(userUserID);
                        Log.e(TAG, "Got username :: " + username);
                        getUrlStorage();

                        if (dataSnapshot.child("artistname").exists()) {
                            String artist_name = dataSnapshot.child("artistname").getValue(String.class);
                            mProfileName.setText(artist_name);
                        }

                        if (dataSnapshot.child("fullname").exists()) {
                            String name = dataSnapshot.child("fullname").getValue(String.class);
                            mProfileName.setText(name);
                        }

                        if (dataSnapshot.child("bio").exists()) {
                            String bio = dataSnapshot.child("bio").getValue(String.class);
                            mBio.setText(bio);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getUrlStorage() {
      //  Log.e(TAG, "Entered storage");
        mStorageRef.child("profilePictures")
                .child(userUserID)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        profilePictureDownloadUrl = uri;
                       // Log.e(TAG, "profile picture uri::" + profilePictureDownloadUrl);
                        if (profilePictureDownloadUrl != null) {
                            circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
                            circleImageView.setVisibility(View.VISIBLE);
                            CircleImageView profileImageView = findViewById(R.id.profileImage);
                            //Uri photoURL = current_user.getPhotoUrl();
                            Glide.with(getApplicationContext()).load(profilePictureDownloadUrl).into(circleImageView);
                            Glide.with(getApplicationContext()).load(profilePictureDownloadUrl).into(profileImageView);
                            getFollowersCount();
                            //getFollowingCount();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
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

    private void getUsername() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("artistUsername") && getIntent().getStringExtra("artistUsername") != null) {
            userClicked = getIntent().getStringExtra("artistUsername");
            //Log.e(TAG, "username clicked is:::" + userClicked);
        } else if (extras.containsKey("basicUsername") && getIntent().getStringExtra("basicUsername") != null) {
            userClicked = getIntent().getStringExtra("basicUsername");
        } else {
            userClicked = "";
        }
    }


    private void getUserClickedUserId() {
        FirebaseDatabase.getInstance()
                .getReference("UsernameUserId")
                .child(userClicked)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userUserID = dataSnapshot.child("tempUserId").getValue(String.class);
                        Log.e(TAG, "userid is :::" + userUserID);
                        Log.e(TAG, "data snapshot values :::" + dataSnapshot);

                        if (userUserID.equals(current_user.getUid())) {
                            mfollowBtn.setVisibility(View.GONE);
                        }
                        myFollowersRef = FirebaseDatabase.getInstance().getReference("followers")
                                .child(userUserID);
                        myFollowingRef = FirebaseDatabase.getInstance().getReference("following")
                                .child(userUserID);

                        getSearchProfile();
                        setFollowButton();
                        getUserFeedDeatils(userUserID);
                        getUserFeed(userUserID);
                        setTabDetails();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void addFollowers() {

        FirebaseDatabase.getInstance().getReference("following")
                .child(current_user.getUid())
                .child(userUserID)
                .setValue(userUserID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addFollowers1();
                    }
                });
    }

    private void addFollowers1() {
        FirebaseDatabase.getInstance().getReference("followers")
                .child(userUserID)
                .child(current_user.getUid())
                .setValue(current_user.getUid())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setFollowing();
                    }
                });
    }

    private void cancelFollowers() {
        FirebaseDatabase.getInstance()
                .getReference("following")
                .child(current_user.getUid())
                .child(userUserID)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cancelFollowers1();
                    }
                });
    }

    private void cancelFollowers1() {
        FirebaseDatabase.getInstance()
                .getReference("followers")
                .child(userUserID)
                .child(current_user.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setUnFollowing();
                    }
                });
    }

    private void setFollowing() {
        mfollowBtn.setVisibility(View.GONE);
        mUnfollowBtn.setVisibility(View.VISIBLE);
    }

    private void setUnFollowing() {
        mfollowBtn.setVisibility(View.VISIBLE);
        mUnfollowBtn.setVisibility(View.GONE);
    }

    private void getFollowersCount() {
        Log.e(TAG, "entered getFollowers count");
        followerscount = 0;
        myFollowersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followerscount = dataSnapshot.getChildrenCount();
                mfollowers.setText(String.valueOf(followerscount));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        getFollowingCount();
    }

    private void getFollowingCount() {
        followingcount = 0;
        myFollowingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // Log.e(TAG, "Following datasnapshot:" + dataSnapshot);
                //Log.e(TAG, "Following datasnapshot children:" + dataSnapshot.getChildrenCount());

                long followingcount = dataSnapshot.getChildrenCount();
                Log.e(TAG, "Value is: " + followingcount);
                mfollowing.setText(String.valueOf(followingcount));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        finished();
    }

    private void finished() {
        Log.e(TAG, "Finished counting");
    }

    private void getBackgroundImage(String idForBackground){
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (idForBackground != null) {
            //Log.e(TAG, "Background Uri selected" +fileUri);
            backgroundRef = mStorageRef.child("backgroundPictures").child(idForBackground);
            backgroundRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e(TAG,"down uri: "+uri);
                            ImageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(getApplicationContext()).load(uri).into(ImageViewBackground);
                        }

                    });
        }
    }

    private void call(){
        userAdapter = new UserFeedAdapter(UserVideoId, UserFeedDetails, mListener, glideRequests);
        view_UserFeed.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }


    private void setUpRecyclerView(){
        /*feedRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (userVideoList.contains(document.getId())) {
                        UserVideoId.add(document.toObject(Video.class));
                    }
                }
                call();
            }
        });*/
        Query queryRef = feedRef.orderBy("timestamp", Query.Direction.DESCENDING);

        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (userVideoList.contains(document.getId())) {
                        UserVideoId.add(document.toObject(Video.class));
                    }
                }
                call();
            }
        });

    }


    private void getUserFeed(String cUserId) {

        FirebaseDatabase.getInstance().getReference("UserFeed")
                .child(cUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for(DataSnapshot each : dataSnapshot.getChildren()){
                                userVideoList.add(String.valueOf(each.getKey()));
                            }
                        }
                        setUpRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void getUserFeedDeatils(String cUserId) {

        FirebaseDatabase.getInstance().getReference("UserFeed")
                .child(cUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for(DataSnapshot each : dataSnapshot.getChildren()) {
                                Feed feed = each.getValue(Feed.class);
                                feed.setFeedvideoId(each.getKey());

                                if(each.child("Likes").exists()) {
                                    feed.setFeedLike(each.child("Likes").getValue().toString());
                                }
                                else {
                                    feed.setFeedLike("N");
                                }

                                if(each.child("Repost").exists()) {
                                    feed.setFeedRepost(each.child("Repost").getValue().toString());
                                }
                                else {
                                    feed.setFeedRepost("N");
                                }

                                UserFeedDetails.add(feed);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

   private void getUserUploadVideoId(String cUserId){

        videoIdRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (cUserId.contains(document.getId())) {
                       userUploadVideoList.add( document.get("videos").toString());


                    }
                }
                setUpRecyclerViewUpload();
            }
        });

    }

    private void setUpRecyclerViewUpload(){

     Query queryRef = feedRef.orderBy("timestamp", Query.Direction.DESCENDING);
        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(userUploadVideoList.size() > 0) {
                        if (userUploadVideoList.get(0).contains(document.getId())) {
                            UserUploadVideoId.add(document.toObject(Video.class));
                            //Log.e(TAG,"video uploaded by user desc 3: "+ UserUploadVideoId);
                        }
                    }

                }
                callVideoAdapter();
            }
        });
    }

    private void callVideoAdapter(){

        userVideoAdapter = new UserUploadVideoAdapter(UserUploadVideoId, mListener, glideRequests);
        userVideoAdapter.notifyDataSetChanged();
        view_UserUpload.setAdapter(userVideoAdapter);
    }

    public void showOverflow(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.video_overflow_menu);
        popupMenu.show();
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

    /**
     * Handles back button on toolbar
     *
     * @return true if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == mfollowBtn) {
            addFollowers();
        }
        if (view == mUnfollowBtn) {
            cancelFollowers();
        }
        if (view == mEditProfile) {
            Intent accountPage = new Intent(this, Account.class);
            accountPage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
            startActivity(accountPage);
        }
    }
}