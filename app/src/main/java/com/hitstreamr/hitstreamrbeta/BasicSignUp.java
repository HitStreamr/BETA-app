package com.hitstreamr.hitstreamrbeta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class BasicSignUp extends AppCompatActivity implements View.OnClickListener{

    String st;

    private static final String BASIC_TAG = "EmailPassword";

    private Button signup, backbtn;
    private EditText mEmailField, mPasswordField, mUsername;
    private TextView signintext;
    private RadioButton radiobtn;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_sign_up);

        progressDialog = new ProgressDialog(this);

        // Views
        mUsername = findViewById(R.id.Username);
        mEmailField = findViewById(R.id.Email);
        mPasswordField = findViewById(R.id.Password);
        signintext = findViewById(R.id.textviewsignin);


        // Buttons
        signup = findViewById(R.id.signup_button);
        backbtn = findViewById(R.id.backBtn);
        radiobtn = findViewById(R.id.tos_button);

        //Listeners
        mUsername.setOnClickListener(this);
        mEmailField.setOnClickListener(this);
        mPasswordField.setOnClickListener(this);
        signintext.setOnClickListener(this);
        signup.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        radiobtn.setOnClickListener(this);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        if(mAuth.getCurrentUser() !=null){
            //home activity here
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    private void registerUser(){
        final String username = mUsername.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();


        if(TextUtils.isEmpty(username)){
            //email is empty
            Toast.makeText(this, "Please enter a Username",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            return;
        }
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter an Email address",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailField.setError("Please enter a valid email");
            mEmailField.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter Password",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            return;
        }
        if(password.length()<6) {
            mPasswordField.setError("Minimum length of password characters is 6");
            mPasswordField.requestFocus();
            return;
        }
        //If validations is ok we will first show progressbar
        progressDialog.setMessage("Registering New User...");
        progressDialog.show();


        //Address if statements and toast messages.
        //Address if statements and toast messages.
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // We will store the additional fields in the Firebase Database

                            User user = new User(
                                   username,
                                   email
                           );

                            FirebaseDatabase.getInstance().getReference("BasicUsers")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener (new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(BasicSignUp.this, "Registered Successfully",Toast.LENGTH_SHORT).show();
                                        //we will start the home activity here
                                         finish();
                                         startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    }else {
                                        //Display a failure message
                                    }
                                }
                            });


                        }else{
                            Toast.makeText(BasicSignUp.this, "Could not register. Please try again",Toast.LENGTH_SHORT).show();
                        }
                        }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == signup){
            if (radiobtn.isChecked()){
                registerUser();
            }else{
                Toast.makeText(BasicSignUp.this, "Please agree to the Terms and Conditions.",Toast.LENGTH_SHORT).show();
            }
        }
        if (view == signintext){
            //will open sign in activity
            finish();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
        if (view == backbtn){
            //will open sign in activity
            finish();
            startActivity(new Intent(getApplicationContext(), AccountType.class));
        }

    }
}
