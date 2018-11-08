package com.hitstreamr.hitstreamrbeta.DrawerMenuFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

public class NotificationSettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    //phone notifications
    SwitchCompat allPush;
    SwitchCompat newFollow;
    SwitchCompat repostVid;
    SwitchCompat newPostByFollowed;
    SwitchCompat favesVid;
    SwitchCompat commentVid;
    SwitchCompat newHitstreamr;
    SwitchCompat surveysFeedback;
    SwitchCompat hitstreamrTips;

    //email Notifications
    SwitchCompat email_allPush;
    SwitchCompat email_newFollow;
    SwitchCompat email_repostVid;
    SwitchCompat email_newPostByFollowed;
    SwitchCompat email_favesVid;
    SwitchCompat email_commentVid;
    SwitchCompat email_newHitstreamr;
    SwitchCompat email_surveysFeedback;
    SwitchCompat email_hitstreamrTips;

    //TAG
    private final String TAG = "NOTIF_SETTINGS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notificationsettings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button close = (Button) view.findViewById(R.id.closeBtn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("TYPE", getArguments().getString("TYPE"));
                startActivity(intent);
            }
        });

        allPush = view.findViewById(R.id.pushNotifs);
        newFollow = view.findViewById(R.id.newFollow);
        repostVid= view.findViewById(R.id.repostVid);
        newPostByFollowed= view.findViewById(R.id.newPost);
        favesVid= view.findViewById(R.id.favesVid);
        commentVid= view.findViewById(R.id.commentVid);
        newHitstreamr= view.findViewById(R.id.newFeatures);
        surveysFeedback= view.findViewById(R.id.SurveyFeedback);
        hitstreamrTips= view.findViewById(R.id.hitstreamrTips);

        allPush.setOnCheckedChangeListener(this);
        newFollow.setOnCheckedChangeListener(this);
        newPostByFollowed.setOnCheckedChangeListener(this);
        favesVid.setOnCheckedChangeListener(this);
        commentVid.setOnCheckedChangeListener(this);
        newHitstreamr.setOnCheckedChangeListener(this);
        surveysFeedback.setOnCheckedChangeListener(this);
        hitstreamrTips.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //TODO Setup Notification Settings
        switch(buttonView.getId()){
            case R.id.pushNotifs:
                if(isChecked){
                    newFollow.setChecked(true);
                    repostVid.setChecked(true);
                    newPostByFollowed.setChecked(true);
                    favesVid.setChecked(true);
                    commentVid.setChecked(true);
                    newHitstreamr.setChecked(true);
                    surveysFeedback.setChecked(true);
                    hitstreamrTips.setChecked(true);
                }else{
                    newFollow.setChecked(false);
                    repostVid.setChecked(false);
                    newPostByFollowed.setChecked(false);
                    favesVid.setChecked(false);
                    commentVid.setChecked(false);
                    newHitstreamr.setChecked(false);
                    surveysFeedback.setChecked(false);
                    hitstreamrTips.setChecked(false);
                }
                break;
            case R.id.newFollow:
                if(isChecked){
                    Toast.makeText(getActivity(),"New Follow checked", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"New Follow checked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.repostVid:
                if(isChecked){
                    Toast.makeText(getActivity(),"Repost checked", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Repost checked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.newPost:
                if(isChecked){
                    Toast.makeText(getActivity(),"New Post checked", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"New Post checked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.favesVid:
                if(isChecked){
                    Toast.makeText(getActivity(),"Faves checked", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Faves checked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.commentVid:
                if(isChecked){
                    Toast.makeText(getActivity(),"New Comment checked", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"New Comment checked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.newFeatures:
                if(isChecked){
                    Toast.makeText(getActivity(),"New Features checked", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"New Features checked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.hitstreamrTips:
                break;
        }
    }
}
