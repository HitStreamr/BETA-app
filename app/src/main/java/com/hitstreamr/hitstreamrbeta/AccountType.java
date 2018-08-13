package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountType extends AppCompatActivity implements View.OnClickListener {

    private Button basicAcctBtn, artistAcctBtn, backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);

        basicAcctBtn = (Button) findViewById(R.id.basicAcct);
        artistAcctBtn = (Button) findViewById(R.id.artistAcct);
        backbtn = (Button) findViewById(R.id.backBtn);

        basicAcctBtn.setOnClickListener(this);
        artistAcctBtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.basicAcct:
                Intent openAuthenticator = new Intent(this, BasicSignUp.class);
                startActivity(openAuthenticator);
                break;

            case R.id.artistAcct:
               // Intent openCreateAcct = new Intent(this, ArtistSignUp.class);
              //  startActivity(openCreateAcct);
                break;

            case R.id.backBtn:
                Intent back = new Intent(this, Welcome.class);
                startActivity(back);
                break;
        }
    }
}
