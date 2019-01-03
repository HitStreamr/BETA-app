package com.hitstreamr.hitstreamrbeta.Authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hitstreamr.hitstreamrbeta.R;

public class Welcome extends AppCompatActivity implements View.OnClickListener {

    private Button sign_in, create_account;
    private TextView terms, privacy;

    final String TAG = "WELCOME";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sign_in = (Button) findViewById(R.id.signin_button);
        create_account = (Button) findViewById(R.id.create_account_button);
        terms = findViewById(R.id.terms);
        privacy = findViewById(R.id.privacyPolicy);

       sign_in.setOnClickListener(this);
       create_account.setOnClickListener(this);
       terms.setOnClickListener(this);
       privacy.setOnClickListener(this);

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

            case R.id.terms:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hitstreamr.com/terms-of-service"));
                startActivity(browserIntent);
                break;

            case R.id.privacyPolicy:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hitstreamr.com/terms-of-service"));
                startActivity(browserIntent2);
                break;
        }

    }
}
