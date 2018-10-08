package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoView extends AppCompatActivity implements View.OnClickListener {
    //Layout
    private LinearLayout DescLayout;

    //VideoView
    private VideoView mainVideoView;

    //ImageView
    private ImageView ImageViewPlay;

    //ImageButton
    private ImageButton collapseDecriptionBtn;

    //TextView
    private TextView TextViewVideoDescription;
    private TextView TextViewCurrentTime;
    private TextView TextViewDurationTime;

    private boolean collapseVariable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        //VideoView
        //mainVideoView = findViewById(R.id.idVideo);

        //Linear Layout
        DescLayout = findViewById(R.id.DescriptionLayout);

        //ImageView
        ImageViewPlay = findViewById(R.id.videoPlay);

        //ImageButton
        collapseDecriptionBtn = findViewById(R.id.collapseDescription);

        //TextView
        TextViewVideoDescription = findViewById(R.id.videoDescription);
        TextViewCurrentTime = findViewById(R.id.videoCurrentTime);
        TextViewDurationTime = findViewById(R.id.videoDurationTime);

        //Listners
        collapseDecriptionBtn.setOnClickListener(this);

        //DescLayout.setVisibility(View.GONE);
        //TextViewVideoDescription.setVisibility(View.GONE);


    }


    @Override
    public void onClick(View view) {
        if (view == collapseDecriptionBtn) {
            if (!collapseVariable) {
                TextViewVideoDescription.setVisibility(View.GONE);
                collapseVariable = true;
            }
            else if(collapseVariable) {
                TextViewVideoDescription.setVisibility(View.VISIBLE);
                collapseVariable = false;
            }
        }
    }
}
