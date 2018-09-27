package com.hitstreamr.hitstreamrbeta.Authentication;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hitstreamr.hitstreamrbeta.R;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcomeIntent = new Intent(Splash.this, Welcome.class);
                startActivity(welcomeIntent);
                    finish();
                }
        },SPLASH_TIME_OUT);
    }
}
