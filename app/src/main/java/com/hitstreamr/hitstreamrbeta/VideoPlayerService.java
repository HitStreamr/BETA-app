package com.hitstreamr.hitstreamrbeta;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayerService extends Service {
    static SimpleExoPlayer player;
    Video vid;
    private PlayerNotificationManager playerNotificationManager;
    private int currentWindow;
    private long playbackPosition;
    private Intent lastIntent;
    private int lastID;
    private boolean runCheck;
    private boolean reset;
    private String credits;
    private String currentCreditVal;

    private String sTimeStamp = "";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "Video Player Service";
    private static final String CHANNEL_ID = "HS_VIDEO_PLAYER";

    private static final int NOTIFICATION_ID = 1023456789;
    private ComponentListener componentListener;

    private final IBinder mBinder = new LocalBinder();
    private PlayerServiceCallback serviceCallback;
    private boolean clipped;
    private FirebaseUser currentFirebaseUser;
    private boolean uploadbyUser;
    private boolean autoplay_switchState;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        VideoPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return VideoPlayerService.this;
        }
    }

    //probably need to merge with Dioni
    //needs to be a started bound service
    public VideoPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runCheck = false;
        currentWindow = 0;
        playbackPosition = 0l;
        componentListener = new ComponentListener();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //componentListener = new VideoPlayer.ComponentListener();
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
            //playerView.setPlayer(player);
            player.setPlayWhenReady(true);
            player.seekTo(currentWindow, playbackPosition);
        }

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                getApplicationContext(),
                CHANNEL_ID,
                R.string.hs_player_channel_id,
                NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        if (vid != null){
                            return vid.getTitle();
                        }else return "Loading Music ...";
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        if (vid != null){
                            return vid.getTitle();
                        }else return "Loading Music ...";
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player,
                                                      PlayerNotificationManager.BitmapCallback callback) {

                        //ImageLoader asyncTask = (ImageLoader) new ImageLoader(new ImageLoader.AsyncResponse(){
                        //   @Override
                        //    public void processFinish(Bitmap output) {
                        // callback.onBitmap(output);
                        //  }
                        // }).execute();

                        return getPlaceholderBitmap();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        return null;
                    }

                    public Bitmap getPlaceholderBitmap(){
                        return BitmapFactory.decodeResource(getResources(),R.drawable.hitstreamr_icon);
                    }
                }
        );
        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                if (serviceCallback != null){
                    serviceCallback.stopPlayer();
                }
                stopSelf();
            }
        });
        playerNotificationManager.setPlayer(player);


    }

    @Override
    public void onDestroy() {
        playerNotificationManager.setPlayer(null);
        player.release();
        player = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lastID = startId;
        credits = intent.getStringExtra("CREDITS");
        uploadbyUser = intent.getBooleanExtra("UPLOAD", false);
        //playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        return START_STICKY;
    }

    public void setCallbacks(PlayerServiceCallback callback) {
        serviceCallback = callback;
    }

    public void setPlayer(Video newVid, boolean clipped, String credits) {
        vid = newVid;
        this.clipped = clipped;
        this.credits = credits;
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        ExtractorMediaSource mediaSource1 = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(vid.getUrl()));
        ClippingMediaSource clippingSource = new ClippingMediaSource(mediaSource1, 0, 15_000_000);
        if (clipped)
            player.prepare(clippingSource, true, false);
        else
            player.prepare(mediaSource1,true, false);

        if(serviceCallback != null){
            serviceCallback.setPlayerView();
        }
    }

    public void resetPlayer(){
        Log.d(TAG, "Value in Service: " + clipped );
        if(serviceCallback != null){
            serviceCallback.setPlayerView();
        }
    }

    public void stopVideoService(){
        stopSelf();
    }

    // This method is to check the player position every second and after 15 seconds initiate the DB call
    private Timer timer;
    public void timerCounter(){
        Handler handler = new Handler(Looper.getMainLooper());
        if (!runCheck) {
            timer = new Timer();
            Log.e(TAG, "Video player inside if statetime counter ");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!(VideoPlayerService.player == null) ) {
                                long current = VideoPlayerService.player.getCurrentPosition();
                                if (current >= 15000) {
                                    Log.e(TAG, "Video player inside if state" + current);
                                    timer.cancel();
                                    runCheck = true;
                                    creditCheck();
                                    Log.e(TAG, "Video player inside if cancel timer & runcheck " + runCheck);
                                }
                            }
                        }
                    });
                }
            };
            timer.schedule(task, 0, 1000);
        }
    }

    public void creditCheck(){
        if(Integer.parseInt(credits) > 0 ){
            try {
                checkViewTime(vid, currentFirebaseUser);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            if(serviceCallback != null){
                serviceCallback.callPurchase();
            }
        }
    }

    //This method is called after 15 secs for users with credits watch to check if they watched the video before
    public void checkViewTime(Video vid, FirebaseUser currentFirebaseUser) throws ParseException {
        this.vid = vid;
        FirebaseDatabase.getInstance().getReference("VideoViews")
                .child(vid.getVideoId()).child(currentFirebaseUser.getUid()).child("TimeLimit")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sTimeStamp = dataSnapshot.getValue(String.class);
                        Log.e(TAG, "Your video date from db check view time " + sTimeStamp);
                        if (!Strings.isNullOrEmpty(sTimeStamp)) {
                            try {
                                checkTimeStamp(currentFirebaseUser);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                updatevideoview(currentFirebaseUser);
                                updateCreditValue(currentFirebaseUser);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // This method is to compare the current time with 4 hrs specified time limit for particular user
    private void checkTimeStamp(FirebaseUser user) throws ParseException {
        //Log.e(TAG, "Your video date checktimestamp" + sTimeStamp);
        if (!Strings.isNullOrEmpty(sTimeStamp)) {
            Calendar now = Calendar.getInstance();
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(sTimeStamp);
            Calendar c = Calendar.getInstance();
            c.setTime(parsedDate);
            // Log.e(TAG, "Your video date format from db" + c.getTime());
            //Log.e(TAG, "Your video date format from db now" + now.getTime());
            if (now.getTime().after(c.getTime())) {
                //  Log.e(TAG, "Your video date format after checking inside if");
                updatevideoview(user);
                updateCreditValue(user);
            }
        }
    }

    // to update the user id and time frame (current time + 4hrs) values into VideoView DB
    private void updatevideoview(FirebaseUser currentFirebaseUser) throws ParseException {

       /* long currentTimeMillis = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(currentTimeMillis);
        String currentTime = dateFormat.format(date);
        Log.e(TAG, "Your video date format :" + currentTime);*/

        Calendar now = Calendar.getInstance();
        Calendar tmp = (Calendar) now.clone();
        tmp.add(Calendar.HOUR_OF_DAY, 4);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String strDate = simpleFormat.format(tmp.getTime());
        Log.e(TAG, "Your video date format after" + strDate);
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

    public boolean checkAutoplay(){
        SharedPreferences preferences = getSharedPreferences("UserSwitchPrefs", 0);
        if (preferences.contains("autoplay_switch")){
            return autoplay_switchState = preferences.getBoolean("autoplay_switch", false);
        }

        return false;
    }

    //To reduce 1 user credit for watching a video
    private void updateCreditValue(FirebaseUser currentFirebaseUser) {
        if (!uploadbyUser) {
            if (!Strings.isNullOrEmpty(credits)) {
                int creditval = Integer.parseInt(credits);
                final int newval = creditval - 1;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Credits").child(currentFirebaseUser.getUid());
                ref.child("creditvalue").setValue(String.valueOf(newval))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(serviceCallback != null){
                                    serviceCallback.updateCreditText(String.valueOf(newval));
                                }
                            }
                        });
            }
        }
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
                    // Play the next video ONLY IF we have finished playing the whole video
                    // and the auto-play switch is turned on
                    if (!clipped & checkAutoplay()) {
                        Log.e(TAG, "AUTOPLAY entering IF");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (serviceCallback != null){
                                Log.e(TAG, "AUTOPLAY CALLED");
                                serviceCallback.autoPlayNext();
                            }
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
}
