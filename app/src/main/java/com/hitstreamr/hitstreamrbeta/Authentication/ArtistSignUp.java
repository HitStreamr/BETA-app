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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;
import com.hitstreamr.hitstreamrbeta.UserTypes.UsernameUserIdPair;

import java.util.regex.Pattern;

public class ArtistSignUp extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ArtistSignUp";

    //Artist User object
    ArtistUser artist_object;

    UsernameUserIdPair usernameUserIdPair;

    // Inputs
    private EditText mFirstName, mLastName, mEmail, mPassword, mUsername, mAddress, mCity, mZipcode, mPhone;
    private Spinner mState, mCountry;
    // Add address line 1 and 2?

    // Buttons
    private Button signup, profilePictureBtn;
    private RadioButton termsCond;

    private ImageButton goBack;

    //ImageView
    private ImageView imageViewProfile;

    private String downloadimageUri;

    private Uri selectedImagePath = null;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageTask mstorageTask;
    private StorageReference imageRef = null;

    private static final int REQUEST_CODE = 123;

    private ProgressDialog progressDialog;

    // Access a Cloud Firebase instance from your Activity
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference takenNames;

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

    //Regex pattern for email.
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$", Pattern.CASE_INSENSITIVE);
    //Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_PHONE_NUMBER_REGEX =
            Pattern.compile(("[0-9]{10}"));
    //Pattern.compile(("\\d{3}-\\d{3}-\\d{4}"));

    private static final Pattern VALID_ZIP_REGEX =
            Pattern.compile(("[0-9]{5}"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_sign_up);
        progressDialog = new ProgressDialog(this);

        mStorageRef = storage.getReference();

        // Views
        mFirstName = findViewById(R.id.artistFirstName);
        mLastName = findViewById(R.id.artistLastName);
        mEmail = findViewById(R.id.artistEmail);
        mPassword = findViewById(R.id.artistPassword);
        mUsername = findViewById(R.id.artistUsername);
        mAddress = findViewById(R.id.artistAddressLine1);
        mCity = findViewById(R.id.artistCity);
        mState = findViewById(R.id.artistState);
        mZipcode = findViewById(R.id.artistZip);
        mCountry = findViewById(R.id.artistCountry);
        mPhone = findViewById(R.id.artistPhone);

        // Buttons
        signup = findViewById(R.id.signup_button);
        goBack = findViewById(R.id.backBtn);
        termsCond = findViewById(R.id.tos_button);
        profilePictureBtn = findViewById(R.id.artistPicture);

        //Image View
        imageViewProfile = findViewById(R.id.artistProfilePicture);

        // Listeners
        signup.setOnClickListener(this);
        //goBack.setOnClickListener(this);
        profilePictureBtn.setOnClickListener(this);


       takenNames  = FirebaseDatabase.getInstance().getReference("TakenUserNames");

        Log.e(TAG, "URI value:" +selectedImagePath);
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
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ArtistSignUp.this, "Image Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Read the inputs from the user, check if inputs are valid, then add to the database.
     */
    private void registerArtist() {
        final String firstname = mFirstName.getText().toString().trim();
        final String lastname = mLastName.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        final String username = mUsername.getText().toString().trim();
        final String address = mAddress.getText().toString().trim();
        final String city = mCity.getText().toString().trim();
        final String state = mState.getSelectedItem().toString();
        final String zip = mZipcode.getText().toString().trim();
        final String country = mCountry.getSelectedItem().toString();
        final String phone = mPhone.getText().toString().trim();


        if (!validateFirstName(firstname) | !validateLastName(lastname) | !validateEmail(email) | !validatePassword(password)
                | !validateAddressLine(address) | !validateCity(city) | !validateUsername(username)| !validatePhone(phone)
                | !validateZip(zip) | !validateToc() | !validateState() | !validateCountry() | !validateProfilePicture()) {
            Log.e(TAG, "Validation failed");
            Toast.makeText(getApplicationContext(), "Please fix indicated errors.", Toast.LENGTH_LONG).show();
            return;
        }

        validateUserNameFirebase(new ArtistUser(firstname, lastname, email, username, address, city, state, country, phone, zip),password);
    }

    private void registerAuthentication(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Checks if the user has successfully completed the Firebase Authentication
        if(user == null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                uploadFromUri(selectedImagePath);
                            } else {
                                Toast.makeText(ArtistSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            //failed on step after authentication
            uploadFromUri(selectedImagePath);
        }
    }

    private void updateAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(artist_object.getUsername())
                    .setPhotoUri(Uri.parse(downloadimageUri))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                usernameUserIdPair = new UsernameUserIdPair(artist_object.getUsername(), user.getUid());
                                registerFirebase2();
                                Log.d(TAG, "User profile updated.");
                            }else {
                                Toast.makeText(ArtistSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void registerFirebase() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            FirebaseDatabase.getInstance().getReference("ArtistAccounts")
                    .child(user.getUid())
                    .setValue(artist_object)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                takenNames.child(artist_object.getUsername()).setValue(true);
                                Toast.makeText(ArtistSignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                //start next activity
                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                mainIntent.putExtra("TYPE", getString(R.string.type_artist));
                                startActivity(mainIntent);
                            } else {
                                //Display a failure message
                                Toast.makeText(ArtistSignUp.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }
        // else block
        // ask to log in again(Invalid login)
    }

    private void registerFirebase2(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("UsernameUserId")
                .child(artist_object.getUsername())
                .setValue(usernameUserIdPair)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        registerFirebase();
                    }
                });
    }


    /**
     * Check if first name input is valid.
     *
     * @param firstname first name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateFirstName(String firstname) {
        if (firstname.isEmpty()) {
            mFirstName.setError("Field can't be empty");
            return false;
        } else if (firstname.length() >= 26) {
            mFirstName.setError("First name can only have 26 characters");
            return false;
        } else if (!(checkAlphabet(firstname))) {
            mFirstName.setError("First name must only have letters");
            return false;
        }
         else {
            mFirstName.setError(null);
            return true;
        }
    }

    /**
     * Check if last name input is valid.
     *
     * @param lastname last name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateLastName(String lastname) {
        if (lastname.isEmpty()) {
            mLastName.setError("Field can't be empty");
            return false;
        } else if (lastname.length() >= 26) {
            mLastName.setError("Last name can only have 26 characters");
            return false;
        } else if (!(checkAlphabet(lastname))) {
            mLastName.setError("Last name must only have letters");
            return false;
        } else {
            mLastName.setError(null);
            return true;
        }
    }

    /**
     * Check if address input is valid.
     *
     * @param address address line
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateAddressLine(String address) {
        if (address.isEmpty()) {
            mAddress.setError("Field can't be empty");
            return false;
        } else if (!checkAlphaNumeric(address)) {
            mAddress.setError("Address is not valid.");
            return false;
        } else {
            mAddress.setError(null);
            return true;
        }
    }

    private boolean validateState() {
        if (mState.getSelectedItem().toString().trim().equals("Select State")) {
            Toast.makeText(this, "State is not selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateCountry() {
        if (mCountry.getSelectedItem().toString().trim().equals("Select Country")) {
            Toast.makeText(this, "Country is not selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Check if email input is valid.
     *
     * @param email email
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            mEmail.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            mEmail.setError("Email is not valid!");
            return false;
        } else {
            mEmail.setError(null);
            return true;
        }
    }

    /**
     * Check if phone number input is valid.
     *
     * @param phone phone number
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validatePhone(String phone) {
        if (phone.isEmpty()) {
            mPhone.setError("Field can't be empty");
            return false;
        } else if (!VALID_PHONE_NUMBER_REGEX.matcher(phone).matches()) {
            mPhone.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return false;
        } else {
            mPhone.setError(null);
            return true;
        }
    }

    /**
     * Check if city input is valid.
     *
     * @param city city
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateCity(String city) {
        if (city.isEmpty()) {
            mCity.setError("Field can't be empty");
            return false;
        } else {
            mCity.setError(null);
            return true;
        }
    }

    /**
     * Check if zip input is valid.
     *
     * @param zip zip
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateZip(String zip) {
        if (zip.isEmpty()) {
            mZipcode.setError("Field can't be empty");
            return false;
        } else if (!VALID_ZIP_REGEX.matcher(zip).matches()) {
            mZipcode.setError("Zip is invalid");
            return false;
        } else {
            mZipcode.setError(null);
            return true;
        }
    }

    /**
     * Check if password input is valid.
     *
     * @param password password
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            mPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            mPassword.setError("Password too weak");
            return false;
        } else {
            mPassword.setError(null);
            return true;
        }
    }

    /**
     * Check if artist name input is valid.
     *
     * @param artist artist name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateUsername(String artist) {
        if (artist.isEmpty()) {
            mUsername.setError("Field can't be empty");
            return false;
        } else if (!checkUsername(artist)) {
            mAddress.setError("Username cannot contain special characters.");
            return false;
        } else if (artist.length() <= 6) {
            mAddress.setError("Username is too short.");
            return false;
        } else {
            mUsername.setError(null);
            return true;
        }
    }

    private void validateUserNameFirebase(ArtistUser artist, String password){
        final boolean[] isTaken = {false};
        takenNames.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(artist.getUsername()))
                {
                    //username exists
                    mUsername.setError("Username is already taken");
                }
                else if (!dataSnapshot.hasChild(artist.getUsername()))
                {
                    mUsername.setError("null");
                    artist_object = artist;

                    //If validations are ok we will first show progressbar
                    progressDialog.setMessage("Registering new Artist...");
                    progressDialog.show();

                    registerAuthentication(artist_object.getEmail(), password);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Connection Error. Please try again in some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateProfilePicture() {
        if (selectedImagePath != null) {
            return true;
        }
        else{
            profilePictureBtn.setText(R.string.image_not_selection);
            Toast.makeText(this, "Please select your profile picture", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // not working yet
    // private boolean validateState(String state) {
    //     if (!state.equals("Select a State")) {
    //         Toast.makeText(this, "Please select a state.", Toast.LENGTH_SHORT).show();
    //         return false;
    //     } else {
    //         return true;
    //     }
    // }

    /**
     * Check if the user agrees to Terms and Conditions
     *
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateToc() {
        if (!termsCond.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            termsCond.setError(null);
            return true;
        }
    }

    /**
     * Method to validate whether the input string entered contains only
     * Alphabetical characters.
     * <p>
     * 1. This method also helps in making sure to handle the edge case i.e space.
     */
    private boolean checkAlphabet(String s) {

        String Alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        boolean[] value_for_each_comparison = new boolean[s.length()];

        for (int i = 0; i < s.length(); i++) {
            for (int count = 0; count < Alphabets.length(); count++) {
                if (s.charAt(i) == Alphabets.charAt(count)) {
                    value_for_each_comparison[i] = true;
                    break;
                } else {
                    value_for_each_comparison[i] = false;
                }
            }
        }

        return checkStringCmpvalues(value_for_each_comparison);
    }

    /**
     * Method to support the above Method named checkAlphabet in accomplishing the task
     * to validate the input String for Alphabetical characters only.
     */
    private boolean checkStringCmpvalues(boolean[] boolarray) {
        boolean flag = false;
        for (boolean aBoolarray : boolarray) {
            if (aBoolarray)
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
    public boolean checkAlphaNumeric(String s) {

        String AlphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
        boolean[] value_for_each_comparison = new boolean[s.length()];

        for (int i = 0; i < s.length(); i++) {
            for (int count = 0; count < AlphaNumeric.length(); count++) {
                if (s.charAt(i) == AlphaNumeric.charAt(count)) {
                    value_for_each_comparison[i] = true;
                    break;
                } else {
                    value_for_each_comparison[i] = false;
                }
            }
        }
        return checkStringCmpvalues(value_for_each_comparison);
    }

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
                            imageViewProfile.setImageDrawable(roundedBitmapDrawable);
                            //profilePictureBtn.setText(R.string.image_selection);
                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    }/* else {
                        //profilePictureBtn.setBackgroundColor(Color.RED);
                        profilePictureBtn.setText(R.string.image_not_selection);
                    }*/
                }
            }
        }
    }

    /**
     * Override the onClick function
     *
     * @param view view
     */
    @Override
    public void onClick(View view) {
        if (view == signup) {
            registerArtist();
        }
        if (view == goBack) {
            //will open account type activity
            finish();
            startActivity(new Intent(getApplicationContext(), AccountType.class));
        }
        if (view == profilePictureBtn) {
            chooseImage();
        }
    }
}