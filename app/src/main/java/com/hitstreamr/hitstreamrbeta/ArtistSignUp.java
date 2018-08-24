package com.hitstreamr.hitstreamrbeta;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;

public class ArtistSignUp extends AppCompatActivity implements View.OnClickListener {
    // Inputs
    private EditText editTextFirstname, editTextLastname, editTextEmail, editTextPhone, editTextAddress,
            editTextCity, editTextZip, editTextUsername, editTextPasssword;
    private Spinner spinnerCountry, spinnerState;

    // Buttons
    private Button signup, goBack;
    private RadioButton tocRadioButton;

    private ProgressDialog progressDialog;

    // Access a Cloud Firebase instance from your Activity
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


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
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\."+
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$", Pattern.CASE_INSENSITIVE);
    //Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_PHONE_NUMBER_REGEX =
            Pattern.compile(("[0-9]{10}"));
    //Pattern.compile(("\\d{3}-\\d{3}-\\d{4}"));

    private static final Pattern VALID_ZIP_REGEX =
            Pattern.compile(("[0-9]{5}"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);
        progressDialog = new ProgressDialog(this);


        // Views
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

        // Buttons
        signup = findViewById(R.id.signup_button);
        goBack = findViewById(R.id.backBtn);
        tocRadioButton = findViewById(R.id.tos_button);

        // Listeners
        signup.setOnClickListener(this);
        goBack.setOnClickListener(this);

    }

    /**
     * Read the inputs from the user, check if inputs are valid, then add to the database.
     */
    private void registerArtist() {
        final String firstname = editTextFirstname.getText().toString().trim();
        final String lastname = editTextLastname.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPasssword.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        final String city = editTextCity.getText().toString().trim();
        final String state = spinnerState.getSelectedItem().toString();
        final String zipcode = editTextZip.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String country =  spinnerCountry.getSelectedItem().toString();
        final String phone = editTextPhone.getText().toString().trim();


        if (!validateFirstName(firstname) | !validateLastName(lastname) | !validateEmail(email) | !validatePhone(phone)
                | !validateAddressLine(address) | !validateZip(zipcode) | !validateCity(city) | !validateUsername(username) | !validatePassword(password) | !validateToc()) {
            return;
        }

        //If validations is ok we will first show progressbar
        progressDialog.setMessage("Registering new Artist...");
        progressDialog.show();

        // Add Artist to the database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Store the additional fields in the Firebase Database
                            Artist Artist_object = new Artist(firstname, lastname, email, phone, address,
                                    city, state, zipcode, country, username);

                            FirebaseDatabase.getInstance().getReference("ArtistAccounts")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    // Method invocation 'getUid' may produce 'java.lang.NullPointerException'
                                    // Should catch? Throw an exception?
                                    .setValue(Artist_object).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ArtistSignUp.this, "Registered Successfully",
                                                Toast.LENGTH_SHORT).show();
                                        //we will start the home activity here
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    } else {
                                        //Display a failure message
                                        Toast.makeText(ArtistSignUp.this, "Registration Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(ArtistSignUp.this, "Could not register. Please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private boolean validateFirstName(String firstname) {

        if (firstname.isEmpty()) {
            editTextFirstname.setError("Field can't be empty");
            return false;
        } else if (firstname.length() <= 26) {
            if (!(checkAlphabet(firstname))) {
                editTextFirstname.setError("First name must only have letters");
                return false;
            }
            return true;
        } else {
            editTextFirstname.setError(null);
            return true;
        }
    }


    private boolean validateLastName(String lastname) {

        if (lastname.isEmpty()) {
            editTextLastname.setError("Field can't be empty");
            return false;
        } else if (lastname.length() <= 26) {
            if (!(checkAlphabet(lastname))) {
                editTextLastname.setError("Last name must only have letters");
                return false;
            }
            return true;
        } else {
            editTextLastname.setError(null);
            return true;
        }
    }

    private boolean validateAddressLine(String address) {

        if (address.isEmpty()) {
            editTextAddress.setError("Field can't be empty");
            return false;
        } else if (!checkAlphaNumeric(address)){
            editTextAddress.setError("Address is not valid.");
            return false;
        } else {
            editTextAddress.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String email) {

        if (email.isEmpty()) {
            editTextEmail.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            editTextEmail.setError("Email is not valid!");
            return false;
        } else {
            editTextEmail.setError(null);
            return true;
        }
    }


    private boolean validatePhone(String phone) {

        if (phone.isEmpty()) {
            editTextPhone.setError("Field can't be empty");
            return false;
        } else if (!VALID_PHONE_NUMBER_REGEX.matcher(phone).matches()) {
            editTextPhone.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return false;
        }else {
            editTextPhone.setError(null);
            return true;
        }
    }

    private boolean validateCity(String city) {

        if (city.isEmpty()) {
            editTextCity.setError("Field can't be empty");
            return false;
        } else {
            editTextCity.setError(null);
            return true;
        }
    }


    private boolean validateZip(String zipcode) {

        if (zipcode.isEmpty()) {
            editTextZip.setError("Field can't be empty");
            return false;
        }  else if (!VALID_ZIP_REGEX.matcher(zipcode).matches()) {
            editTextZip.setError("Zip is invalid");
            return false;
        }  else {
            editTextZip.setError(null);
            return true;
        }
    }

    private boolean validateUsername(String username) {

        if (username.isEmpty()) {
            editTextUsername.setError("Field can't be empty");
            return false;
        } else if (username.length() > 15) {
            editTextUsername.setError("Username too long");
            return false;
        } else {
            editTextUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {

        if (password.isEmpty()) {
            editTextPasssword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            editTextPasssword.setError("Password too weak");
            return false;
        } else {
            editTextPasssword.setError(null);
            return true;
        }
    }


    private boolean validateToc() {
        if (!tocRadioButton.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            tocRadioButton.setError(null);
            return true;
        }
    }

    /**
     * Method to validate whether the input string entered contains only
     * Alphabetical characters.
     *
     * 1. This method also helps in making sure to handle the edge case i.e space.
     */
    private boolean checkAlphabet(String s) {

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

    /**
     * Method to support the above Method named checkAlphabet in accomplishing the task
     * to validate the input String for Alphabetical characters only.
     */
    private boolean checkStringCmpvalues(boolean[] boolarray) {
        boolean flag = false;
        for (boolean aBoolarray : boolarray) {
            if (aBoolarray)
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
    public void onClick(View view) {
        if (view == signup) {
            registerArtist();
        }
        if (view == goBack) {
            //will open account type activity
            finish();
            startActivity(new Intent(getApplicationContext(), AccountType.class));
        }
    }
}