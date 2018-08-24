package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);
        editTextFirstname = findViewById(R.id.firstName);
        editTextLastname = findViewById(R.id.lastName);
        editTextEmail = findViewById(R.id.email);
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
        // [END initialize_auth]
        if(mAuth.getCurrentUser() !=null){
            //home activity here
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        Log.d(TAG, PASSWORD_PATTERN.toString());
    }

    private boolean validateFirstName() {
        String firstNameInput = editTextFirstname.getText().toString().trim();

        if (firstNameInput.isEmpty()) {
            editTextFirstname.setError("Field can't be empty");
            return false;
        } else if (firstNameInput.length() <= 26) {
            if (!(checkAlphabet(firstNameInput))) {
                editTextFirstname.setError("Field can only have alphabets");
                return false;
            }
            return true;
        } else {
            //editTextFirstname.setError(("*First name should be alphabets only & less than 26 characters");
            editTextFirstname.setError(null);
            return true;
        }
    }

    private boolean validateLastName() {
        String lastNameInput = editTextFirstname.getText().toString().trim();

        if (lastNameInput.isEmpty()) {
            editTextLastname.setError("Field can't be empty");
            return false;
        } else if (lastNameInput.length() <= 26) {
            if (!(checkAlphabet(lastNameInput))) {
                editTextFirstname.setError("Field can only have alphabets");
                return false;
            }
            return true;
        } else {
            //editTextFirstname.setError(("*Last name should be alphabets only & less than 26 characters");
            editTextLastname.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = editTextEmail.getText().toString().trim();

        if (emailInput.isEmpty()) {
            editTextEmail.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(emailInput).matches()) {
            editTextPasssword.setError("Email is not valid!");
            return false;
        } else {
            editTextEmail.setError(null);
            return true;
        }
    }

    private boolean validatePhone() {
        String phoneInput = editTextFirstname.getText().toString().trim();

        if (phoneInput.isEmpty()) {
            editTextPhone.setError("Field can't be empty");
            return false;
        } else if (!VALID_PHONE_NUMBER_REGEX.matcher(phoneInput).matches()) {
            editTextPasssword.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return false;
        }else {
            editTextPhone.setError(null);
            return true;
        }
    }

    private boolean validateAddressLine() {
        String AddressLineInput = editTextFirstname.getText().toString().trim();

        if (AddressLineInput.isEmpty()) {
            editTextAddress.setError("Field can't be empty");
            return false;
        } else {
            editTextAddress.setError(null);
            return true;
        }
    }

    private boolean validateCity() {
        String CityInput = editTextFirstname.getText().toString().trim();

        if (CityInput.isEmpty()) {
            editTextCity.setError("Field can't be empty");
            return false;
        } else {
            editTextCity.setError(null);
            return true;
        }
    }

    private boolean validateUsername() {
        String usernameInput = editTextFirstname.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            editTextUsername.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 15) {
            editTextUsername.setError("Username too long");
            return false;
        } else {
            editTextUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = editTextFirstname.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            editTextPasssword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            editTextPasssword.setError("Password too weak");
            return false;
        } else {
            editTextPasssword.setError(null);
            return true;
        }
    }

    private boolean validateToc() {
        if (tocRadioButton.isChecked()) {
            tocRadioButton.setError("Field can't be empty");
            return false;
        } else {
            tocRadioButton.setError(null);
            return true;
        }

    }

    @Override
    public void onClick(View view) {
        if(view == signupBtn){


           // if (!validateFirstName() | !validateLastName() | !validateEmail() | !validatePhone() | !validateUsername() | !validateAddressLine() | !validateCity() | !validateUsername() | !validatePassword() | !validateToc()) {
             //   return;
           // }

            final String firstname = editTextFirstname.getText().toString();
            final String lastname = editTextLastname.getText().toString();
            final String email = editTextEmail.getText().toString();
            final String phone = editTextPhone.getText().toString();
            final String address = editTextAddress.getText().toString();
            final String city = editTextCity.getText().toString();
            final String state = spinnerState.toString();
            final String zip = editTextZip.getText().toString();
            final String country = spinnerCountry.toString();
            final String username = editTextUsername.getText().toString();
            final String password = editTextPasssword.getText().toString();

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

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // We will store the additional fields in the Firebase Database
                                ArtistUser artist = new ArtistUser(firstname,lastname,email,username,password,address,city,state, phone, zip);

                                FirebaseDatabase.getInstance().getReference("Artist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(artist).addOnCompleteListener (new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ArtistSignUp.this, "Registered Successfully",Toast.LENGTH_SHORT).show();
                                            //we will start the home activity here
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        }else {
                                            //Display a failure message
                                        }
                                    }
                                });


                            }else{
                                Toast.makeText(ArtistSignUp.this, "Could not register. Please try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
}

