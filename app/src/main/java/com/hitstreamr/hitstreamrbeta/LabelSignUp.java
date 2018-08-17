package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LabelSignUp extends AppCompatActivity {

    private static final String TAG = "LabelSignUp";

    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ORGANIZATION = "organization";
    private static final String KEY_ADDRESS = "address";
//    add address line 1 and 2
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_ZIPCODE = "zipcode";
    private static final String KEY_PHONE = "phone";

    private EditText mFirstName, mLastName, mEmail, mOrganization, mAddress, mCity, mState, mZipcode, mPhone;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_label_sign_up);
        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.Email);
//        mOrganization = findViewById(R.id.Organization);
        mAddress = findViewById(R.id.addressLine1);
//        mCity = findViewById(R.id.City);
//        mState = findViewById(R.id.State);
//        mZipcode = findViewById(R.id.Zipcode);
    }

    private void registerLabel() {
        final String firstname = mFirstName.getText().toString();
        final String lastname = mLastName.getText().toString();
        final String email = mEmail.getText().toString();
        final String organization = mOrganization.getText().toString();
        final String address = mAddress.getText().toString();
        final String city = mCity.getText().toString();
        final String state = mState.getText().toString();
        final String zipcode = mZipcode.getText().toString();
        final String phone = mPhone.getText().toString();

        // Create a new label with all the information
        Map<String, Object> labelSignUp = new HashMap<>();
        labelSignUp.put(KEY_FIRSTNAME, firstname);
        labelSignUp.put(KEY_LASTNAME, lastname);
        labelSignUp.put(KEY_EMAIL, email);
        labelSignUp.put(KEY_ORGANIZATION, organization);
        labelSignUp.put(KEY_ADDRESS, address);
        labelSignUp.put(KEY_CITY, city);
        labelSignUp.put(KEY_STATE, state);
        labelSignUp.put(KEY_ZIPCODE, zipcode);
        labelSignUp.put(KEY_PHONE, phone);

        // Add a new document with a generated ID
        db.collection("Label Accounts")
                .add(labelSignUp);
    }
}
