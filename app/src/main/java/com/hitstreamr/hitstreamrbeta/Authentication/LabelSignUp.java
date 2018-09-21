package com.hitstreamr.hitstreamrbeta.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.hitstreamr.hitstreamrbeta.LabelDashboard;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.UserTypes.Label;

import java.util.regex.Pattern;

public class LabelSignUp extends AppCompatActivity implements View.OnClickListener {

    // Inputs
    private EditText mFirstName, mLastName, mEmail, mPassword, mLabel, mAddress, mCity, mZipcode, mPhone;
    private Spinner mState, mCountry;
    // Add address line 1 and 2?

    // Buttons
    private Button signup, goBack;
    private RadioButton termsCond;

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
                    "(?=.*[!@#$%^&*-_+=])" +    //at least 1 special character
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
        setContentView(R.layout.activity_label_sign_up);
        progressDialog = new ProgressDialog(this);

        // Views
        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.Password);
        mLabel = findViewById(R.id.labelName);
        mAddress = findViewById(R.id.addressLine1);
        mCity = findViewById(R.id.city);
        mState = findViewById(R.id.state);
        mZipcode = findViewById(R.id.zip);
        mCountry = findViewById(R.id.country);
        mPhone = findViewById(R.id.phone);

        // Buttons
        signup = findViewById(R.id.signup_button);
        goBack = findViewById(R.id.backBtn);
        termsCond = findViewById(R.id.tos_button);

        // Listeners
        signup.setOnClickListener(this);
        goBack.setOnClickListener(this);

    }

    /**
     * Read the inputs from the user, check if inputs are valid, then add to the database.
     */
    private void registerLabel() {
        final String firstname = mFirstName.getText().toString().trim();
        final String lastname = mLastName.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        final String label = mLabel.getText().toString().trim();
        final String address = mAddress.getText().toString().trim();
        final String city = mCity.getText().toString().trim();
        final String state = mState.getSelectedItem().toString();
        final String zipcode = mZipcode.getText().toString().trim();
        final String country = mCountry.getSelectedItem().toString();
        final String phone = mPhone.getText().toString().trim();


        if (!validateFirstName(firstname) | !validateLastName(lastname) | !validateEmail(email) |!validatePassword(password)
                | !validateAddressLine(address) | !validateCity(city) | !validateLabel(label) | !validatePhone(phone)
                | !validateZip(zipcode) | !validateToc() | validateState(state)) {
            return;
        }

        //If validations are ok we will first show progressbar
        progressDialog.setMessage("Registering new Label...");
        progressDialog.show();

        // Add label to the database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Store the additional fields in the Firebase Database
                            Label label_object = new Label(firstname, lastname, email, password,
                                    label, address, city, state, zipcode, country, phone);

                            FirebaseDatabase.getInstance().getReference("LabelAccounts")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    // Method invocation 'getUid' may produce 'java.lang.NullPointerException'
                                    // Should catch? Throw an exception?
                                    .setValue(label_object).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LabelSignUp.this, "Registered Successfully",
                                                Toast.LENGTH_SHORT).show();
                                        //we will start the home activity here
                                        finish();
                                        Intent homeIntent = new Intent(getApplicationContext(), LabelDashboard.class);
                                        homeIntent.putExtra("TYPE", getString(R.string.type_label));
                                        startActivity(homeIntent);
                                    } else {
                                        //Display a failure message
                                        Toast.makeText(LabelSignUp.this, "Registration Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(LabelSignUp.this, "Could not register. Please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Check if first name input is valid.
     * @param firstname first name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateFirstName(String firstname) {
        if (firstname.isEmpty()) {
            mFirstName.setError("Field can't be empty");
            return false;
        } else if (firstname.length() <= 26) {
            if (!(checkAlphabet(firstname))) {
                mFirstName.setError("First name must only have letters");
                return false;
            }
            return true;
        } else {
            mFirstName.setError(null);
            return true;
        }
    }

    /**
     * Check if last name input is valid.
     * @param lastname last name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateLastName(String lastname) {
        if (lastname.isEmpty()) {
            mLastName.setError("Field can't be empty");
            return false;
        } else if (lastname.length() <= 26) {
            if (!(checkAlphabet(lastname))) {
                mLastName.setError("Last name must only have letters");
                return false;
            }
            return true;
        } else {
            mLastName.setError(null);
            return true;
        }
    }

    /**
     * Check if address input is valid.
     * @param address address line
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateAddressLine(String address) {
        if (address.isEmpty()) {
            mAddress.setError("Field can't be empty");
            return false;
        } else if (!checkAlphaNumeric(address)){
            mAddress.setError("Address is not valid.");
            return false;
        } else {
            mAddress.setError(null);
            return true;
        }
    }

    /**
     * Check if email input is valid.
     * @param email email
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            mEmail.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            mEmail.setError("Email is not valid!");
            return false;
        } else {
            mEmail.setError(null);
            return true;
        }
    }

    /**
     * Check if phone number input is valid.
     * @param phone phone number
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validatePhone(String phone) {
        if (phone.isEmpty()) {
            mPhone.setError("Field can't be empty");
            return false;
        } else if (!VALID_PHONE_NUMBER_REGEX.matcher(phone).matches()) {
            mPhone.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return false;
        }else {
            mPhone.setError(null);
            return true;
        }
    }

    /**
     * Check if city input is valid.
     * @param city city
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateCity(String city) {
        if (city.isEmpty()) {
            mCity.setError("Field can't be empty");
            return false;
        } else {
            mCity.setError(null);
            return true;
        }
    }

    /**
     * Check if zipcode input is valid.
     * @param zipcode zipcode
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateZip(String zipcode) {
        if (zipcode.isEmpty()) {
            mZipcode.setError("Field can't be empty");
            return false;
        }  else if (!VALID_ZIP_REGEX.matcher(zipcode).matches()) {
            mZipcode.setError("Zip is invalid");
            return false;
        }  else {
            mZipcode.setError(null);
            return true;
        }
    }

    /**
     * Check if password input is valid.
     * @param password password
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            mPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            mPassword.setError("Password too weak");
            return false;
        } else {
            mPassword.setError(null);
            return true;
        }
    }

    /**
     * Check if label name input is valid.
     * @param label label name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateLabel(String label) {
        if (label.isEmpty()) {
            mLabel.setError("Field can't be empty");
            return false;
        } else {
            mLabel.setError(null);
            return true;
        }
    }

    // not working yet
    private boolean validateState(String state) {
        if (!state.equals("Select State")) {
            Toast.makeText(this, "Please select a state.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if the user agrees to Terms and Conditions
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateToc() {
        if (!termsCond.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();            return false;
        } else {
            termsCond.setError(null);
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

    /**
     * Override the onClick function
     * @param view view
     */
    @Override
    public void onClick(View view) {
        if (view == signup) {
            registerLabel();
        }
        if (view == goBack) {
            //will open account type activity
            finish();
            startActivity(new Intent(getApplicationContext(), AccountType.class));
        }
    }
}
