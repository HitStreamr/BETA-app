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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ArtistSignUp extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ArtistSignUp";

    private static final String KEY_fIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_STATE = "state";
    private static final String KEY_CITY = "city";
    private static final String KEY_ZIP = "zip";
    private static final String KEY_Country = "Country";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "passsword";


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
    private Button signupBtn;
    private RadioButton tocRadioButton;

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


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);

        editTextFirstname = findViewById(R.id.firstName);
        editTextLastname = findViewById(R.id.lastName);
        editTextEmail = findViewById(R.id.Email);
        editTextPhone = findViewById(R.id.phone);
        editTextAddress = findViewById(R.id.addressLine1);
        editTextCity = findViewById(R.id.city);
        spinnerState = findViewById(R.id.state);
        spinnerCountry = findViewById(R.id.country);
        editTextZip = findViewById(R.id.zip);
        editTextUsername = findViewById(R.id.Username);
        editTextPasssword = findViewById(R.id.Password);
        tocRadioButton = (RadioButton) findViewById(R.id.tos_button);

        signupBtn = (Button) findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(ArtistSignUp.this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

    }

    private void registerUser(){
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

        if(TextUtils.isEmpty(firstname)){
            //firstname is empty
            Toast.makeText(this, "Please enter First Name",Toast.LENGTH_SHORT).show();
            editTextFirstname.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (firstname.length() <= 26) {
            if (!(checkAlphabet(firstname))) {
                editTextFirstname.setError("Field can only have alphabets");
                return;
            }
        }

        if(TextUtils.isEmpty(lastname)){
            //lastname is empty
            Toast.makeText(this, "Please enter Last Name",Toast.LENGTH_SHORT).show();
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
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter an Email",Toast.LENGTH_SHORT).show();
            editTextEmail.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            editTextEmail.setError("Email is not valid!");
            return;
    }

        if(TextUtils.isEmpty(phone)){
            //phone is empty
            Toast.makeText(this, "Please enter a valid Phone Number",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextPhone.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        }
        else if (!VALID_PHONE_NUMBER_REGEX.matcher(phone).matches()) {
            editTextPhone.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return;
        }

        if(TextUtils.isEmpty(address)){
            //addressLine1 is empty
            Toast.makeText(this, "Please enter a Address",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextAddress.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        }

        if(TextUtils.isEmpty(city)){
            //city is empty
            Toast.makeText(this, "Please enter a city",Toast.LENGTH_SHORT).show();
            //Stop the function execution further
            editTextPhone.setError("Feild cannont be empty");
            //Stop the function execution further
            return;
        }






        if(TextUtils.isEmpty(username)){
            //firstname is empty
            Toast.makeText(this, "Please enter Username",Toast.LENGTH_SHORT).show();
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
                return;}

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // We will store the additional fields in the Firebase Database
                            Map<String, Object> ArtistSignUp = new HashMap<>();
                            ArtistSignUp.put(KEY_fIRSTNAME, firstname);
                            ArtistSignUp.put(KEY_LASTNAME, lastname);
                            ArtistSignUp.put(KEY_EMAIL, email);
                            ArtistSignUp.put(KEY_PHONE, phone);
                            ArtistSignUp.put(KEY_ADDRESS, address);
                            ArtistSignUp.put(KEY_CITY, city);
                            ArtistSignUp.put(KEY_STATE, state);
                            ArtistSignUp.put(KEY_ZIP, zip);
                            ArtistSignUp.put(KEY_Country, country);
                            ArtistSignUp.put(KEY_USERNAME, username);
                            ArtistSignUp.put(KEY_PASSWORD, password);


                            db.collection("Accounts").document("ArtistAccount").set(ArtistSignUp)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ArtistSignUp.this, "Artist sign up saved", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ArtistSignUp.this, "Error saving Artist sign up", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }


                    }
                });
    }


    @Override
    public void onClick(View view) {

        if(view == signupBtn){
            registerUser();
        }
    }
}

