package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ArtistSignUp extends AppCompatActivity {

    private static final String TAG = "ArtistSignUp";

    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "passsword";
    private static final String KEY_fIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_STATE = "state";
    private static final String KEY_CITY = "city";
    private static final String KEY_ZIP = "zip";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHONE = "phone";

    private EditText editTextEmail;
    private EditText editTextPasssword;
    private EditText editTextFirstname;
    private EditText editTextLastname;
    private EditText editTextAddress;
    private EditText editTextState;
    private EditText editTextCZip;
    private EditText editTextUsername;
    private EditText editTextPhone;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);
        editTextEmail = findViewById(R.id.);
        editTextPasssword;
        editTextFirstname;
        editTextLastname;
        editTextAddress;
        editTextState;
        editTextCZip;
        editTextUsername;
        EditText editTextPhone;

    }
}
