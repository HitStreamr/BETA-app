package com.hitstreamr.hitstreamrbeta.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitstreamr.hitstreamrbeta.LabelDashboard;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

public class Welcome extends AppCompatActivity implements View.OnClickListener {
    private Button sign_in, create_account;

    final String TAG = "WELCOME";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
// [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sign_in = (Button) findViewById(R.id.signin_button);
        create_account = (Button) findViewById(R.id.create_account_button);

       sign_in.setOnClickListener(this);
       create_account.setOnClickListener(this);

        //user not logged in
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
       // [END initialize_auth]
        if(mAuth.getCurrentUser() !=null){
            //home activity here
            sortUsers();
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

    private void sortUsers() {
        //DatabaseReference basicRoot= mDatabase.child(getString(R.string.child_basic));
        //DatabaseReference artistRoot= mDatabase.child(getString(R.string.child_artist));
        //DatabaseReference labelRoot= mDatabase.child(getString(R.string.child_label));

        mDatabase.child(getString(R.string.child_basic) + "/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    //user exists in basic user table, do something
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    homeIntent.putExtra("TYPE", getString(R.string.type_basic));
                    startActivity(homeIntent);
                    // Sign in success, update UI with the signed-in user's information
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });

        mDatabase.child(getString(R.string.child_artist) + "/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    //user exists in basic user table, do something
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    homeIntent.putExtra("TYPE", getString(R.string.type_artist));
                    startActivity(homeIntent);
                    // Sign in success, update UI with the signed-in user's information
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });

        mDatabase.child(getString(R.string.child_label) + "/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    //user exists in basic user table, do something
                    Intent labelIntent = new Intent(getApplicationContext(), LabelDashboard.class);
                    labelIntent.putExtra("TYPE", getString(R.string.type_label));
                    startActivity(labelIntent);
                    // Sign in success, update UI with the signed-in user's information
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }
}
