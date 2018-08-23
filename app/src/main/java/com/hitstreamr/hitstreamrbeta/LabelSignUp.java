package com.hitstreamr.hitstreamrbeta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LabelSignUp extends AppCompatActivity implements View.OnClickListener {

    // Inputs
    private EditText mFirstName, mLastName, mEmail, mPassword, mLabel, mAddress, mCity, mZipcode, mPhone;
    private Spinner mState, mCountry;
    // Add address line 1 and 2?

    // Buttons
    private Button signup, goBack;
    private RadioButton termsCond; // take this out

    private ProgressDialog progressDialog;

    // Access a Cloud Firebase instance from your Activity
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        termsCond = findViewById(R.id.tos_button); // take this out

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
        final String label = mLabel.getText().toString();
        final String address = mAddress.getText().toString();
        final String city = mCity.getText().toString().trim();
        final String state = mState.getSelectedItem().toString();
        final String zipcode = mZipcode.getText().toString().trim();
        final String country = mCountry.getSelectedItem().toString();
        final String phone = mPhone.getText().toString().trim();

        if (checkCredentials(firstname, lastname, email, password, label, address, city, state, zipcode,
                country, phone)) {

            //If validations is ok we will first show progressbar
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

                                FirebaseDatabase.getInstance().getReference("Label Accounts")
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
                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
    }

    /**
     * Check for inputs validations.
     * @return true if ALL pass, otherwise false
     */
    private Boolean checkCredentials(String firstname, String lastname, String email, String password,
                                     String label, String address, String city, String state, String zipcode,
                                     String country, String phone) {

        if(TextUtils.isEmpty(firstname)) {
            // First name is empty
            Toast.makeText(this, "Please enter your first name.", Toast.LENGTH_SHORT).show();
            // Stop the function execution further
            return false;
        }

        if(TextUtils.isEmpty(lastname)) {
            // Last name is empty
            Toast.makeText(this, "Please enter your last name.", Toast.LENGTH_SHORT).show();
            // Stop the function execution further
            return false;
        }

        // If ALL is well
        return true;
    }

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
