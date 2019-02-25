package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EmailVerification extends AppCompatActivity {

    private TextView verifText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        verifText = findViewById(R.id.verificationText);
        verifText.setText("A Verification Email has been sent to");
    }
}
