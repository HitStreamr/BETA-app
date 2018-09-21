package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;
import java.util.Objects;
import java.util.regex.Pattern;

public class Account extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AccountActivity";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;

    private String type;

    private LinearLayout ArtistInfoLayout;

    //EditText
    private EditText EditTextUsername;
    private EditText EditTextEmail;
    private EditText EditTextBio;
    private EditText EditTextFirstName;
    private EditText EditTextLastName;
    private EditText EditTextAddress;
    private EditText EditTextCity;
    private EditText EditTextZip;
    private EditText EditTextPhone;
    private EditText EditTextCountry;
    private TextView UserNameText;
    private TextView LabelNameText;

    //Spinner
    private Spinner SpinnerState;

    //Buttons
    private Button SaveAccountBtn;

    //Regex pattern for password.
    /*private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&*-_+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");*/

    //Regex pattern for email.
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
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
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        //Layout
        LinearLayout artistInfoLayout = findViewById(R.id.privateInfo);

        //EditText
        EditTextUsername = findViewById(R.id.accountUsername);
        EditTextEmail = findViewById(R.id.accountEmail);
        EditTextBio = findViewById(R.id.Bio);
        EditTextFirstName = findViewById(R.id.accountFirstName);
        EditTextLastName = findViewById(R.id.accountLastName);
        EditTextAddress = findViewById(R.id.accountAddress);
        EditTextCity = findViewById(R.id.accountCity);
        EditTextZip = findViewById(R.id.accountZip);
        EditTextPhone = findViewById(R.id.accountPhone);
        EditTextCountry = findViewById(R.id.accountCountry);
        UserNameText = findViewById(R.id.UserNametext);
        LabelNameText = findViewById(R.id.Labeltext);
        //Spinners
        SpinnerState = findViewById(R.id.accountState);


        //Button
        SaveAccountBtn = findViewById(R.id.saveAccount);
        SaveAccountBtn.setOnClickListener(this);

        type = getIntent().getStringExtra("TYPE");
        if (type.equals(getString(R.string.type_artist))) {

            artistInfoLayout.setVisibility(View.VISIBLE);

            if (userID != null) {
                myRef = database.getReference("ArtistAccounts").child(userID);
            }
        } else if (type.equals(getString(R.string.type_basic))) {
            artistInfoLayout.setVisibility(View.GONE);
             if (userID != null) {
                myRef = database.getReference("BasicAccounts").child(userID);
             }
        } else if (type.equals(getString(R.string.type_label))) {


            UserNameText.setVisibility(View.GONE);
            LabelNameText.setVisibility(View.VISIBLE);
            ArtistInfoLayout.setVisibility(View.VISIBLE);
            EditTextUsername.setEnabled(false);
            EditTextUsername.setFocusable(false);
            EditTextEmail.setEnabled(false);
            EditTextEmail.setFocusable(false);

            if (userID != null) {
                myRef = database.getReference("LabelAccounts").child(userID);
            }

        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (type.equals(getString(R.string.type_artist))) {
                    showArtistData(dataSnapshot);

                } else if (type.equals(getString(R.string.type_basic))) {
                    showBasicData(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showArtistData(DataSnapshot dataSnapshot) {

        String firstname = dataSnapshot.child("firstname").getValue(String.class);
        String lastname = dataSnapshot.child("lastname").getValue(String.class);
        String email = dataSnapshot.child("email").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);
        String address = dataSnapshot.child("address").getValue(String.class);
        String city = dataSnapshot.child("city").getValue(String.class);
        String state = dataSnapshot.child("state").getValue(String.class);
        String zip = dataSnapshot.child("zip").getValue(String.class);
        String country = dataSnapshot.child("country").getValue(String.class);
        String phone = dataSnapshot.child("phone").getValue(String.class);

        ArtistUser artist = new ArtistUser(firstname, lastname, email, username, address, city, state, country, phone, zip);

        EditTextFirstName.setText(artist.getFirstname());
        EditTextLastName.setText(artist.getLastname());
        EditTextEmail.setText(artist.getEmail());
        EditTextUsername.setText(artist.getUsername());
        EditTextAddress.setText(artist.getAddress());
        EditTextCity.setText(artist.getCity());
        EditTextZip.setText(artist.getZip());
        EditTextPhone.setText(artist.getPhone());
        EditTextCountry.setText(artist.getCountry());
        SpinnerState.setSelection(getIndex(SpinnerState, artist.getState()));

    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    public void showBasicData(DataSnapshot dataSnapshot) {

        String email = dataSnapshot.child("email").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);

        User basic = new User(username, email);

        EditTextEmail.setText(basic.getEmail());
        EditTextUsername.setText(basic.getUsername());
    }

    private void registerUser() {
        final String username = EditTextUsername.getText().toString().trim();
        final String email = EditTextEmail.getText().toString().trim();
        //String password = mPasswordField.getText().toString().trim();


        if (!validateEmail(email) | !validateUsername(username)) {
            return;
        }
        /*//If validations is ok we will first show progressbar
        progressDialog.setMessage("Registering New User...");
        progressDialog.show();*/


        User user = new User(username, email);
        FirebaseDatabase.getInstance().getReference("BasicAccounts")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Account.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Account.this, "Could not update. Please try again", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void registerArtist() {
        final String firstname = EditTextFirstName.getText().toString().trim();
        final String lastname = EditTextLastName.getText().toString().trim();
        final String email = EditTextEmail.getText().toString().trim();
        //final String password = Edit.getText().toString().trim();
        final String username = EditTextUsername.getText().toString().trim();
        final String address = EditTextAddress.getText().toString().trim();
        final String city = EditTextCity.getText().toString().trim();
        final String state = SpinnerState.getSelectedItem().toString();
        final String zip = EditTextZip.getText().toString().trim();
        final String country = EditTextCountry.getText().toString().trim();
        final String phone = EditTextPhone.getText().toString().trim();


        if (!validateFirstName(firstname) | !validateLastName(lastname) | !validateEmail(email) | !validateAddressLine(address)
                | !validateCity(city) | !validateUsername(username) | !validatePhone(phone)
                | !validateZip(zip)) {
            return;
        }

        ArtistUser artist_object = new ArtistUser(firstname, lastname, email, username, address, city, state, country, phone, zip);

        FirebaseDatabase.getInstance().getReference("ArtistAccounts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(artist_object)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Account.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Account.this, "Update Failed, Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Check if first name input is valid.
     *
     * @param firstname first name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateFirstName(String firstname) {
        if (firstname.isEmpty()) {
            EditTextFirstName.setError("Field can't be empty");
            return false;
        } else if (firstname.length() <= 26) {
            if (!(checkAlphabet(firstname))) {
                EditTextFirstName.setError("First name must only have letters");
                return false;
            }
            return true;
        } else {
            EditTextFirstName.setError(null);
            return true;
        }
    }

    /**
     * Check if last name input is valid.
     *
     * @param lastname last name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateLastName(String lastname) {
        if (lastname.isEmpty()) {
            EditTextLastName.setError("Field can't be empty");
            return false;
        } else if (lastname.length() <= 26) {
            if (!(checkAlphabet(lastname))) {
                EditTextLastName.setError("Last name must only have letters");
                return false;
            }
            return true;
        } else {
            EditTextLastName.setError(null);
            return true;
        }
    }

    /**
     * Check if address input is valid.
     *
     * @param address address line
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateAddressLine(String address) {
        if (address.isEmpty()) {
            EditTextAddress.setError("Field can't be empty");
            return false;
        } else if (!checkAlphaNumeric(address)) {
            EditTextAddress.setError("Address is not valid.");
            return false;
        } else {
            EditTextAddress.setError(null);
            return true;
        }
    }

    /**
     * Check if email input is valid.
     *
     * @param email email
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            EditTextEmail.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            EditTextEmail.setError("Email is not valid!");
            return false;
        } else {
            EditTextEmail.setError(null);
            return true;
        }
    }

    /**
     * Check if phone number input is valid.
     *
     * @param phone phone number
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validatePhone(String phone) {
        if (phone.isEmpty()) {
            EditTextPhone.setError("Field can't be empty");
            return false;
        } else if (!VALID_PHONE_NUMBER_REGEX.matcher(phone).matches()) {
            EditTextPhone.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return false;
        } else {
            EditTextPhone.setError(null);
            return true;
        }
    }

    /**
     * Check if city input is valid.
     *
     * @param city city
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateCity(String city) {
        if (city.isEmpty()) {
            EditTextCity.setError("Field can't be empty");
            return false;
        } else {
            EditTextCity.setError(null);
            return true;
        }
    }

    /**
     * Check if zip input is valid.
     *
     * @param zip zip
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateZip(String zip) {
        if (zip.isEmpty()) {
            EditTextZip.setError("Field can't be empty");
            return false;
        } else if (!VALID_ZIP_REGEX.matcher(zip).matches()) {
            EditTextZip.setError("Zip is invalid");
            return false;
        } else {
            EditTextZip.setError(null);
            return true;
        }
    }


    /**
     * Check if artist name input is valid.
     *
     * @param artist artist name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateUsername(String artist) {
        if (artist.isEmpty()) {
            EditTextUsername.setError("Field can't be empty");
            return false;
        } else if (!checkUsername(artist)) {
            EditTextUsername.setError("Username cannot contain special characters.");
            return false;
        } else if (artist.length() <= 6) {
            EditTextUsername.setError("Username is too short.");
            return false;
        } else {
            EditTextUsername.setError(null);
            return true;
        }
    }

    /**
     * Method to validate whether the input string entered contains only
     * Alphabetical characters.
     * <p>
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
    public boolean checkAlphaNumeric(String s) {

        String AlphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
        boolean[] value_for_each_comparison = new boolean[s.length()];

        for (int i = 0; i < s.length(); i++) {
            for (int count = 0; count < AlphaNumeric.length(); count++) {
                if (s.charAt(i) == AlphaNumeric.charAt(count)) {
                    value_for_each_comparison[i] = true;
                    break;
                } else {
                    value_for_each_comparison[i] = false;
                }
            }
        }
        return checkStringCmpvalues(value_for_each_comparison);
    }

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


    @Override
    public void onClick(View view) {
        if(view == SaveAccountBtn ){
            if(type.equals(getString(R.string.type_artist))){
                registerArtist();
            }
            else if(type.equals(getString(R.string.type_basic))){
                registerUser();
            }
        }
}
}