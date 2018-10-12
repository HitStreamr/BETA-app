package com.hitstreamr.hitstreamrbeta;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity implements View.OnClickListener {
    //Layout
    private LinearLayout DescLayout;

    //VideoView
    private VideoView mainVideoView;

    //ImageView
    private ImageView ImageViewPlay;

    //progrss bar
    private ProgressBar videoProgress;
    private ProgressBar bufferProgress;

    //ImageButton
    private ImageButton collapseDecriptionBtn;

    //TextView
    private TextView TextViewVideoDescription;
    private TextView TextViewCurrentTime;
    private TextView TextViewDurationTime;

    //Video URI
    private Uri videoUri;

    private int current = 0;
    private int duration = 0;

    private Boolean isPlaying;
    private boolean collapseVariable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        isPlaying = false;

        //VideoView
        mainVideoView = findViewById(R.id.artistVideoPlayer);

        //Linear Layout
        DescLayout = findViewById(R.id.DescriptionLayout);

        //ImageView
        ImageViewPlay = findViewById(R.id.videoPlay);

        //ProgressBar
        videoProgress = findViewById(R.id.videoProgressBar);
        videoProgress.setMax(100);
        bufferProgress = findViewById(R.id.bufferProgress);

        //ImageButton
        collapseDecriptionBtn = findViewById(R.id.collapseDescription);

        //TextView
        TextViewVideoDescription = findViewById(R.id.videoDescription);
        TextViewCurrentTime = findViewById(R.id.videoCurrentTime);
        TextViewDurationTime = findViewById(R.id.videoDurationTime);

        //Listners
        collapseDecriptionBtn.setOnClickListener(this);
        ImageViewPlay.setOnClickListener(this);

        //videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/videos%2FHJsb8mUO2lgueTaCrs7JgIbxmJ82%2Framanuja?alt=media&token=59489ad2-977e-496a-864b-61816539220a");

        videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/videos%2FUpWI6YuzS6Qoh0I1EBrO3a6kmXQ2%2Fmp4%2Fextended%20house%20tour?alt=media&token=cc40e2e4-40db-48ab-a3e3-31ef4a8549d6");
        mainVideoView.setVideoURI(videoUri);
        mainVideoView.requestFocus();
        mainVideoView.start();
        isPlaying =true;
        //ImageViewPlay.setImageResource(R.mipmap.pause_action);

        new VideoPro().execute();

        mainVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                if (i == mediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    bufferProgress.setVisibility(View.VISIBLE);
                } else if (i == mediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    bufferProgress.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mainVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                duration = mediaPlayer.getDuration()/1000;
                String durationString = String.format("%02d:%02d", duration/60, duration%60);
                TextViewDurationTime.setText(durationString);
            }
        });

    }

    private void videoPauseResume(){
        if(isPlaying){
            mainVideoView.pause();
            isPlaying = false;
        }
        else{
            mainVideoView.start();
            isPlaying = true;
        }
    }




    @Override
    public void onClick(View view) {
        if (view == collapseDecriptionBtn) {
            if (!collapseVariable) {
                TextViewVideoDescription.setVisibility(View.GONE);
                collapseVariable = true;
            } else if (collapseVariable) {
                TextViewVideoDescription.setVisibility(View.VISIBLE);
                collapseVariable = false;
            }
        }
        if (view == ImageViewPlay) {
            videoPauseResume();
        }
    }

    public class VideoPro extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            do{
                current = mainVideoView.getCurrentPosition()/1000;
                try{
                    int currentPercent = current * 100/duration;
                    publishProgress(current);
                }catch (Exception e){

                }

            }while(videoProgress.getProgress() <= 100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            try {
                int currentPercent = values[0] * 100/duration;
                videoProgress.setProgress(currentPercent);

                String currentString = String.format("%02d:%02d", values[0]/60, values[0]%60);
                TextViewCurrentTime.setText(currentString);
            }catch (Exception e){

            }
        }
    }
}
