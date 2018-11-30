package com.hitstreamr.hitstreamrbeta.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hitstreamr.hitstreamrbeta.R;

public class AccountType extends AppCompatActivity implements View.OnClickListener {

    private Button basicAcctBtn, artistAcctBtn;
    private ImageButton backbtn;
    private TextView labelAcct;
    private ImageButton backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);

        basicAcctBtn = (Button) findViewById(R.id.basicAcct);
        artistAcctBtn = (Button) findViewById(R.id.artistAcct);
        backbtn = findViewById(R.id.backBtn);
        labelAcct = (TextView) findViewById(R.id.labelAcct);

        basicAcctBtn.setOnClickListener(this);
        artistAcctBtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        labelAcct.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.basicAcct:
                Intent openAuthenticator = new Intent(this, BasicSignUp.class);
                startActivity(openAuthenticator);
                break;

            case R.id.artistAcct:
                Intent openCreateAcct = new Intent(this, ArtistSignUp.class);
                startActivity(openCreateAcct);
                break;

            case R.id.backBtn:
                Intent back = new Intent(this, Welcome.class);
                startActivity(back);
                break;

            case R.id.labelAcct:
                Intent labelsignup = new Intent(this, LabelSignUp.class);
                startActivity(labelsignup);
                break;
        }
    }
}
