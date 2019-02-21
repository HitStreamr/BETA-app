package com.hitstreamr.hitstreamrbeta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hitstreamr.hitstreamrbeta.Authentication.SignInActivity;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.InviteAFriendFragment;


public class ContributorHelp extends Activity implements View.OnClickListener {

    private Button Close, Invite;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contributor_help);

        Close = (Button) findViewById(R.id.closePop);
        Invite = (Button) findViewById(R.id.invite);

        Close.setOnClickListener(this);
        Invite.setOnClickListener(this);

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
//        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getWindow().setLayout((int) (width), (int) (height));
        getWindow().setBackgroundDrawable(new ColorDrawable(0x4b000000));

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        if(mAuth.getCurrentUser() ==null){
            finish();
            startActivity(new Intent(this, SignInActivity.class));

            FirebaseUser user = mAuth.getCurrentUser();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closePop:
                finish();
                break;

            case R.id.invite:
//                startActivity(new Intent(this, InviteAFriendFragment.class));
                finish();
                break;
        }
    }
}
