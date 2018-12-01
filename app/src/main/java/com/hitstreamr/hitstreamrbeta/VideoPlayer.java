package com.hitstreamr.hitstreamrbeta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private TextView TextViewLikesCount;
    private TextView TextViewRepostCount;

    private RelativeLayout MediaContolLayout;

    //CircleImageView
    private CircleImageView artistProfPic;
    StorageReference artistProfReference;

    //Video URI
    private Uri videoUri;

    FirebaseUser currentFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private Boolean VideoLiked = false;
    private Boolean VideoReposted = false;
    private Long VideoLikesCount;
    private Long VideoRepostCount;

    PlayerControlView controlView;


    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    private boolean collapseVariable = false;

    Video vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vid = getIntent().getParcelableExtra("VIDEO");

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("VideoLikes");

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

        TextViewTitle = findViewById(R.id.Title);
        TextViewTitle.setText(vid.getTitle());

        artistNameBold = findViewById(R.id.artistNameBold);
        artistName = findViewById(R.id.Artist);
        artistName.setText(vid.getUsername());
        artistNameBold.setText(vid.getUsername());

        artistProfReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitstreamr-beta.appspot.com/profilePictures/" + vid.getUserId());

        if (artistProfReference == null) {
            Glide.with(getApplicationContext()).load(R.mipmap.ic_launcher_round).into(artistProfPic);
        } else {
            Glide.with(getApplicationContext()).load(artistProfReference).into(artistProfPic);
        }

        //Listners
        collapseDecriptionBtn.setOnClickListener(this);
        likeBtn.setOnClickListener(this);
        repostBtn.setOnClickListener(this);
        addToPlaylistBtn.setOnClickListener(this);

        initFullscreenButton();

        checkLikes();
        checkRepost();
        checkLikesCount();
        checkRepostCount();


        videoUri = Uri.parse(vid.getUrl());
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
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
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

            fullscreenExapndBtn.setVisibility(View.GONE);
            fullscreenShrinkBtn.setVisibility(View.VISIBLE);

            hideSystemUi();

            hideFullLayout.setVisibility(View.GONE);
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

        hideSystemUi();

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

    private void finishedRepost() {
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
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
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

    @Override
    public void onClick(View view) {
        if (view == collapseDecriptionBtn) {
            if (!collapseVariable) {
                //TextViewVideoDescription.setVisibility(View.GONE);
                DescLayout.setVisibility(View.GONE);
                collapseVariable = true;
            } else if (collapseVariable) {
                //TextViewVideoDescription.setVisibility(View.VISIBLE);
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

        if (view == addToPlaylistBtn) {
            Log.e(TAG, "add to Playlist clicked" + vid.getVideoId());
            Intent playListAct = new Intent(getApplicationContext(), AddToPlaylsit.class);
            playListAct.putExtra("VideoId", vid.getVideoId());
            startActivity(playListAct);
        }

        /*if(view == fullscreenExapndBtn){
            Log.e(TAG, "Fullscreen button clicked");
            fullscreenExapndBtn.setVisibility(View.GONE);
            openFullscreenDialog();
        }

        if(view == fullscreenShrinkBtn){
            Log.e(TAG, "Fullscreen button clicked");
            fullscreenExapndBtn.setVisibility(View.VISIBLE);
            openFullscreenDialog();
        }*/
    }
}