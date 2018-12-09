package com.hitstreamr.hitstreamrbeta.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hitstreamr.hitstreamrbeta.LabelDashboard;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    // [END declare_auth]

    final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                // [START initialize_auth]
                mAuth = FirebaseAuth.getInstance();
                // [END initialize_auth]
                Log.e(TAG, mAuth.getCurrentUser()+"");
                if(mAuth.getCurrentUser() != null){
                    //store the notification token/overirde if different

                    //find the refto user notif
                    DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference().child("FirebaseNotificationTokens").child(mAuth.getCurrentUser().getUid());

                    notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e(TAG, ""+dataSnapshot.getChildrenCount());
                            if(dataSnapshot.exists()){
                                for(DataSnapshot child: dataSnapshot.getChildren()){
                                    Log.e(TAG, "Child Key: " + child.getKey() + " Value: " + child.getValue(Boolean.TYPE));
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                                        return;
                                                    }else{
                                                        //phone token takes precendence, if they are different overwite
                                                        String currentToken = child.getKey();

                                                        if (!task.getResult().getToken().equals(currentToken)){
                                                            notifRef.child(currentToken).removeValue();
                                                            notifRef.child(task.getResult().getToken()).setValue(true);
                                                        }

                                                        sortUsers();
                                                    }
                                                }
                                            });
                                }
                            }
                            else{
                                //user has no notification tokens
                                //Instace id stuff
                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                                    return;
                                                }else{
                                                    notifRef.child(task.getResult().getToken()).setValue(true);
                                                    sortUsers();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Log.d(TAG, "null User");
                    startActivity(new Intent(getApplicationContext(),Welcome.class));
                }
            }
        },SPLASH_TIME_OUT);


    }

    private void sortUsers() {
        mDatabase.child(getString(R.string.child_basic) + "/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    // Sign in success, update UI with the signed-in user's information
                    finish();
                    //user exists in basic user table, do something
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    homeIntent.putExtra("TYPE", getString(R.string.type_basic));
                    startActivity(homeIntent);

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
                    // Sign in success, update UI with the signed-in user's information
                    finish();
                    //user exists in basic user table, do something
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    homeIntent.putExtra("TYPE", getString(R.string.type_artist));
                    startActivity(homeIntent);

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
                    // Sign in success, update UI with the signed-in user's information
                    finish();
                    //user exists in basic user table, do something
                    Intent labelIntent = new Intent(getApplicationContext(), LabelDashboard.class);
                    labelIntent.putExtra("TYPE", getString(R.string.type_label));
                    startActivity(labelIntent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }
}
