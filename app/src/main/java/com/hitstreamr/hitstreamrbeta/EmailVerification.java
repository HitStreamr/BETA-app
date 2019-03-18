package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hitstreamr.hitstreamrbeta.Authentication.PickGenre;

public class EmailVerification extends AppCompatActivity {

    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Get the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Set the text using the current user's email address
        TextView verificationText = findViewById(R.id.verificationText);
        String verification = "A Verification Email has been sent to " + current_user.getEmail();
        verificationText.setText(verification);

        // Check if the current user's email address is verified
        if (current_user.isEmailVerified()) {
            Intent selectGenrePage = new Intent(getApplicationContext(), PickGenre.class);
            selectGenrePage.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
            startActivity(selectGenrePage);
        }
    }

    /**
     * RESEND BUTTON
     * Resend the verification email if the resend button is clicked.
     * @param view view
     */
    public void resendVerificationEmail(View view) {
        current_user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Verification email has been resent to " +
                                    current_user.getEmail() + ". Please check your inbox/spam folder.",
                            Toast.LENGTH_LONG);

                    // Centering the text
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    if (toastLayout.getChildCount() > 0) {
                        TextView textView = (TextView) toastLayout.getChildAt(0);
                        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    }

                    toast.show();
                }
            }
        });
    }

    /**
     * CLICK HERE BUTTON
     * Go to the next page if email is already verified and the page does not do it automatically.
     * @param view view
     */
    public void haveVerified(View view) {
        // Reload the current user's account information
        current_user.reload();

        // Check if the user's email is verified once they reopen the app
        if (current_user.isEmailVerified()) {
            Intent selectGenrePage = new Intent(getApplicationContext(), PickGenre.class);
            selectGenrePage.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
            startActivity(selectGenrePage);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Oops! Our system shows that your email has not been verified.", Toast.LENGTH_LONG);

            // Centering the text
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            if (toastLayout.getChildCount() > 0) {
                TextView textView = (TextView) toastLayout.getChildAt(0);
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            }

            toast.show();
        }
    }

    /**
     * Reload the current user's account information and immediately check if their email has been verified.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Reload the current user's account information
        current_user.reload();

        // Check if the user's email is verified once they reopen the app
        if (current_user.isEmailVerified()) {
            Intent selectGenrePage = new Intent(getApplicationContext(), PickGenre.class);
            selectGenrePage.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
            startActivity(selectGenrePage);
        }
    }
}