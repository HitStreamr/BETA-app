package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);

        editTextEmail = findViewById(R.id.Email);
        editTextPasssword = findViewById(R.id.Password);
        editTextFirstname = findViewById(R.id.firstName);
        editTextLastname = findViewById(R.id.lastName);
        editTextAddress = findViewById(R.id.addressLine1);
        editTextCity = findViewById(R.id.city);
        spinnerState = findViewById(R.id.state);
        spinnerCountry = findViewById(R.id.country);
        editTextZip = findViewById(R.id.zip);
        editTextUsername = findViewById(R.id.Username);
        editTextPhone = findViewById(R.id.phone);

        signupBtn = (Button) findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(ArtistSignUp.this);
    }

    @Override
    public void onClick(View view) {
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPasssword.getText().toString();
        String firstname = editTextFirstname.getText().toString();
        String lastname = editTextLastname.getText().toString();
        String address = editTextAddress.getText().toString();
        String state = spinnerState.toString();
        String country = spinnerCountry.toString();
        String city = editTextCity.getText().toString();
        String zip = editTextZip.getText().toString();
        String phone = editTextPhone.getText().toString();

        Map<String, Object> ArtistSignUp = new HashMap<>();
        ArtistSignUp.put(KEY_USERNAME, username);
        ArtistSignUp.put(KEY_EMAIL, email);
        ArtistSignUp.put(KEY_PASSWORD, password);
        ArtistSignUp.put(KEY_fIRSTNAME, firstname);
        ArtistSignUp.put(KEY_LASTNAME, lastname);
        ArtistSignUp.put(KEY_ADDRESS, address);
        ArtistSignUp.put(KEY_CITY, city);
        ArtistSignUp.put(KEY_STATE, state);
        ArtistSignUp.put(KEY_Country, country);
        ArtistSignUp.put(KEY_ZIP, zip);
        ArtistSignUp.put(KEY_PHONE, phone);

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

    }
}

