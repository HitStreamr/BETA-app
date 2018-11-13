package com.hitstreamr.hitstreamrbeta.DrawerMenuFragments;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

import static android.content.Context.MODE_PRIVATE;

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

    //Notification Preference File
    SharedPreferences userNotifSettings;

    //Preference Constants
    private final String FOLLOW = "newFollow";
    private final String ALL_PUSH = "pushNotifs";
    private final String REPOST = "repostVid";
    private final String NEW_POST = "newPost";
    private final String FAVE = "newFave";
    private final String COMMENT = "newComment";
    private final String FEATURES = "newFeatures";
    private final String TIPS = "hitstreamrTips";
    private final String SURVEY = "surveys";

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
        surveysFeedback= view.findViewById(R.id.surveyFeedback);
        hitstreamrTips= view.findViewById(R.id.hitstreamrTips);

        allPush.setOnCheckedChangeListener(this);
        newFollow.setOnCheckedChangeListener(this);
        newPostByFollowed.setOnCheckedChangeListener(this);
        favesVid.setOnCheckedChangeListener(this);
        commentVid.setOnCheckedChangeListener(this);
        newHitstreamr.setOnCheckedChangeListener(this);
        surveysFeedback.setOnCheckedChangeListener(this);
        hitstreamrTips.setOnCheckedChangeListener(this);

        //Set up the Notification Storage for the user in question
        userNotifSettings = getActivity().getSharedPreferences(getArguments().getString("ID"), MODE_PRIVATE);

        //Load the SettingsFragment
        loadSettings(userNotifSettings);
    }

    private void loadSettings(SharedPreferences sp){
        //check for each Settings type
        allPush.setChecked(sp.getBoolean(ALL_PUSH,false));
        newFollow.setChecked(sp.getBoolean(FOLLOW, false));
        repostVid.setChecked(sp.getBoolean(REPOST,false));
        newPostByFollowed.setChecked(sp.getBoolean(NEW_POST,false));
        favesVid.setChecked(sp.getBoolean(FAVE, false));
        commentVid.setChecked(sp.getBoolean(COMMENT, false));
        newHitstreamr.setChecked(sp.getBoolean(FEATURES, false));
        surveysFeedback.setChecked(sp.getBoolean(SURVEY, false));
        hitstreamrTips.setChecked(sp.getBoolean(TIPS, false));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //TODO Setup Notification Settings
        SharedPreferences.Editor edit = userNotifSettings.edit();
        switch(buttonView.getId()){
            case R.id.pushNotifs:
                if(!isChecked){
                    edit.putBoolean(ALL_PUSH, false);
                    edit.apply();
                    newFollow.setChecked(false);
                    repostVid.setChecked(false);
                    newPostByFollowed.setChecked(false);
                    favesVid.setChecked(false);
                    commentVid.setChecked(false);
                    newHitstreamr.setChecked(false);
                    surveysFeedback.setChecked(false);
                    hitstreamrTips.setChecked(false);
                }else{
                    edit.putBoolean(ALL_PUSH, true);
                    edit.apply();
                }
                break;
            case R.id.newFollow:
                if(isChecked){
                    allPush.setChecked(true);
                    edit.putBoolean(FOLLOW, true);
                    edit.apply();
                    //Toast.makeText(getActivity(),"New Follow checked", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getActivity(),"New Follow unchecked", Toast.LENGTH_SHORT).show();
                    edit.putBoolean(FOLLOW, false);
                    edit.apply();
                }
                break;
            case R.id.repostVid:
                if(isChecked){
                    allPush.setChecked(true);
                    edit.putBoolean(REPOST, true);
                    edit.apply();
                    //Toast.makeText(getActivity(),"Repost checked", Toast.LENGTH_SHORT).show();
                }else{
                    edit.putBoolean(REPOST, false);
                    edit.apply();
                    //Toast.makeText(getActivity(),"Repost unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.newPost:
                if(isChecked){
                    allPush.setChecked(true);
                    edit.putBoolean(NEW_POST, true);
                    edit.apply();
                    //Toast.makeText(getActivity(),"New Post checked", Toast.LENGTH_SHORT).show();
                }else{
                    edit.putBoolean(NEW_POST, false);
                    edit.apply();
                    //oast.makeText(getActivity(),"New Post unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.favesVid:
                if(isChecked){
                    allPush.setChecked(true);
                    edit.putBoolean(FAVE, true);
                    edit.apply();
                    //Toast.makeText(getActivity(),"Faves checked", Toast.LENGTH_SHORT).show();
                }else{
                    edit.putBoolean(FAVE, false);
                    edit.apply();
                    //Toast.makeText(getActivity(),"Faves unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.commentVid:
                if(isChecked){
                    edit.putBoolean(COMMENT, true);
                    edit.apply();
                    allPush.setChecked(true);
                    //Toast.makeText(getActivity(),"New Comment checked", Toast.LENGTH_SHORT).show();
                }else{
                    edit.putBoolean(COMMENT, false);
                    edit.apply();
                    //Toast.makeText(getActivity(),"New Comment unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.newFeatures:
                if(isChecked){
                    allPush.setChecked(true);
                    edit.putBoolean(FEATURES, true);
                    edit.apply();
                    //Toast.makeText(getActivity(),"New Features checked", Toast.LENGTH_SHORT).show();
                }else{
                    edit.putBoolean(FEATURES, false);
                    edit.apply();
                    //Toast.makeText(getActivity(),"New Features unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.hitstreamrTips:
                if(isChecked){
                    allPush.setChecked(true);
                    edit.putBoolean(TIPS, true);
                    edit.apply();
                    //Toast.makeText(getActivity(),"Tips checked", Toast.LENGTH_SHORT).show();
                }else{
                    edit.putBoolean(TIPS, false);
                    edit.apply();
                   // Toast.makeText(getActivity(),"Tips unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.surveyFeedback:
                if(isChecked){
                    allPush.setChecked(true);
                    edit.putBoolean(SURVEY, true);
                    edit.apply();
                    //Toast.makeText(getActivity(),"Tips checked", Toast.LENGTH_SHORT).show();
                }else{
                    edit.putBoolean(SURVEY, false);
                    edit.apply();
                    // Toast.makeText(getActivity(),"Tips unchecked", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
