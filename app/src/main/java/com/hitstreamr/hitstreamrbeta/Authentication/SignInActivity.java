package com.hitstreamr.hitstreamrbeta.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.LabelDashboard;
import com.hitstreamr.hitstreamrbeta.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button backbutton, signinbtn;
    private EditText ETemail, ETpassword;
    private TextView register;
    private ProgressDialog progressDialog;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private DatabaseReference mDatabase;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    private static final String TAG = "FACELOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressDialog = new ProgressDialog(this);

        //Buttons
        backbutton = (Button)findViewById(R.id.backBtn);
        signinbtn = (Button)findViewById(R.id.signin_button);

        //Views
        ETemail = (EditText) findViewById(R.id.Email);
        ETpassword = (EditText)findViewById(R.id.Password);
        register = (TextView) findViewById(R.id.textviewsignin);

        backbutton.setOnClickListener(this);
        signinbtn.setOnClickListener(this);
        register.setOnClickListener(this);

        //user not logged in
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        if(mAuth.getCurrentUser() != null) {
            //home activity here
            Log.e(TAG, mAuth.getCurrentUser().getUid());
            //TODO Handle different users here
            sortUsers();
            //we will start the home activity here
            Toast.makeText(SignInActivity.this, "Welcome Back, " + mAuth.getCurrentUser().getUid() ,Toast.LENGTH_SHORT).show();
        }
            // Initialize Facebook Login button
            mCallbackManager = CallbackManager.Factory.create();
            LoginButton loginButton = findViewById(R.id.fblogin_button);
            loginButton.setReadPermissions("email", "public_profile");
            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                    // ...
                }
            });






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                        // ...
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser !=null){
            updateUI();
        }

    }

    private void updateUI() {

        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }



    private void UserLogin(){
        String email = ETemail.getText().toString().trim();
        String password = ETpassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email address",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            ETemail.setError("Please enter a valid email");
            ETemail.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter password",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            return;
        }
        //If validations is ok we will first show progressbar
        progressDialog.setMessage("Welcome");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            sortUsers();
                            //we will start the home activity here
                            Toast.makeText(SignInActivity.this, "Login Successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignInActivity.this, "Incorrect email or password. Please try again",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void sortUsers(){
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
    @Override
    public void onClick(View view) {
        if (view == signinbtn){
            UserLogin();
        }

        if (view == backbutton){
            //will open previous activity
        }

        if (view == register){
            finish();
            startActivity(new Intent(getApplicationContext(), AccountType.class));
            //will open sign in activity
        }

        if (view == backbutton){
            //will open sign in activity
            finish();
            startActivity(new Intent(getApplicationContext(), Welcome.class));
        }

    }

}
