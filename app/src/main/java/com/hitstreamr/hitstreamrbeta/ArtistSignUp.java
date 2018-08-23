package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class ArtistSignUp extends AppCompatActivity implements View.OnClickListener {

    private static final String ARTIST_TAG = "ArtistSignUp";

    //private static final String KEY_fIRSTNAME = "firstname";
    //private static final String KEY_LASTNAME = "lastname";
    //private static final String KEY_EMAIL = "email";
    //private static final String KEY_PHONE = "phone";
    //private static final String KEY_ADDRESS = "address";
    //private static final String KEY_STATE = "state";
    //private static final String KEY_CITY = "city";
    //private static final String KEY_ZIP = "zip";
    //private static final String KEY_Country = "Country";
    //private static final String KEY_USERNAME = "username";
    //private static final String KEY_PASSWORD = "passsword";


    private EditText editTextFirstname;
    private EditText editTextLastname;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private EditText editTextCity;
    private Spinner spinnerState;
    private EditText editTextZip;
    private Spinner spinnerCountry;
    private EditText editTextUsername;
    private EditText editTextPasssword;
    private Button signupBtn, backbtn;
    private RadioButton tocRadioButton;
    private TextView signintext;

    private FirebaseAuth mAuth;

    //Regex pattern for password.
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    //Regex pattern for email.
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_PHONE_NUMBER_REGEX =
            Pattern.compile(("\\d{3}-\\d{3}-\\d{4}"));

    private static final Pattern VALID_ZIP_REGEX =
            Pattern.compile(("\\d{5}"));

    /*
     * Method to validate whether the input string entered contains only
     * Alphabetical characters.
     *
     * 1. This method also helps in making sure to handle the edge case i.e space.
     */
    public boolean checkAlphabet(String s) {

        String Alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        boolean[] value_for_each_comparison = new boolean[s.length()];

        for (int i = 0; i < s.length(); i++) {
            for (int count = 0; count < Alphabets.length(); count++) {
                if (s.charAt(i) == Alphabets.charAt(count)) {
                    value_for_each_comparison[i] = true;
                    break;
                } else {
                    value_for_each_comparison[i] = false;
                }
            }
        }

        return checkStringCmpvalues(value_for_each_comparison);
    }

    /*
     * Method to support the above Method named checkAlphabet in accomplishing the task
     * to validate the input String for Alphabetical characters only.
     */
    public boolean checkStringCmpvalues(boolean[] boolarray) {
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

    public boolean checkAlphaNumeric(String s){

        String AlphaNumeric ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
        boolean[] value_for_each_comparison = new boolean[s.length()];

        for(int i=0; i<s.length(); i++){
            for(int count = 0; count<AlphaNumeric.length(); count++){
                if(s.charAt(i) == AlphaNumeric.charAt(count)){
                    value_for_each_comparison[i] = true;
                    break;
                }else{
                    value_for_each_comparison[i] = false;
                }
            }
        }
        return checkStringCmpvalues(value_for_each_comparison);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);


        editTextFirstname = (EditText)findViewById(R.id.firstName);
        editTextLastname = (EditText)findViewById(R.id.lastName);
        editTextEmail = (EditText)findViewById(R.id.Email);
        editTextPhone = (EditText)findViewById(R.id.phone);
        editTextAddress = (EditText)findViewById(R.id.addressLine1);
        editTextCity = (EditText)findViewById(R.id.city);
        spinnerState = (Spinner)findViewById(R.id.state);
        spinnerCountry = (Spinner)findViewById(R.id.country);
        editTextZip = (EditText)findViewById(R.id.zip);
        editTextUsername = (EditText)findViewById(R.id.Username);
        editTextPasssword = (EditText)findViewById(R.id.Password);
        signintext = (TextView)findViewById(R.id.textviewsignin);

        tocRadioButton = (RadioButton) findViewById(R.id.tos_button);
        backbtn = (Button) findViewById(R.id.backBtn);
        signupBtn = (Button) findViewById(R.id.signup_button);

        //Listeners
        //editTextFirstname.setOnClickListener(this);
        //editTextLastname.setOnClickListener(this);
        //editTextEmail.setOnClickListener(this);
       // editTextPhone.setOnClickListener(this);
        //editTextAddress.setOnClickListener(this);
       // editTextCity.setOnClickListener(this);
        //spinnerState.setOnClickListener(this);
       // spinnerCountry.setOnClickListener(this);
       // editTextZip.setOnClickListener(this);
       // editTextUsername.setOnClickListener(this);
       // editTextPasssword.setOnClickListener(this);
        signintext.setOnClickListener(this);

        tocRadioButton.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        signupBtn.setOnClickListener(ArtistSignUp.this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]
        if(mAuth.getCurrentUser() !=null) {
            //home activity here
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

    }

    private void registerUser() {
        Log.e(ARTIST_TAG,"i AM TRYING TO PRINT  ARTIST");
        final String firstname = editTextFirstname.getText().toString().trim();
        final String lastname = editTextLastname.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        final String city = editTextCity.getText().toString().trim();
        final String state = spinnerState.toString().trim();
        final String zip = editTextZip.getText().toString().trim();
        final String country = spinnerCountry.toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPasssword.getText().toString().trim();
        Log.e(ARTIST_TAG,"i AM TRYING TO PRINT  ARTIST");

        if (TextUtils.isEmpty(firstname)) {
            //firstname is empty
            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show();
            editTextFirstname.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (firstname.length() <= 26) {
            if (!(checkAlphabet(firstname))) {
                editTextFirstname.setError("Field can only have alphabets");
                return;
            }
        }

        if (TextUtils.isEmpty(lastname)) {
            //lastname is empty
            Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextLastname.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (lastname.length() <= 26) {
            if (!(checkAlphabet(lastname))) {
                editTextLastname.setError("Field can only have alphabets");
                return;
            }
        }
        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter an Email", Toast.LENGTH_SHORT).show();
            editTextEmail.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            editTextEmail.setError("Email is not valid!");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            //phone is empty
            Toast.makeText(this, "Please enter a valid Phone Number", Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextPhone.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (!VALID_PHONE_NUMBER_REGEX.matcher(phone).matches()) {
            editTextPhone.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            //addressLine1 is empty
            Toast.makeText(this, "Please enter a Address", Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextAddress.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (!(checkAlphaNumeric(address))) {
            editTextAddress.setError("Field can only have alphabets and numbers");
            return;
        }

        if (TextUtils.isEmpty(city)) {
            //city is empty
            Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextPhone.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        }

        if (TextUtils.isEmpty(zip)) {
            //addressLine1 is empty
            Toast.makeText(this, "Please enter a Zip", Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextAddress.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (!VALID_ZIP_REGEX.matcher(zip).matches()) {
            editTextEmail.setError("Zip is not valid!");
            return;
        }


        if (TextUtils.isEmpty(username)) {
            //firstname is empty
            Toast.makeText(this, "Please enter Username", Toast.LENGTH_SHORT).show();
            editTextUsername.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
            //Number values allowed... underscores allowed... no spaces allowed
        } else if (username.length() <= 20) {
            if (!(checkAlphabet(username))) {
                editTextUsername.setError("Field can only have alphabets");
                return;
            }
        }


        //if(password.length()<8) {
        //xtPasssword.setError("Minimum length of password characters is 8");
        //editTextPasssword.requestFocus();
        if (TextUtils.isEmpty(password)) {
            editTextPasssword.setError("Field can't be empty");
            return;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            editTextPasssword.setError("Password too weak");
            return;
        }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // We will store the additional fields in the Firebase Database

                                ArtistUser user = new ArtistUser(
                                        firstname,
                                        lastname,
                                        email,
                                        username,
                                        password,
                                        address,
                                        city,
                                        state,
                                        phone,
                                        zip
                                );

                                FirebaseDatabase.getInstance().getReference("ArtistUsers")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ArtistSignUp.this, "Registred Successfully", Toast.LENGTH_SHORT).show();
                                            //we will start the home activity here
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        } else {
                                            //Display a failure message
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(ArtistSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    @Override
    public void onClick(View view) {
        if (view == signupBtn){
                registerUser();
            }else{
                Toast.makeText(ArtistSignUp.this, "Please agree to the Terms and Conditions.",Toast.LENGTH_SHORT).show();
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

