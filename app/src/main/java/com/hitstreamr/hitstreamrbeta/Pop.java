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


public class Pop extends Activity implements View.OnClickListener {

    private Button cancel, ok;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_popup);

        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.confirm);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

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
            case R.id.cancel:
                finish();
                break;

            case R.id.confirm:
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                //startActivity(new Intent(getApplicationContext(), SignInActivity.class));

                Intent i = new Intent(this, SignInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
        }
    }
}
