package com.hitstreamr.hitstreamrbeta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Math.toIntExact;

public class VideoPlayer extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "PlayerActivity";

    // bandwidth meter to measure and estimate bandwidth
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    //ExoPlayer
    private ExoPlayer player;
    private PlayerView playerView;
    private ComponentListener componentListener;

    //Layout
    private LinearLayout DescLayout;
    private LinearLayout hideFullLayout;

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


    //CircleImageView
    private CircleImageView artistProfPic;
    StorageReference artistProfReference;

    //Video URI
    private Uri videoUri;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = false;
    FirebaseUser currentFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference videoIdRef = db.collection("ArtistVideo");
    private ArrayList<String> userUploadVideoList;

    private Boolean VideoLiked = false;
    private Boolean VideoReposted = false;
    private Long VideoLikesCount;
    private Long VideoRepostCount;
    private Long VideoViewCount;
    PlayerControlView controlView;


    private boolean collapseVariable = true;
    private boolean uploadbyUser = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vid = getIntent().getParcelableExtra("VIDEO");

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

        vid = getIntent().getParcelableExtra("VIDEO");

        credit = getIntent().getStringExtra("CREDIT");
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

        componentListener = new ComponentListener();
        playerView = findViewById(R.id.artistVideoPlayer);

        //Linear Layout
        DescLayout = findViewById(R.id.DescriptionLayout);
        hideFullLayout = findViewById(R.id.hideFullscreenLayount);

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

        checkLikes();
        checkRepost();
        checkLikesCount();
        checkRepostCount();
        checkViewCount();

        videoUri = Uri.parse(vid.getUrl());

        context = this;


        contributorTextViews = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Videos")
                .whereEqualTo("videoId", vid.getVideoId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<HashMap<String,String>> temp = (ArrayList<HashMap<String,String>>) document.get("contributors");



                               for(HashMap<String,String> contributor : temp){
                                   Log.d(TAG, contributor.get("contributorName") + " " + contributor.get("percentage")+ " " + contributor.get("type"));
                                   TextView TVtemp = new TextView(context);
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

                               //remove extra ,0
                                TextView last = contributorTextViews.get(contributorTextViews.size()-1);
                                last.setText(last.getText().toString().substring(0,last.getText().toString().length()-2));
                                contributorTextViews.set(contributorTextViews.size()-1,last);

                                for(TextView tv : contributorTextViews){
                                    contributorView.addView(tv);
                                }


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        TextViewDate.setText(df.format(vid.getTimestamp()));


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
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            hideFullLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    playerView.getLayoutParams();

            Log.e(TAG, "LANDSCAPE" + params.height);
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            playerView.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    playerView.getLayoutParams();
            Log.e(TAG, "POTRAIT" + params.height);
            hideFullLayout.setVisibility(View.VISIBLE);
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
        MediaControlLayout = controlView.findViewById(R.id.playerControlLayout);

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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                playerView.getLayoutParams();
        Log.e(TAG, "POTRAIT" + params.height);
        hideFullLayout.setVisibility(View.VISIBLE);
        //unhide your objects here.
        params.width = params.MATCH_PARENT;
        params.height = 575;
        playerView.setLayoutParams(params);
    }


    private void openFullscreenDialog() {

        fullscreenExapndBtn.setVisibility(View.GONE);
        fullscreenShrinkBtn.setVisibility(View.VISIBLE);

        hideFullLayout.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                playerView.getLayoutParams();

        Log.e(TAG, "LANDSCAPE" + params.height);
        params.width = params.MATCH_PARENT;
        params.height = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
    }


    private void initializePlayer() {
        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
            player.addListener(componentListener);
            //player.addVideoDebugListener(componentListener);
            //player.addAudioDebugListener(componentListener);
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
        //MediaSource mediaSource = buildMediaSource(Uri.parse(getString(R.string.media_url_dash)));
        player.prepare(mediaSource, true, false);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        //playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
        //player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
    }

    private void releasePlayer() {
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
    }

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
        Toast.makeText(VideoPlayer.this, "You reposted", Toast.LENGTH_SHORT).show();
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


    private class ComponentListener extends Player.DefaultEventListener implements
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
                            timerCounter();
                        }
                    }

                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    if (!(Integer.parseInt(currentCreditVal) > 0)) {
                        callPurchase();
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
                collapseVariable = true;
            } else if (collapseVariable) {
                //TextViewVideoDescription.setVisibility(View.VISIBLE);
                collapseDecriptionBtn.setBackground(getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                DescLayout.setVisibility(View.VISIBLE);
                collapseVariable = false;
            }
        }


        if (view == likeBtn) {
            if (!VideoLiked) {
                likeVideo();
            } else {
                cancelLikeVideo();
            }
        }

        if (view == repostBtn) {
            Log.e(TAG, "repost clicked");
            if (!VideoReposted) {
                repostVideo();
            } else {
                cancelRepostVideo();
            }
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
            Intent playListAct = new Intent(getApplicationContext(), AddToPlaylsit.class);
            playListAct.putExtra("VideoId", vid.getVideoId());
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
}