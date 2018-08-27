package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity implements View.OnClickListener {
    private Button sign_in, create_account;

    // [START declare_auth]
    private FirebaseAuth mAuth;
// [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sign_in = (Button) findViewById(R.id.signin_button);
        create_account = (Button) findViewById(R.id.create_account_button);

       sign_in.setOnClickListener(this);
       create_account.setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
       // [END initialize_auth]
        if(mAuth.getCurrentUser() !=null){
            //home activity here
            finish();
            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
            homeIntent.putExtra("TYPE", getString(R.string.type_basic));
            startActivity(homeIntent);
        }
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
