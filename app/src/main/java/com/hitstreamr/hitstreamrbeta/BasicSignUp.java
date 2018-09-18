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

//    private static final Pattern PASSWORD_PATTERN =
//            Pattern.compile("^" +
//                    "(?=.*[0-9])" +         //at least 1 digit
//                    "(?=.*[a-z])" +         //at least 1 lower case letter
//                    "(?=.*[A-Z])" +         //at least 1 upper case letter
//                    //"(?=.*[a-zA-Z])" +      //any letter
//                    "(?=.*[!@#$%^&*-_+=])" +    //at least 1 special character
//                    "(?=\\S+$)" +           //no white spaces
//                   ".{8,}" +               //at least 8 characters
//                    "$");

    //Regex pattern for email.
//    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
//            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\."+
//                    "[a-zA-Z0-9_+&*-]+)*@" +
//                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
//                    "A-Z]{2,7}$", Pattern.CASE_INSENSITIVE);
    //Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private boolean checkStringCmpvalues(boolean[] boolarray) {
        boolean flag = false;
        for (int i = 0; i < boolarray.length; i++) {
            if (boolarray[i])
                flag = true;
            else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * Method to validate the Street Address of any unwanted characters
     */
    public boolean checkUsername(String s) {

        String AlphaUsername = "abcdefghijklmnopqrstuvwxyz0123456789_ ";
        boolean[] value_for_each_comparison = new boolean[s.length()];

        for (int i = 0; i < s.length(); i++) {
            for (int count = 0; count < AlphaUsername.length(); count++) {
                if (s.charAt(i) == AlphaUsername.charAt(count)) {
                    value_for_each_comparison[i] = true;
                    break;
                } else {
                    value_for_each_comparison[i] = false;
                }
            }
        }
        return checkStringCmpvalues(value_for_each_comparison);
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
        if (!checkUsername(username)) {
            Toast.makeText(this, "Username cannot contain special characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.length() <= 6) {
            Toast.makeText(this, "Username is too short.", Toast.LENGTH_SHORT).show();
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

                            FirebaseDatabase.getInstance().getReference("BasicAccounts")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener (new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(BasicSignUp.this, "Registered Successfully",Toast.LENGTH_SHORT).show();
                                        //we will start the home activity here
                                         finish();
                                         Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                         homeIntent.putExtra("TYPE", getString(R.string.type_basic));
                                         startActivity(homeIntent);
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
