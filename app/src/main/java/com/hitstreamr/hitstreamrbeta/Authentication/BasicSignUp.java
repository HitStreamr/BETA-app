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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;
import com.hitstreamr.hitstreamrbeta.UserTypes.UsernameUserIdPair;

import java.util.Objects;
import java.util.regex.Pattern;

public class BasicSignUp extends AppCompatActivity implements View.OnClickListener {

    String st;

    private static final String TAG = "BasicSignUp";

    private Button signup, uploadPhotoBtn;
    private Button backbtn;
    private EditText mEmailField, mPasswordField, mUsername, mFullName;
    private TextView signintext, passwordHint;
    private RadioButton radiobtn;
    private ImageView mProfilePhoto;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    DatabaseReference takenNames;

    private String downloadimageUri;

    private Uri selectedImagePath;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageTask mstorageTask;
    private StorageReference imageRef = null;

    private static final int REQUEST_CODE = 123;

    User basicUser;
    UsernameUserIdPair usernameUserIdPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_sign_up);

        progressDialog = new ProgressDialog(this);

        mStorageRef = storage.getReference();

        // Views
        mFullName = findViewById(R.id.fullName);
        mUsername = findViewById(R.id.Username);
        mEmailField = findViewById(R.id.Email);
        mPasswordField = findViewById(R.id.Password);
        signintext = findViewById(R.id.textviewsignin);
        passwordHint = findViewById(R.id.PasswordHint);

        // Buttons
        signup = findViewById(R.id.signup_button);
        backbtn = findViewById(R.id.backBtn);
        radiobtn = findViewById(R.id.tos_button);
        uploadPhotoBtn = findViewById(R.id.basicUploadPhoto);

        //ImageView
        mProfilePhoto = findViewById(R.id.basicProfilePhoto);

        //Listeners
        mFullName.setOnClickListener(this);
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

        takenNames  = FirebaseDatabase.getInstance().getReference("TakenUserNames");

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
                    "(?=.*[!@#$%^&*_+=])" +    //at least 1 special character
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

    //TODO clear old errors
    private void clearErrors(){};

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

    private void validateUserNameFirebase(User user, String password){
        final boolean[] isTaken = {false};
        takenNames.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user.getUsername()))
                {
                    //username exists
                    mUsername.setError("Username is already taken");
                }
                else if (!dataSnapshot.hasChild(user.getUsername()))
                {
                    mUsername.setError("null");
                    basicUser = user;

                    //If validations are ok we will first show progressbar
                    progressDialog.setMessage("Registering new Listener...");
                    progressDialog.show();

                    registerAuthentication(basicUser.getEmail(), password);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Connection Error. Please try again in some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            passwordHint.setVisibility(View.VISIBLE);
            mPasswordField.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordHint.setVisibility(View.VISIBLE);
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

    private boolean validateBrowsePhoto() {
        if (selectedImagePath != null) {
            return true;
        }
        Toast.makeText(this, "Please select your profile picture", Toast.LENGTH_SHORT).show();
        uploadPhotoBtn.setText(R.string.image_not_selection);
        return false;
    }

    private void registerUser() {
        final String fullName = mFullName.getText().toString().trim();
        final String username = mUsername.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        // TODO: validateFullName
        if (!validateEmail(email) | !validatePassword(password) | !validateUsername(username)
                | !validateToc() | !validateBrowsePhoto())  {
            return;
        }

        validateUserNameFirebase(new User(username, email, null, fullName, null, "false"), password);

    }

    private void registerFirebase2(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("UsernameUserId")
                .child(basicUser.getUsername())
                .setValue(usernameUserIdPair)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        registerFirebase();
                    }
                });
    }

    private void registerNotifcationTokens(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String notifToken = task.getResult().getToken();

                        FirebaseDatabase.getInstance().getReference("FirebaseNotificationTokens")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .child(notifToken)
                                .setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            //Upload Prof Image
                                            uploadFromUri(selectedImagePath);
                                        }
                                    }
                                });
                    }
                });
    }

    private void registerFirebase() {
        //TODO make sure that newID is not being made when not needed
        FirebaseDatabase.getInstance().getReference("BasicAccounts")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(basicUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(BasicSignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    takenNames.child(basicUser.getUsername()).setValue(true);
                    finish();
                    //TODO: Do something with genres
                    Intent genreIntent = new Intent(getApplicationContext(), PickGenre.class);
                    genreIntent.putExtra("TYPE", getString(R.string.type_basic));
                    startActivity(genreIntent);
                } else {
                    Toast.makeText(BasicSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerAuthentication(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Checks if the user has successfully completed the Firebase Authentication
        if (user == null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                registerNotifcationTokens();
                            } else {
                                Toast.makeText(BasicSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            //failed on step after authentication
            registerNotifcationTokens();
        }

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
                                usernameUserIdPair = new UsernameUserIdPair(basicUser.getUsername(), user.getUid());
                                registerFirebase2();
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
        if (user != null && fileUri != null) {
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
        }else if (fileUri == null){
            Toast.makeText(BasicSignUp.this, "No image selected.", Toast.LENGTH_SHORT).show();
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
