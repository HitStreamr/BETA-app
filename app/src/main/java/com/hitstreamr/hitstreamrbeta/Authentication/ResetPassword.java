package com.hitstreamr.hitstreamrbeta.Authentication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.hitstreamr.hitstreamrbeta.R;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {

    final String TAG = "RESET_PASSWORD";

    EditText resetEmail;
    Button enterEmail;
    FirebaseAuth auth;

    //Regex pattern for email.
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //set the views
        enterEmail = (Button)findViewById(R.id.confirmResetEmail);
        resetEmail = findViewById(R.id.ETResetEmail);

        auth = FirebaseAuth.getInstance();

        enterEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmail(resetEmail.getText().toString())){

                    String emailAddress = resetEmail.getText().toString() ;

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        setResult(RESULT_OK);
                                        finish();
                                    }else{
                                        Log.d(TAG, "Email failed.");
                                        setResult(RESULT_CANCELED);
                                        finish();
                                    }
                                }
                            });

                }
            }
        });

    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            resetEmail.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            resetEmail.setError("Email is not valid!");
            return false;
        } else {
            resetEmail.setError(null);
            return true;
        }
    }
}
