package com.hitstreamr.hitstreamrbeta.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hitstreamr.hitstreamrbeta.R;

public class Welcome extends AppCompatActivity implements View.OnClickListener {
    private Button sign_in, create_account;

    final String TAG = "WELCOME";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sign_in = (Button) findViewById(R.id.signin_button);
        create_account = (Button) findViewById(R.id.create_account_button);

       sign_in.setOnClickListener(this);
       create_account.setOnClickListener(this);

        //user not logged in, because Splash redirects

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_button:
                Intent openAuthenticator = new Intent(this, SignInActivity.class);
                startActivity(openAuthenticator);
                break;

            case R.id.create_account_button:
                Intent openCreateAcct = new Intent(this, AccountType.class);
                startActivity(openCreateAcct);
                break;
        }

    }
}
