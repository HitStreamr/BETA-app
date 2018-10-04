package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoView extends AppCompatActivity implements View.OnClickListener {
    //Layout
    private LinearLayout DescLayout;

    //ImageButton
    private ImageButton collapseDecriptionBtn;

    //TextView
    private TextView TextViewVideoDescription;

    private boolean collapseVariable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        //Linear Layout
        DescLayout = findViewById(R.id.DescriptionLayout);

        //ImageButton
        collapseDecriptionBtn = findViewById(R.id.collapseDescription);

        //TextView
        TextViewVideoDescription = findViewById(R.id.videoDescription);

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
