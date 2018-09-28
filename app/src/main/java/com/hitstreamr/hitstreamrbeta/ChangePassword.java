package com.hitstreamr.hitstreamrbeta;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChangePassword";

    //EditText
    private EditText eCurrentpassword, eNewpassword, reenterpassword;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button changepasswordtBtn;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        eCurrentpassword = findViewById(R.id.currentPassword);
        eNewpassword = findViewById(R.id.NewPassword1);
        reenterpassword = findViewById(R.id.NewPassword2);

        //Button
        changepasswordtBtn = findViewById(R.id.savePassword);
        changepasswordtBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void changepwd(){

        final String email = user.getEmail();
        final String oldpwd = eCurrentpassword.getText().toString().trim();
        final String newpassword = eNewpassword.getText().toString().trim();
        final String repassword = reenterpassword.getText().toString().trim();

        if (email.contains("null")| !validateOldPassword(oldpwd) | !validatePassword(newpassword)| !validatePassword(newpassword, repassword)) {

            return;
        }
        else {

            //To get authentication credentials of current user
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldpwd);

            //final String usernewpassword = newpwd;

            //Validate and reauthenticate
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePassword.this, " Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                            finish();

                                        } else {
                                            Toast.makeText(ChangePassword.this, " Password Updated Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePassword.this, " Authentication Failed. Please enter your correct password.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }

    private boolean validateOldPassword(String password) {
        if (password.isEmpty()) {
            eCurrentpassword.setError("Field can't be empty");
            return false;
        } else {
            eCurrentpassword.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            eNewpassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            eNewpassword.setError("Password too weak");
            return false;
        } else {
            eNewpassword.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password, String repassword) {
        if (repassword.isEmpty()) {
            reenterpassword.setError("Field can't be empty");
            return false;
        } else if (!(repassword.equals(password))) {
            reenterpassword.setError("Re-enter Password is not same as New Password");
            return false;
        } else {
            reenterpassword.setError(null);
            return true;
        }
    }

    public void onClick(View view) {
        if(view == changepasswordtBtn ){
            changepwd();
        }
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

}
