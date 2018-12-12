package com.hitstreamr.hitstreamrbeta.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.hitstreamr.hitstreamrbeta.LabelDashboard;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signinbtn;
    private ImageButton backbutton;
    private EditText ETemail, ETpassword;
    private TextView register,forgetPassword;
    private ProgressDialog progressDialog;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private DatabaseReference mDatabase;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    private static final String TAG = "FACELOG";

    private int loginAttempts;
    final int MAX_LOGIN = 5;

    static final int RESET_PASSWORD = 1;

    SharedPreferences sharedPref;
    volatile long timeLeft;
    CountDownTimer timer;
    final int TIMEOUT_PASSWORD = 900000;
    volatile Boolean locked_out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressDialog = new ProgressDialog(this);

        //Buttons
        backbutton = findViewById(R.id.backBtn);
        signinbtn = (Button)findViewById(R.id.signin_button);


        //Views
        ETemail = (EditText) findViewById(R.id.Email);
        ETpassword = (EditText)findViewById(R.id.Password);
        register = (TextView) findViewById(R.id.textviewsignin);
        forgetPassword = findViewById(R.id.forgot_password);

        backbutton.setOnClickListener(this);
        signinbtn.setOnClickListener(this);
        register.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

        //lock out code
        loginAttempts = 0;
        //get shared preferences
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        checkTimer();

        //user not logged in, because splash would have redirected
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

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

    /* Timer Code */
    private void checkTimer() {
        if (sharedPref.contains("time"))
            setTimer();
        else {
            locked_out = false;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("time", -1L);
            editor.apply();
        }
    }

    private void setTimer() {
        timeLeft = sharedPref.getLong("time", -1L);
        //Log.e(TAG,"Time: " + timeLeft);
        if (timeLeft != -1L)
            startTimer(timeLeft);
        else
            locked_out = false;
            loginAttempts = 0;

    }

    private void startTimer(long time) {
        locked_out = true;
        timer = new CountDownTimer(time, 1000) {

            @Override
            public void onFinish() {
                locked_out = false;
                loginAttempts = 0;
                saveToPref(-1L);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                saveToPref(timeLeft);
            }
        }.start();
    }

    private void saveToPref(long timeLeft){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("time", timeLeft);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESET_PASSWORD) {
            if (resultCode == RESULT_OK) {
                loginAttempts = 0;
                Toast.makeText(SignInActivity.this, "Password Rest Email Sent. Please check your email to reset password.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SignInActivity.this, "Email failed to send. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

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

                                loginAttempts++;
                                int temp = MAX_LOGIN - loginAttempts;
                                if (temp <= 0){
                                    //Log.e(TAG,"Start Timer: " + TIMEOUT_PASSWORD/1000);
                                    Toast.makeText(SignInActivity.this, "Too many failed login attempts. Please wait " + TIMEOUT_PASSWORD/1000 + " minute(s) to retry.",Toast.LENGTH_SHORT).show();
                                    startTimer(TIMEOUT_PASSWORD);
                                }else{
                                    Toast.makeText(SignInActivity.this, "Incorrect email or password. Please try again",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(SignInActivity.this, "You have " + temp + " attempt(s) left.",Toast.LENGTH_SHORT).show();
                                }

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
            if (!locked_out){
                UserLogin();
            }else{
                Toast.makeText(getApplicationContext(),"Please wait to attempt to login again.",Toast.LENGTH_LONG).show();
            }
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

        if (view == forgetPassword){
            startActivityForResult(new Intent(getApplicationContext(), ResetPassword.class),RESET_PASSWORD);
        }
    }

}
