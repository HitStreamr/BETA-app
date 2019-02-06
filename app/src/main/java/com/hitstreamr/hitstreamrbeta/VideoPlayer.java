package com.hitstreamr.hitstreamrbeta;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hitstreamr.hitstreamrbeta.Authentication.SignInActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Math.toIntExact;

public class VideoPlayer extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, GestureDetector.OnGestureListener, PlayerServiceCallback {
    private static final String TAG = "PlayerActivity";

    // bandwidth meter to measure and estimate bandwidth
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String DEBUG_TAG = "DEBUG_DRAG";
    private static final String CHANNEL_ID = "HS_VIDEO_PLAYER";
    private static final int CHANNEL_NAME = 987654321;
    private static final int NOTIFICATION_ID = 1023456789;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection;

    private VideoPlayerService mService;
    private boolean mBound;
    //ExoPlayer
    //private ExoPlayer player;
    private PlayerView playerView;
    //private ComponentListener componentListener;

    //Layout
    private LinearLayout DescLayout;
    private LinearLayout hideFullLayout;
    private LinearLayout hideToolbarLayout;

    //Button
    private Button collapseDecriptionBtn;

    //ImageButton
    private ImageButton likeBtn;
    private ImageButton repostBtn;
    private ImageButton addToPlaylistBtn;
    private ImageView fullscreenExapndBtn;
    private ImageView fullscreenShrinkBtn;

    //TextView
    private TextView TextViewVideoDescription;
    private TextView TextViewTitle;
    private TextView artistNameBold;
    private TextView artistName;
    private TextView TextViewDate;
    private TextView TextViewLikesCount;
    private TextView TextViewRepostCount;
    private TextView TextViewViewCount;
    private TextView follow;
    private TextView unfollow;
    private RelativeLayout MediaControlLayout;
    private TextView showMore;

    private RelativeLayout MediaContolLayout;

    //CircleImageView
    private CircleImageView artistProfPic;
    StorageReference artistProfReference;

    //Video URI
    private Uri videoUri;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    FirebaseUser currentFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference videoIdRef = db.collection("ArtistVideo");
    private CollectionReference videocontributorRef = db.collection("Videos");


    private ArrayList<String> userUploadVideoList;

    private Boolean VideoLiked = false;
    private Boolean VideoReposted = false;
    private Long VideoLikesCount;
    private Long VideoRepostCount;
    private Long VideoViewCount;
    PlayerControlView controlView;


    private boolean collapseVariable = true;
    private boolean uploadbyUser = false;
    private boolean iscontributor = false;
    private ArrayList<String> userContributor;

    /*private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;*/




    Video vid;

    private String creditValue="";

    private Button confirmBtn, cancelBtn;
    private TextView messgText;
    private LayoutInflater mInflater;
    private DatabaseReference myRefview;

    private boolean runCheck = false;
    private String currentCreditVal;

    private String credit;
    private String sTimeStamp = "";

    //Comment stuff
    private String accountType;
    private String username;
    private FirebaseUser current_user;
    private Uri photoURL;
    private TextView allCommentsCount, recent_username, recent_message;
    private long totalComments = 0;

    //private VideoContributorAdapter contributorAdapter;
    private ArrayList<TextView> contributorTextViews;
    private LinearLayout contributorView;
    private Context context;

    // Related videos & auto-play
    private RelatedVideosAdapter relatedVideosAdapter;
    private boolean autoplay_switchState = false;
    private boolean wholeVideo = false;

    private GestureDetectorCompat mDetector;

    private Intent serviceIntent;
    private ImageView minimizeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vid = getIntent().getParcelableExtra("VIDEO");

        userUploadVideoList = new ArrayList<>();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("VideoLikes");

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Profile Picture
        if (current_user.getPhotoUrl() != null) {
            CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            //CircleImageView circleImageView_comment = findViewById(R.id.profilePicture_comment);
            //circleImageView.setVisibility(View.VISIBLE);
            photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
            //Glide.with(getApplicationContext()).load(photoURL).into(circleImageView_comment);
        }

        FirebaseDatabase.getInstance().getReference("Credits")
                .child(current_user.getUid()).child("creditvalue")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentCredit = dataSnapshot.getValue(String.class);
                Log.e(TAG, "Main activity credit val " + currentCredit);
                if (!Strings.isNullOrEmpty(currentCredit)) {

                    currentCreditVal = currentCredit;
                    if(getIntent().getBooleanExtra("RETURN", false)){
                        restartConnection();
                    }else {
                        startConnection();
                    }
                } else {
                    //userCredits.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        serviceIntent = new Intent(this, VideoPlayerService.class);
        serviceIntent.putExtra("CREDITS",credit);
        startService(serviceIntent);
        (new Handler()).postDelayed(this::binder, 500);
        userUploadVideoList = new ArrayList<>();

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("VideoLikes");

        getUserType();
        getRecentComment();


        // Get current account's username
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(accountType)
                .child(current_user.getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(eventListener);

        allCommentsCount = findViewById(R.id.allComments_count);
        // Get comment counts
        DatabaseReference databaseReference_comments = FirebaseDatabase.getInstance().getReference("Comments")
                .child(vid.getVideoId());
        ValueEventListener eventListener_comments = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalComments = dataSnapshot.getChildrenCount();
                allCommentsCount.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference_comments.addListenerForSingleValueEvent(eventListener_comments);

        // Get reply counts
        DatabaseReference databaseReference_replies = FirebaseDatabase.getInstance().getReference("CommentReplies")
                .child(vid.getVideoId());
        ValueEventListener eventListener_replies = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int x = toIntExact(dataSnapshot.getChildrenCount());
                int temp = Integer.parseInt(allCommentsCount.getText().toString());
                int y = x + temp;
                allCommentsCount.setText(y + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference_replies.addListenerForSingleValueEvent(eventListener_replies);

        //componentListener = new ComponentListener();
        playerView = findViewById(R.id.artistVideoPlayer);

        //Linear Layout
        DescLayout = findViewById(R.id.DescriptionLayout);
        hideFullLayout = findViewById(R.id.hideFullscreenLayount);
        hideToolbarLayout = findViewById(R.id.toolbarLayout);

        //Profile Picture
        artistProfPic = findViewById(R.id.artistProfilePicture);

        //Button
        collapseDecriptionBtn = findViewById(R.id.collapseDescription);

        //ImageButton
        likeBtn = findViewById(R.id.fave);
        repostBtn = findViewById(R.id.rePostVideo);
        addToPlaylistBtn = findViewById(R.id.addToPlaylist);

        //TextView
        TextViewVideoDescription = findViewById(R.id.videoDescription);
        TextViewVideoDescription.setText(vid.getDescription());
        TextViewLikesCount = findViewById(R.id.videoLikes);
        TextViewRepostCount = findViewById(R.id.repostCount);
        TextViewDate = findViewById(R.id.publishDate);
        TextViewViewCount = findViewById(R.id.TextViewViewCount);
        showMore = findViewById(R.id.showMoreLess);

        TextViewTitle = findViewById(R.id.Title);
        TextViewTitle.setText(vid.getTitle());

        //LinearLayout
        contributorView = findViewById(R.id.contributorLayout);

        artistNameBold = findViewById(R.id.artistNameBold);
        artistName = findViewById(R.id.Artist);
        artistName.setText(vid.getUsername());
        artistNameBold.setText(vid.getUsername());

        artistProfReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitstreamr-beta.appspot.com/profilePictures/" + vid.getUserId());
        follow = findViewById(R.id.followText);
        unfollow = findViewById(R.id.unfollowText);

        follow.setOnClickListener(this);
        unfollow.setOnClickListener(this);

        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.GONE);

        //set up UI for following
        checkFollowing(new OnDataReceiveCallback() {
            @Override
            public void onFollowChecked(boolean following) {
                if(following){
                    //if following == true
                    follow.setVisibility(View.GONE);
                    unfollow.setVisibility(View.VISIBLE);
                }else{
                    //if following == false
                    follow.setVisibility(View.VISIBLE);
                    unfollow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCheckUpdateFailed() {
                Log.e("TAG", "Follow Check Failed");
            }
        });

        artistProfReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitstreamr-beta.appspot.com/profilePictures/" + vid.getUserId());

        if (artistProfReference == null) {
            Glide.with(getApplicationContext()).load(R.mipmap.ic_launcher_round).into(artistProfPic);
        } else {
            Glide.with(getApplicationContext()).load(artistProfReference).into(artistProfPic);
        }

        //Listeners
        collapseDecriptionBtn.setOnClickListener(this);
        likeBtn.setOnClickListener(this);
        repostBtn.setOnClickListener(this);
        addToPlaylistBtn.setOnClickListener(this);

        initFullscreenButton();
        initMiniButton();

        checkLikes();
        checkRepost();
        checkLikesCount();
        checkRepostCount();
        checkViewCount();

        videoUri = Uri.parse(vid.getUrl());

        context = this;


        contributorTextViews = new ArrayList<>();
        userContributor = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Videos")
                .whereEqualTo("videoId", vid.getVideoId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<HashMap<String,String>> temp = (ArrayList<HashMap<String,String>>) document.get("contributors");



                               for(HashMap<String,String> contributor : temp){
                                   Log.d(TAG, contributor.get("contributorName") + " " + contributor.get("percentage")+ " " + contributor.get("type"));
                                   TextView TVtemp = new TextView(context);
                                   userContributor.add(contributor.get("contributorName"));
                                   TVtemp.setText(contributor.get("contributorName") + "(" +  contributor.get("type") + ")"+", ");
                                   TVtemp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                           LinearLayout.LayoutParams.WRAP_CONTENT));

                                   TVtemp.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(context, Profile.class);
                                           intent.putExtra("TYPE", context.getString(R.string.type_artist));
                                           intent.putExtra("artistUsername", contributor.get("contributorName"));
                                           context.startActivity(intent);
                                       }
                                   });

                                   contributorTextViews.add(TVtemp);
                               }

                               if(contributorTextViews.size()>0) {

                               //remove extra ,0
                                TextView last = contributorTextViews.get(contributorTextViews.size()-1);
                                last.setText(last.getText().toString().substring(0,last.getText().toString().length()-2));
                                contributorTextViews.set(contributorTextViews.size()-1,last);

                                for(TextView tv : contributorTextViews){
                                    contributorView.addView(tv);
                                }
                               }


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        TextViewDate.setText(df.format(vid.getTimestamp().toDate()));

        // Getting the credit value of user. If credits available initialize normal video else initialize clipped video of 15 sec
        if (credit != null)
            currentCreditVal = credit;
        else
            currentCreditVal = "0";

        mDetector = new GestureDetectorCompat(this,this);

        // Populate the recycler view with videos of same genre based on views
        loadRelatedVideos();

        // Save auto-play switch state for users
        Switch autoplay_switch = findViewById(R.id.autoPlay_videoPlayer);
        autoplay_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    autoplay_switchState = true;
                } else {
                    autoplay_switchState = false;
                }
                SharedPreferences preferences = getSharedPreferences("UserSwitchPrefs", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("autoplay_switch", b);
                editor.apply();
            }
        });

        SharedPreferences preferences = getSharedPreferences("UserSwitchPrefs", 0);
        boolean autoplay_state = preferences.getBoolean("autoplay_switch", false);
        autoplay_switch.setChecked(autoplay_state);
    }

    private void restartConnection(){
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                VideoPlayerService.LocalBinder binder = (VideoPlayerService.LocalBinder) service;
                mService = binder.getService();
                mService.setCallbacks(VideoPlayer.this);
                mBound = true;
                mService.resetPlayer();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
                mService = null;

            }
        };
    }


    private void startConnection(){
        /* Video Player Service */
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                VideoPlayerService.LocalBinder binder = (VideoPlayerService.LocalBinder) service;
                mService = binder.getService();
                mService.setCallbacks(VideoPlayer.this);
                mBound = true;

                readData(new MyCallback() {
                    @Override
                    public void onCallback(ArrayList value) {


                        iscontributor = userContributor.contains(username);
                        Log.e(TAG, "player user contributor name " + userContributor.size() + " user " + username);
                        Log.e(TAG, "player user contributor iscontributor " + iscontributor);

                        if (value.size() > 0) {
                            // Log.e(TAG, "player before inside callback "+value);
                            checkuploaded();
                            //checkforContributor();
                        }
                        if (uploadbyUser) {
                            //Log.e(TAG, "player before inside else if  "+uploadbyUser);
                            wholeVideo = true;
                            mService.setPlayer(vid, false, currentCreditVal);
                        } else if (iscontributor) {
                            Log.e(TAG, "player before inside else if  contributor" + iscontributor);
                            wholeVideo = true;
                            mService.setPlayer(vid, false, currentCreditVal);
                        } else if (Integer.parseInt(currentCreditVal) > 0) {
                            //Log.e(TAG, "player before inside if ");
                            // Log.e(TAG, "player before initializePlayer success ");
                            wholeVideo = true;
                            mService.setPlayer(vid, false, currentCreditVal);
                        } else {
                            mService.setPlayer(vid, true, currentCreditVal);
                            runCheck = true;
                        }
                    }
                });

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
                mService = null;

            }
        };
    }

    @Override
    public void setPlayerView() {
        //ClippingMediaSource clippingSource = new ClippingMediaSource(mediaSource1, 0, 15_000_000);
        //player.prepare(clippingSource, true, false);
        Log.d(TAG, "Set PlayerView");
        playerView.setPlayer(VideoPlayerService.player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
    }

    @Override
    public void stopPlayer(){
        //called when notification is cancelled
        this.finish();
    }
    
    /**
     * Load related videos to the current displayed one based on its genre, and sorted based on views.
     */
    private void loadRelatedVideos() {
        RecyclerView recyclerView = findViewById(R.id.relatedVideos_RCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Video> videoList = new ArrayList<>();
        relatedVideosAdapter = new RelatedVideosAdapter(videoList, getApplicationContext(), getIntent());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").orderBy("views",
                com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc.get("genre").equals(vid.getGenre())) {
                                if (!vid.getVideoId().equals(doc.getId())) {
                                    videoList.add(doc.toObject(Video.class));
                                    relatedVideosAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(relatedVideosAdapter);
                                }
                            }
                        }
                    }
                });
    }

    //plays the next video when the Service signals
    public void autoPlayNext(){
        autoPlayNextVideo(relatedVideosAdapter.getFirstFromList());
    }

    /**
     * Play the next video on the list.
     * @param nextVideo next video
     */
    private void autoPlayNextVideo(Video nextVideo) {
        Intent nextVideoPage = new Intent(getApplicationContext(), VideoPlayer.class);
        nextVideoPage.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        nextVideoPage.putExtra("VIDEO", nextVideo);
        nextVideoPage.putExtra("CREDIT", currentCreditVal);
        startActivity(nextVideoPage);
    }

    public interface MyCallback {
        void onCallback(ArrayList value);
    }

    /*public void readData(MyCallback myCallback) {
        FirebaseDatabase.getInstance().getReference("Credits")
                .child(currentFirebaseUser.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        creditValue = dataSnapshot.getValue(String.class);
                        myCallback.onCallback(creditValue);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }*/


    private void binder(){
        Log.d(TAG, "Bind Service");
        bindService(serviceIntent,mConnection, 0);
    }

    // After watching 15 sec clipped video user is prompted to purchase credits on confirmation redirected to purchase credits page
    public void callPurchase(){

        setContentView(R.layout.activity_confirm);
        //Button
        confirmBtn = findViewById(R.id.confirm);
        confirmBtn.setOnClickListener(this);

        cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(this);

        messgText = findViewById(R.id.MessageText);
        messgText.setText("Please purchase credits to watch videos");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .4));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void updateCreditText(String credit) {
        Log.d(TAG, "Credits Decresed:  " + credit);
    }

    // This method is to compare the current time with 4 hrs specified time limit for particular user
    private void checkTimeStamp() throws ParseException{
        //Log.e(TAG, "Your video date checktimestamp" + sTimeStamp);
        if(!Strings.isNullOrEmpty(sTimeStamp)) {
            Calendar now = Calendar.getInstance();
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(sTimeStamp);
            Calendar c = Calendar.getInstance();
            c.setTime(parsedDate);
           // Log.e(TAG, "Your video date format from db" + c.getTime());
            //Log.e(TAG, "Your video date format from db now" + now.getTime());
            if (now.getTime().after(c.getTime())) {
              //  Log.e(TAG, "Your video date format after checking inside if");
                updatevideoview();
                updateCreditValue();
            }
        }
    }

    // to update the user id and time frame (current time + 4hrs) values into VideoView DB
    private void updatevideoview() throws ParseException {

       /* long currentTimeMillis = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(currentTimeMillis);
        String currentTime = dateFormat.format(date);
        Log.e(TAG, "Your video date format :" + currentTime);*/

        Calendar now = Calendar.getInstance();
        Calendar tmp = (Calendar) now.clone();
        tmp.add(Calendar.HOUR_OF_DAY, 24);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String strDate = simpleFormat.format(tmp.getTime());
        Log.e(TAG, "Your video date format after" +strDate);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("VideoViews").child(vid.getVideoId());
      /*  Map<String, Object> value = new HashMap<>();
        value.put("UserId", currentFirebaseUser.getUid());
        value.put("timestamp", System.currentTimeMillis());
        ref.setValue(value)*/
        ref.child(currentFirebaseUser.getUid()).child("TimeLimit").setValue(strDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    //To reduce 1 user credit for watching a video
    private void updateCreditValue(){
        if(!uploadbyUser && !(iscontributor)) {
            if (!Strings.isNullOrEmpty(currentCreditVal)) {
                int creditval = Integer.parseInt(currentCreditVal);
                creditval = creditval - 1;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Credits").child(currentFirebaseUser.getUid());
                ref.child("creditvalue").setValue(String.valueOf(creditval))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
            }
        }
    }

   // private void getUserUploadVideoId(){
   public void readData(MyCallback myCallback) {
       String cUser = currentFirebaseUser.getUid();

        videoIdRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                     if (cUser.contains(document.getId())) {
                        userUploadVideoList.add( document.get("videos").toString());
                    }
                }
               // Log.e(TAG, "player user uploaded userUploadVideoList "+userUploadVideoList);
                myCallback.onCallback(userUploadVideoList);
            }
        });
       // Log.e(TAG, "player user uploaded userUploadVideoList "+userUploadVideoList);

        //checkuploaded();
    }

    public void checkuploaded(){
        Log.e(TAG, "player user uploaded video list "+userUploadVideoList);
        if (userUploadVideoList.get(0).contains(vid.getVideoId())) {
            uploadbyUser = true;
            Log.e(TAG, "player user uploaded video list boolean "+uploadbyUser);

        }

        else{
            uploadbyUser = false;
            Log.e(TAG, "player user uploaded video list boolean "+uploadbyUser);
        }

    }


    public void showVideoPlayerOverflow(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.video_player_overflow_menu);
        popupMenu.show();
    }

    private void checkLikes() {
        FirebaseDatabase.getInstance().getReference("VideoLikes")
                .child(vid.getVideoId())
                .child(currentFirebaseUser.getUid())
                .child("userId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        if (dataSnapshot.exists()) {
                            if (value.equals(currentFirebaseUser.getUid())) {
                                int fillColor = Color.parseColor("#ff13ae");
                                likeBtn.setColorFilter(fillColor);
                                //Log.e(TAG, "Video not Liked");
                                VideoLiked = true;
                            }
                        } else {
                            VideoLiked = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void checkLikesCount() {
        FirebaseDatabase.getInstance().getReference("VideoLikes")
                .child(vid.getVideoId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            VideoLikesCount = dataSnapshot.getChildrenCount();
                            //VideoLikesCount = 1100L;
                            String temp = formatt(VideoLikesCount);
                            Log.e(TAG, "Video Count likes : " + temp);
                            TextViewLikesCount.setText(temp);
                        }else{
                            VideoLikesCount = 0l;
                            String temp = formatt(VideoLikesCount);
                            Log.e(TAG, "Video Count likes : " + temp);
                            TextViewLikesCount.setText(temp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }


    private void checkRepost() {
        FirebaseDatabase.getInstance().getReference("Repost")
                .child(vid.getVideoId())
                .child(currentFirebaseUser.getUid())
                .child("userId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        if (dataSnapshot.exists()) {
                            if (value.equals(currentFirebaseUser.getUid())) {
                                int fillColor = Color.parseColor("#ff13ae");
                                repostBtn.setColorFilter(fillColor);
                                Log.e(TAG, "Video not Reposted");
                                VideoReposted = true;
                            }
                        } else {
                            VideoReposted = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void checkRepostCount() {
        FirebaseDatabase.getInstance().getReference("Repost")
                .child(vid.getVideoId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            VideoRepostCount = dataSnapshot.getChildrenCount();
                            String temp = formatt(VideoRepostCount);
                            Log.e(TAG, "Repost Count : " + temp);
                            TextViewRepostCount.setText(temp);
                        }else{
                            VideoRepostCount = 0l;
                            String temp = formatt(VideoRepostCount);
                            Log.e(TAG, "Video Count reposts : " + temp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Get the video's view count.
     */
    private void checkViewCount() {
        FirebaseDatabase.getInstance().getReference("VideoViews")
                .child(vid.getVideoId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            VideoViewCount = dataSnapshot.getChildrenCount();
                            String temp = formatt(VideoViewCount);
                            Log.e(TAG, "View Count : " + temp);
                            TextViewViewCount.setText(temp);
                        }else{
                            VideoViewCount = 0l;
                            String temp = formatt(VideoViewCount);
                            Log.e(TAG, "Video Count reposts : " + temp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
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

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        restartConnection();
        binder();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mConnection != null && mBound){
            unbindService(mConnection);
            mBound = false;
            mService.setCallbacks(null);
            mService = null;
        }
    }

    private void releasePlayer(){
        if (mConnection != null && mBound){
            unbindService(mConnection);
            mBound = false;
            mService.stopVideoService();
            mService = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            fullscreenExapndBtn.setVisibility(View.GONE);
            fullscreenShrinkBtn.setVisibility(View.VISIBLE);


            hideSystemUi();

            hideFullLayout.setVisibility(View.GONE);
            hideToolbarLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    playerView.getLayoutParams();

            Log.e(TAG, "LANDSCAPE" + params.height);
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            playerView.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullscreenExapndBtn.setVisibility(View.VISIBLE);
            fullscreenShrinkBtn.setVisibility(View.GONE);

            hideSystemUi();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    playerView.getLayoutParams();
            Log.e(TAG, "POTRAIT" + params.height);
            hideFullLayout.setVisibility(View.VISIBLE);
            hideToolbarLayout.setVisibility(View.VISIBLE);
            //unhide your objects here.
            params.width = params.MATCH_PARENT;
            params.height = 575;
            playerView.setLayoutParams(params);
        }
    }

    private void initFullscreenButton() {

        controlView = playerView.findViewById(R.id.exo_controller);
        fullscreenExapndBtn = controlView.findViewById(R.id.fullscreen_expand);
        fullscreenShrinkBtn = controlView.findViewById(R.id.fullscreen_shrink);
        fullscreenShrinkBtn.setVisibility(View.GONE);
        MediaContolLayout = controlView.findViewById(R.id.playerControlLayout);

        fullscreenExapndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullscreenDialog();
            }
        });

        fullscreenShrinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFullscreenDialog();
            }
        });
    }

    private void closeFullscreenDialog() {

        fullscreenExapndBtn.setVisibility(View.VISIBLE);
        fullscreenShrinkBtn.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                playerView.getLayoutParams();
        Log.e(TAG, "POTRAIT" + params.height);
        hideFullLayout.setVisibility(View.VISIBLE);
        hideToolbarLayout.setVisibility(View.VISIBLE);
        //unhide your objects here.
        params.width = params.MATCH_PARENT;
        params.height = 575;
        playerView.setLayoutParams(params);
    }


    private void openFullscreenDialog() {

        fullscreenExapndBtn.setVisibility(View.GONE);
        fullscreenShrinkBtn.setVisibility(View.VISIBLE);


        hideSystemUi();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        hideFullLayout.setVisibility(View.GONE);
        hideToolbarLayout.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                playerView.getLayoutParams();

        Log.e(TAG, "LANDSCAPE" + params.height);
        params.width = params.MATCH_PARENT;
        params.height = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
    }

    /*private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            //player.removeVideoDebugListener(componentListener);
            //player.removeAudioDebugListener(componentListener);
            player.release();
            player = null;
        }
    }*/

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void likeVideo() {
        Log.e(TAG, "Your video Id is:" + vid.getVideoId());
        //Toast.makeText(VideoPlayer.this, "You liked" + vid.getVideoId(), Toast.LENGTH_SHORT).show();
        String ttt = "true";

        FirebaseDatabase.getInstance()
                .getReference("VideoLikes")
                .child(vid.getVideoId())
                .child(currentFirebaseUser.getUid())
                .child("userId")
                .setValue(currentFirebaseUser.getUid())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finishedLike();
                        int fillColor = Color.parseColor("#ff13ae");
                        likeBtn.setColorFilter(fillColor);
                        VideoLiked = true;
                        Log.e(TAG, "Video is liked " + VideoLiked);
                    }
                });
    }

    private void cancelLikeVideo() {
        FirebaseDatabase.getInstance()
                .getReference("VideoLikes")
                .child(vid.getVideoId())
                .child(currentFirebaseUser.getUid())
                /*.child("userID")*/
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int fillColor = Color.parseColor("#000000");
                        likeBtn.setColorFilter(fillColor);
                        VideoLiked = false;
                        Log.e(TAG, "Video like is cancelled " + VideoLiked);
                    }
                });
    }

    private void userLikedVideo() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String strDate = simpleFormat.format(now.getTime());
        FirebaseDatabase.getInstance()
                .getReference("UserFeed")
                .child(currentFirebaseUser.getUid())
                .child(vid.getVideoId())
               // .child("TimeStamp")
                //.setValue(strDate)
                .child("Likes")
                .setValue("Y")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "User Video is reposted ");
                    }
                });
    }

    private void cancelUserLikedVideo() {
        FirebaseDatabase.getInstance()
                .getReference("UserFeed")
                .child(currentFirebaseUser.getUid())
                .child(vid.getVideoId())
                .child("Likes")
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }


    private void finishedLike() {
        /*FirebaseDatabase.getInstance().getReference("VideoLikes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long value = dataSnapshot.getChildrenCount();
                        Log.e(TAG, "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });*/
        Toast.makeText(VideoPlayer.this, "You liked", Toast.LENGTH_SHORT).show();
    }

    private void repostVideo() {
        Log.e(TAG, "Your video Id is:" + vid.getVideoId());
        //Toast.makeText(VideoPlayer.this, "You liked" + vid.getVideoId(), Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance()
                .getReference("Repost")
                .child(vid.getVideoId())
                .child(currentFirebaseUser.getUid())
                .child("userId")
                .setValue(currentFirebaseUser.getUid())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finishedRepost();
                        int fillColor = Color.parseColor("#ff13ae");
                        repostBtn.setColorFilter(fillColor);
                        VideoReposted = true;
                        Log.e(TAG, "Video is reposted " + VideoReposted);
                    }
                });
    }

    private void cancelRepostVideo() {
        FirebaseDatabase.getInstance()
                .getReference("Repost")
                .child(vid.getVideoId())
                .child(currentFirebaseUser.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int fillColor = Color.parseColor("#000000");
                        repostBtn.setColorFilter(fillColor);
                        VideoReposted = false;
                        Log.e(TAG, "Video Repost is cancelled " + VideoReposted);
                    }
                });
    }

    private void userRepostVideo() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String strDate = simpleFormat.format(now.getTime());
       FirebaseDatabase.getInstance()
                .getReference("UserFeed")
                .child(currentFirebaseUser.getUid())
                .child(vid.getVideoId())
               .child("Repost")
                .setValue("Y")
               // .child("TimeStamp")
               //.setValue(strDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "User Video is reposted ");
                    }
                });
    }

    private void cancelUserRepostVideo() {
        FirebaseDatabase.getInstance()
                .getReference("UserFeed")
                .child(currentFirebaseUser.getUid())
                .child(vid.getVideoId())
                .child("Repost")
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void registerWatchLater() {
        FirebaseDatabase.getInstance()
                .getReference("WatchLater")
                .child(currentFirebaseUser.getUid())
                .child(vid.getVideoId())
                .child("VideoId")
                .setValue(vid.getVideoId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.e(TAG, "Video is added to Watch Later ");
                        finishedWatchLater();
                    }
                });
    }

    private void finishedWatchLater() {
        Toast.makeText(VideoPlayer.this, "Video has been added to Watch Later", Toast.LENGTH_SHORT).show();

    }

    private void finishedRepost() {
        Toast.makeText(VideoPlayer.this, "Video has been reposted.", Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.reportVideo:
                Intent reportAct = new Intent(getApplicationContext(), ReportVideoPopup.class);
                reportAct.putExtra("VideoId", vid.getVideoId());
                startActivity(reportAct);
                break;
            case R.id.addWatchLater:
                registerWatchLater();
                break;
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


   /*private class ComponentListener extends Player.DefaultEventListener implements
            VideoRendererEventListener, AudioRendererEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    if (!(player.equals(""))) {
                        if (player.getCurrentPosition() == 0) {
                            if (!(uploadbyUser) && !iscontributor){
                                timerCounter();
                            }
                        }
                    }

                    break;

                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    if (!(Integer.parseInt(currentCreditVal) > 0)) {
                        callPurchase();
                    }

                    // Play the next video ONLY IF we have finished playing the whole video
                    // and the auto-play switch is turned on
                    else if (wholeVideo & autoplay_switchState) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                autoPlayNextVideo(relatedVideosAdapter.getFirstFromList());
                            }
                        }, 1500);
                    }
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }

        // Implementing VideoRendererEventListener.

        @Override
        public void onVideoEnabled(DecoderCounters counters) {
            // Do nothing.
        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            // Do nothing.
        }

        @Override
        public void onVideoInputFormatChanged(Format format) {
            // Do nothing.
        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {
            // Do nothing.
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            // Do nothing.
        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {
            // Do nothing.
        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {
            // Do nothing.
        }

        // Implementing AudioRendererEventListener.

        @Override
        public void onAudioEnabled(DecoderCounters counters) {
            // Do nothing.
        }

        @Override
        public void onAudioSessionId(int audioSessionId) {
            // Do nothing.
        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            // Do nothing.
        }

        @Override
        public void onAudioInputFormatChanged(Format format) {
            // Do nothing.
        }

        @Override
        public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            // Do nothing.
        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {
            // Do nothing.
        }

    }

    /* Following Code
        Save Following - the former saves the user id to the artist they are following

        Save Unfollowing and Record Unfollow - the former removes the user id from the artist they are following
     */
    //TODO update these to more fault tolerant firebase updates?
    private void checkFollowing(OnDataReceiveCallback callback){
        //get where the following state would be
        // check who the user is following
        database.getReference().child("following").child(currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(vid.getUserId()).exists()){
                    Log.e(TAG, "Following");
                    callback.onFollowChecked(true);
                }else{
                    Log.e(TAG, "Not Following");
                    callback.onFollowChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCheckUpdateFailed();
            }
        });
    }
    private void saveFollowing(){
        FirebaseDatabase.getInstance().getReference("following")
                .child(currentFirebaseUser.getUid())
                .child(vid.getUserId())
                .setValue(vid.getUserId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // do the Reciprocal on Success
                        // artistFollowers -> user
                        FirebaseDatabase.getInstance().getReference("followers")
                                .child(vid.getUserId())
                                .child(currentFirebaseUser.getUid())
                                .setValue(currentFirebaseUser.getUid())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // hide/show the UI
                                        follow.setVisibility(View.GONE);
                                        unfollow.setVisibility(View.VISIBLE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //TODO Error for following failing
                                        }
                                    });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO Error for following failing

            }
        });
    }

    private void saveUnfollowing(){
        FirebaseDatabase.getInstance()
                .getReference("following")
                .child(currentFirebaseUser.getUid())
                .child(vid.getUserId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //remove user from artist's list of followers
                        FirebaseDatabase.getInstance()
                                .getReference("followers")
                                .child(vid.getUserId())
                                .child(currentFirebaseUser.getUid())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //update UI
                                        unfollow.setVisibility(View.GONE);
                                        follow.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });
    }


    @Override
    public void onClick(View view) {
        if (view == collapseDecriptionBtn) {
            if (!collapseVariable) {
                //TextViewVideoDescription.setVisibility(View.GONE);
                DescLayout.setVisibility(View.GONE);
                collapseDecriptionBtn.setBackground(getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                showMore.setText("Show more");
                collapseVariable = true;
            } else if (collapseVariable) {
                //TextViewVideoDescription.setVisibility(View.VISIBLE);
                collapseDecriptionBtn.setBackground(getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                DescLayout.setVisibility(View.VISIBLE);
                showMore.setText("Show less");
                collapseVariable = false;
            }
        }


        if (view == likeBtn) {
            if (!VideoLiked) {
                likeVideo();
                userLikedVideo();
            } else {
                cancelLikeVideo();
                cancelUserLikedVideo();
            }
        }

        if (view == repostBtn) {
            Log.e(TAG, "repost clicked");
            if (!VideoReposted) {
                repostVideo();
                userRepostVideo();
            } else {
                cancelRepostVideo();
                cancelUserRepostVideo();
            }
        }
        if(view == confirmBtn){
            finish();
            startActivity(new Intent(getApplicationContext(), CreditsPurchase.class));
        }
        else if (view == cancelBtn){
            super.onBackPressed();
        }
        // Following and Unfollowing
        if(view == follow){
            saveFollowing();
        }

        if(view == unfollow){
            saveUnfollowing();
        }

        if (view == addToPlaylistBtn) {
            Log.e(TAG, "add to Playlist clicked" + vid.getVideoId());
            Intent playListAct = new Intent(getApplicationContext(), AddToPlaylist.class);
            playListAct.putExtra("VIDEO", vid);
            playListAct.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
            startActivity(playListAct);
        }
    }


    /*
        Provides interface for the callback for Async call to Firebase
     */
    public interface OnDataReceiveCallback {
        /*
         *   Method that notifies the ui that the Data was received
        */
        void onFollowChecked(boolean following);
        void onCheckUpdateFailed();
    }


    /**
     * Handles the back button on toolbar.
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Open the comments page.
     * @param view view
     */
    public void viewAllComments(View view) {
        Intent commentPageIntent = new Intent(VideoPlayer.this, CommentPage.class);
        commentPageIntent.putExtra("VIDEO", vid);
        commentPageIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        startActivity(commentPageIntent);
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
                accountType = "ArtistAccounts";
            } else {
                accountType = "LabelAccounts";
            }
        }
        Log.e(TAG, "account type selected :"+accountType);
    }

    /**
     * Get the most recent comment for the corresponding video.
     */
    private void getRecentComment() {
        recent_username = findViewById(R.id.recent_username);
        recent_message = findViewById(R.id.recent_comment);
        // Get most recent comment
        DatabaseReference databaseReference_recentComment = FirebaseDatabase.getInstance().getReference("Comments");
        databaseReference_recentComment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(vid.getVideoId())) {
                    Query query_recentComment = databaseReference_recentComment.child(vid.getVideoId()).orderByKey().limitToLast(1);
                    query_recentComment.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            recent_username.setText(comment.getUsername());
                            recent_message.setText(comment.getMessage());
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
                } else {
                    recent_username.setText("No recent comment to display");
                    recent_message.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initMiniButton(){
        //minimizeButton = controlView.findViewById(R.id.shrink_into_backBtn);
        //minimizeButton.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
              //  makeMiniPlayer();
            //}
        //});
    }

    public void makeMiniPlayer(){
        Intent intent = new Intent(this, MainActivity.class);
        if (mConnection != null && mBound){
            unbindService(mConnection);
            mBound = false;
            mService.setCallbacks(null);
            mService = null;
        }
        intent.putExtra("MINI_VISIBLE", true);
        intent.putExtra("VIDEO", vid);
        intent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        //intent.putExtra("Playback_Position",  playbackPosition);
        //intent.putExtra("CurrentWindow", currentWindow);
        // Pass data object in the bundle and populate details activity.
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, TextViewTitle, "title");
        startActivity(intent, options.toBundle());
    }


    /**DRAG VIDEO **/
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " );
        makeMiniPlayer();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

}