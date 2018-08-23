package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ArtistSignUp extends AppCompatActivity {

  /*  private static final String TAG = "ArtistSignUp";

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
    private EditText editTextCity;
    private EditText editTextState;
    private EditText editTextZip;
    private EditText editTextUsername;
    private EditText editTextPhone;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);

        editTextEmail = findViewById(R.id.Email);
        editTextPasssword = findViewById(R.id.Password);
        editTextFirstname = findViewById(R.id.FirstName);
        editTextLastname = findViewById(R.id.LastName);
        editTextAddress = findViewById(R.id.AddressLine1);
        editTextCity = findViewById(R.id.City);
        editTextState = findViewById(R.id.State);
        //editTextZip = findViewById(R.id.Zip);
        editTextUsername = findViewById(R.id.Username);
        editTextPhone = findViewById(R.id.Phone);

            String username = editTextUsername.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPasssword.getText().toString();
            String firstname = editTextFirstname.getText().toString();
            String lastname = editTextLastname.getText().toString();
            String address = editTextAddress.getText().toString();
            String state = editTextState.getText().toString();
            String city = editTextCity.getText().toString();
            //String zip = editTextZip.getText().toString();
            String phone = editTextPhone.getText().toString();

            Map<String, Object> ArtistSignUp = new HashMap<>();
            saveArtistSignUp.put(KEY_USERNAME, username);
            saveArtistSignUp.put(KEY_EMAIL, email);
            saveArtistSignUp.put(KEY_PASSWORD, password);
            saveArtistSignUp.put(KEY_fIRSTNAME, firstname);
            saveArtistSignUp.put(KEY_LASTNAME, lastname);
            saveArtistSignUp.put(KEY_ADDRESS, address);
            saveArtistSignUp.put(KEY_CITY, city);
            saveArtistSignUp.put(KEY_STATE, state);
            //saveArtistSignUp.put(KEY_ZIP, zip);
            saveArtistSignUp.put(KEY_PHONE, phone);

            db.collection("Accounts").document("ArtistAccount").set(ArtistSignUp)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Artist sign up saved", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error saving Artist sign up", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });



        }
    }*/
}
