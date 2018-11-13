package com.hitstreamr.hitstreamrbeta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ReportVideoPopup extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PlayerActivity";

    private Button cancelBtn, reportBtn;

    FirebaseUser currentFirebaseUser;

    private String videoId;

    private int radioButtonID;
    private View radioButton;
    private int idx;
    private RadioGroup radioGroup;
    private String selectedtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_video_popup);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Buttons
        reportBtn = findViewById(R.id.reportBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        //Listners
        reportBtn.setOnClickListener(this);



        videoId = getIntent().getStringExtra("VideoId");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));
    }

    private void registerReportVideo() {

        //selectedtext = radioButton.toString();

        radioGroup = findViewById(R.id.rRadioBtnGroup);
        radioButtonID = radioGroup.getCheckedRadioButtonId();
        radioButton = radioGroup.findViewById(radioButtonID);
        idx = radioGroup.indexOfChild(radioButton);

        switch(idx){
            case 0:
                selectedtext = "Sexual Content";
                break;
            case 1:
                selectedtext = "Violent or repulsive content";
                break;
            case 2:
                selectedtext = "Hateful or abusive content";
                break;
            case 3:
                selectedtext = "Child abuse";
                break;
            case 4:
                selectedtext = "Infringes my rights";
                break;
            case 5:
                selectedtext = "Promotes terrorism";
                break;
            case 6:
                selectedtext = "Spam or misleading";
                break;
        }

        Log.e(TAG, "report video is clicked" + idx +" and video id is :::::" +videoId + "and text is :::" + selectedtext);

        Toast.makeText(this, "video is reported", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance()
                .getReference("Report")
                .child(videoId)
                .child(currentFirebaseUser.getUid())
                .child("reason")
                .setValue(selectedtext)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Video is reported");
                        finish();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view == reportBtn) {
            //Log.e(TAG, "report video is clicked" + idx +selectedtext);
            registerReportVideo();
        }
    }
}
