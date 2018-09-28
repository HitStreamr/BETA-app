package com.hitstreamr.hitstreamrbeta.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

import java.util.Objects;
import java.util.regex.Pattern;

public class BasicSignUp extends AppCompatActivity implements View.OnClickListener {

    String st;

    private static final String TAG = "BasicSignUp";

    private Button signup, backbtn, uploadPhotoBtn;
    private EditText mEmailField, mPasswordField, mUsername;
    private TextView signintext;
    private RadioButton radiobtn;
    private ImageView mProfilePhoto;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    private String downloadimageUri;

    private Uri selectedImagePath;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageTask mstorageTask;
    private StorageReference imageRef = null;

    private static final int REQUEST_CODE = 123;

    User basicUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_sign_up);

        progressDialog = new ProgressDialog(this);

        mStorageRef = storage.getReference();

        // Views
        mUsername = findViewById(R.id.Username);
        mEmailField = findViewById(R.id.Email);
        mPasswordField = findViewById(R.id.Password);
        signintext = findViewById(R.id.textviewsignin);


        // Buttons
        signup = findViewById(R.id.signup_button);
        backbtn = findViewById(R.id.backBtn);
        radiobtn = findViewById(R.id.tos_button);
        uploadPhotoBtn = findViewById(R.id.basicUploadPhoto);

        //ImageView
        mProfilePhoto = findViewById(R.id.basicProfilePhoto);

        //Listeners
        mUsername.setOnClickListener(this);
        mEmailField.setOnClickListener(this);
        mPasswordField.setOnClickListener(this);
        signintext.setOnClickListener(this);
        signup.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        radiobtn.setOnClickListener(this);
        uploadPhotoBtn.setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        if (mAuth.getCurrentUser() != null) {
            //home activity here
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$", Pattern.CASE_INSENSITIVE);

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

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            mEmailField.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            mEmailField.setError("Email is not valid!");
            return false;
        } else {
            mEmailField.setError(null);
            return true;
        }
    }

    private boolean validateUsername(String artist) {
        if (artist.isEmpty()) {
            mUsername.setError("Field can't be empty");
            return false;
        } else if (!checkUsername(artist)) {
            mUsername.setError("Username cannot contain special characters and UpperCase letters.");
            return false;
        } else if (artist.length() <= 6) {
            mUsername.setError("Username is too short.");
            return false;
        } else {
            mUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            mPasswordField.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            mPasswordField.setError("Password too weak");
            return false;
        } else {
            mPasswordField.setError(null);
            return true;
        }
    }

    private boolean validateToc() {
        if (!radiobtn.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            radiobtn.setError(null);
            return true;
        }
    }

    private boolean validateBrowseVideo() {
        if (selectedImagePath != null) {
            return true;
        }
        uploadPhotoBtn.setText(R.string.video_not_selection);
        return false;
    }


    private void registerUser() {
        final String username = mUsername.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (!validateEmail(email) | !validatePassword(password) | !validateUsername(username)
                | !validateToc() | !validateBrowseVideo())  {
            return;
        }
        basicUser = new User(
                username,
                email
        );

        registerAuthentication(email, password);

        //If validations is ok we will first show progressbar
        progressDialog.setMessage("Registering New User...");
        progressDialog.show();
    }

    private void registerFirebase() {
        FirebaseDatabase.getInstance().getReference("BasicAccounts")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(basicUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(BasicSignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent genreIntent = new Intent(getApplicationContext(), PickGenre.class);
                    genreIntent.putExtra("TYPE", getString(R.string.type_basic));
                    startActivity(genreIntent);
                    /*Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    homeIntent.putExtra("TYPE", getString(R.string.type_basic));
                    startActivity(homeIntent);*/
                } else {
                    Toast.makeText(BasicSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerAuthentication(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadFromUri(selectedImagePath);
                        } else {
                            Toast.makeText(BasicSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void updateAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(basicUser.getUsername())
                    .setPhotoUri(Uri.parse(downloadimageUri))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                registerFirebase();
                                Log.e(TAG, "User profile updated.");
                            }
                            else{
                                Toast.makeText(BasicSignUp.this, "Could not update authentication, Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            // else block
            // ask to log in again(Invalid login)
        }
    }

    private void uploadFromUri(final Uri fileUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            imageRef = mStorageRef.child("profilePictures").child(user.getUid());
            mstorageTask = imageRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot takeSnapshot) {
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadimageUri = uri.toString();
                                            updateAuthentication();
                                            //registerFirebase();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BasicSignUp.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void chooseImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select your profile picture"), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    if (data.getData() != null) {
                        selectedImagePath = data.getData();
                        try {

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImagePath);
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            roundedBitmapDrawable.setCircular(true);
                            mProfilePhoto.setImageDrawable(roundedBitmapDrawable);
                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == signup) {
            if (radiobtn.isChecked()) {
                registerUser();
            } else {
                Toast.makeText(BasicSignUp.this, "Please agree to the Terms and Conditions.", Toast.LENGTH_SHORT).show();
            }
        }
        if (view == signintext) {
            //will open sign in activity
            finish();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
        if (view == backbtn) {
            //will open sign in activity
            finish();
            startActivity(new Intent(getApplicationContext(), AccountType.class));
        }

        if (view == uploadPhotoBtn) {
            chooseImage();
        }

    }
}
