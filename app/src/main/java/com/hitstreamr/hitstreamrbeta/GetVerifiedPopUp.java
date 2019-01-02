package com.hitstreamr.hitstreamrbeta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hitstreamr.hitstreamrbeta.Authentication.SignInActivity;


public class GetVerifiedPopUp extends Activity implements View.OnClickListener {

    private Button close, ok;
    // [START declare_auth]
    private FirebaseAuth mAuth;
// [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_verified_popup);

        close = (Button) findViewById(R.id.closePop);
        ok = (Button) findViewById(R.id.okay);

        ok.setOnClickListener(this);
        close.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


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

            case R.id.okay:
                finish();
                break;
        }
    }
}
