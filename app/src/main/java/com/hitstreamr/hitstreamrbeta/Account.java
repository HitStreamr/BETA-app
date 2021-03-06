package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

import java.util.Objects;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AccountActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;
    FirebaseUser user;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageTask mstorageTask;
    private StorageReference imageRef = null;
    private StorageReference backgroundRef = null;

    private String downloadimageUri;
    private String downloadBackgroundUri;

    private boolean photoChanged = false;
    private boolean backgroundChanged = false;

    private String email;
    private String verified;

    //Account Type
    private String type;

    private LinearLayout ArtistInfoLayout;

    //EditText
    private TextView EditTextUsername;
    private TextView EditTextEmail;
    private EditText EditTextBio;
    private EditText EditTextFirstName;
    private EditText EditTextLastName;
    private EditText EditTextName;
    private EditText EditTextAddress;
    private EditText EditTextCity;
    private EditText EditTextZip;
    private EditText EditTextPhone;
    private EditText EditTextCountry;
    private TextView UserNameText;
    private TextView LabelNameText;
    private TextView NameText;
    private CircleImageView circleImageView;

    //Spinner
    private Spinner SpinnerState;

    //ImageView
    private ImageView ImageViewPhoto;

    //Buttons
    private Button SaveAccountBtn;
    private Button ChangePwdBtn;
    private Button ChangePhotoBtn;
    private Button ChangeBackgroundBtn;
    private Button DeleteMyAccount;

    //Object
    ArtistUser artist;
    ArtistUser artist_object;
    User basicUser;
    User oldBasic;

    private boolean selectPhoto = false;
    private boolean selectBackground = false;

    private Uri selectedImagePath;
    private Uri selectedBackgroundPath;

    private static final int REQUEST_CODE = 123;

    //Regex pattern for email.
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_PHONE_NUMBER_REGEX =
            Pattern.compile(("[0-9]{10}"));

    private static final Pattern VALID_ZIP_REGEX =
            Pattern.compile(("[0-9]{5}"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        mStorageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        user = mAuth.getCurrentUser();
        userID = user.getUid();

        //Layout
        LinearLayout artistInfoLayout = findViewById(R.id.privateInfo);

        //EditText
        EditTextUsername = findViewById(R.id.accountUsername);
        EditTextEmail = findViewById(R.id.accountEmail);
        EditTextName = findViewById(R.id.name);
        EditTextBio = findViewById(R.id.Bio);
        EditTextFirstName = findViewById(R.id.accountFirstName);
        EditTextLastName = findViewById(R.id.accountLastName);
        EditTextAddress = findViewById(R.id.accountAddress);
        EditTextCity = findViewById(R.id.accountCity);
        EditTextZip = findViewById(R.id.accountZip);
        EditTextPhone = findViewById(R.id.accountPhone);
        EditTextCountry = findViewById(R.id.accountCountry);
        UserNameText = findViewById(R.id.UserNametext);
        LabelNameText = findViewById(R.id.Labeltext);
        NameText = findViewById(R.id.nameTitle);

        //Spinners
        SpinnerState = findViewById(R.id.accountState);

        //ImageView
        ImageViewPhoto = findViewById(R.id.accountPhoto);

        //Button
        SaveAccountBtn = findViewById(R.id.update_account);
        SaveAccountBtn.setOnClickListener(this);
        ChangePhotoBtn = findViewById(R.id.accountChangePhoto);
        ChangeBackgroundBtn = findViewById(R.id.accountChangeBackground);
        DeleteMyAccount = findViewById(R.id.deleteAccount);

        ChangePwdBtn = findViewById(R.id.ChangePassword);
        ChangePwdBtn.setOnClickListener(this);
        ChangePhotoBtn.setOnClickListener(this);
        ChangeBackgroundBtn.setOnClickListener(this);
        DeleteMyAccount.setOnClickListener(this);

        type = getIntent().getStringExtra("TYPE");
        if (type.equals(getString(R.string.type_artist))) {

            artistInfoLayout.setVisibility(View.VISIBLE);

            if (userID != null) {
                myRef = database.getReference("ArtistAccounts").child(userID);
            }
        } else if (type.equals(getString(R.string.type_basic))) {
            artistInfoLayout.setVisibility(View.GONE);
            if (userID != null) {
                myRef = database.getReference("BasicAccounts").child(userID);
            }
        } else if (type.equals(getString(R.string.type_label))) {
            UserNameText.setVisibility(View.GONE);
            NameText.setVisibility(View.GONE);
            LabelNameText.setVisibility(View.VISIBLE);
            ArtistInfoLayout.setVisibility(View.VISIBLE);
            EditTextUsername.setEnabled(false);
            EditTextUsername.setFocusable(false);
            EditTextEmail.setEnabled(false);
            EditTextEmail.setFocusable(false);

            if (userID != null) {
                myRef = database.getReference("LabelAccounts").child(userID);
            }
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (type.equals(getString(R.string.type_artist))) {
                    showArtistData(dataSnapshot);

                } else if (type.equals(getString(R.string.type_basic))) {
                    showBasicData(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*Profile Picture
        if (user.getPhotoUrl() != null) {
            circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }*/

        // Prevent keyboard from showing automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void showArtistData(DataSnapshot dataSnapshot) {

        String firstname = dataSnapshot.child("firstname").getValue(String.class);
        String lastname = dataSnapshot.child("lastname").getValue(String.class);
        String artistname = dataSnapshot.child("artistname").getValue(String.class);
        String email = dataSnapshot.child("email").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);
        String address = dataSnapshot.child("address").getValue(String.class);
        String city = dataSnapshot.child("city").getValue(String.class);
        String state = dataSnapshot.child("state").getValue(String.class);
        String zip = dataSnapshot.child("zip").getValue(String.class);
        String country = dataSnapshot.child("country").getValue(String.class);
        String phone = dataSnapshot.child("phone").getValue(String.class);
        verified = dataSnapshot.child("verified").getValue(String.class);

        String bio = "";
        if (dataSnapshot.child("bio").exists()) {
            bio = dataSnapshot.child("bio").getValue(String.class);
        }

        artist = new ArtistUser(firstname, lastname, artistname, email, username, address, city, state,
                country, phone, zip, bio, userID, verified);

        EditTextFirstName.setText(artist.getFirstname());
        EditTextLastName.setText(artist.getLastname());
        EditTextName.setText(artist.getArtistname());
        EditTextEmail.setText(artist.getEmail());
        EditTextUsername.setText(artist.getUsername());
        EditTextAddress.setText(artist.getAddress());
        EditTextCity.setText(artist.getCity());
        EditTextZip.setText(artist.getZip());
        EditTextPhone.setText(artist.getPhone());
        EditTextCountry.setText(artist.getCountry());
        EditTextBio.setText(artist.getBio());
        SpinnerState.setSelection(getIndex(SpinnerState, artist.getState()));

        Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(ImageViewPhoto);

        getSupportActionBar().setTitle(username);
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    public void showBasicData(DataSnapshot dataSnapshot) {

        email = dataSnapshot.child("email").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);
        String fullname = dataSnapshot.child("fullname").getValue(String.class);
        verified = dataSnapshot.child("verified").getValue(String.class);

        String bio = "";
        if (dataSnapshot.child("bio").exists()) {
            bio = dataSnapshot.child("bio").getValue(String.class);
        }

        oldBasic = new User(username, email, userID, fullname, bio, verified);

        EditTextEmail.setText(oldBasic.getEmail());
        EditTextUsername.setText(oldBasic.getUsername());
        EditTextName.setText(oldBasic.getFullname());
        EditTextBio.setText(oldBasic.getBio());

        Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(ImageViewPhoto);

        getSupportActionBar().setTitle(username);
    }

    private void registerUser() {
        final String username = EditTextUsername.getText().toString().trim();
        final String email = EditTextEmail.getText().toString().trim();
        final String name = EditTextName.getText().toString().trim();
        final String bio = EditTextBio.getText().toString().trim();

        // TODO: validate name
        if (!validateEmail(email) | !validateUsername(username) | !validateBio(bio)) {
            return;
        }
        /*//If validations is ok we will first show progressbar
        progressDialog.show();
        progressDialog.setMessage("Registering New User...");*/

        //make sure the basic user has the write id
        basicUser = new User(username, email, oldBasic.getUserID(), name, bio, verified);
        oldBasic = basicUser;

        if(selectedBackgroundPath!= null){
            uploadBackgroundImage(selectedBackgroundPath);
        }

        if(selectedBackgroundPath!= null){
            uploadBackgroundImage(selectedBackgroundPath);
        }

        if(selectedBackgroundPath!= null){
            uploadBackgroundImage(selectedBackgroundPath);
        }

        if (selectedImagePath != null) {
            uploadFromUri(selectedImagePath);
        }
        else {
            downloadimageUri = user.getPhotoUrl().toString();
            updateAuthentication();
        }
    }

    private void registerBasicFirebase(){
        FirebaseDatabase.getInstance().getReference("BasicAccounts")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(basicUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Account.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Account.this, "Could not update. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void registerArtist() {
        Log.e(TAG, "Entered register artist method" + type);

        final String firstname = EditTextFirstName.getText().toString().trim();
        Log.e(TAG, "Entered register artist firstname:::::" + firstname);
        final String lastname = EditTextLastName.getText().toString().trim();
        email = EditTextEmail.getText().toString().trim();
        final String artistname = EditTextName.getText().toString().trim();
        final String username = EditTextUsername.getText().toString().trim();
        final String address = EditTextAddress.getText().toString().trim();
        final String city = EditTextCity.getText().toString().trim();
        final String state = SpinnerState.getSelectedItem().toString();
        final String zip = EditTextZip.getText().toString().trim();
        final String country = EditTextCountry.getText().toString().trim();
        final String phone = EditTextPhone.getText().toString().trim();
        final String bio = EditTextBio.getText().toString().trim();

        if (!validateFirstName(firstname) | !validateLastName(lastname) | !validateArtistName(artistname)
                | !validateEmail(email) | !validateAddressLine(address) | !validateCity(city)
                | !validateUsername(username) | !validatePhone(phone) | !validateZip(zip)
                | !validateBio(bio)) {

            Log.e(TAG, "register Artist no validation" + type);
            return;
        }
        artist_object = new ArtistUser(firstname, lastname, artistname, email, username, address, city,
                state, country, phone, zip, bio, userID, verified);

        if(selectedBackgroundPath!= null){
            uploadBackgroundImage(selectedBackgroundPath);
        }

        if(selectedBackgroundPath!= null){
            uploadBackgroundImage(selectedBackgroundPath);
        }

        if(selectedBackgroundPath!= null){
            uploadBackgroundImage(selectedBackgroundPath);
        }

        videoSelection();

        Log.e(TAG, "email details" + artist.getEmail() + "new email" + email);
        Log.e(TAG, "email details" + user.getEmail() + "profile photo: " + user.getEmail());

        //updateProfile();
    }

    private void videoSelection(){

        if (selectedImagePath != null) {
            uploadFromUri(selectedImagePath);
        }
        else {
            downloadimageUri = user.getPhotoUrl().toString();
            updateAuthentication();
        }

    }

    private void updateAuthentication() {
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateProfile();
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });
    }

    private void updateProfile() {
        if (user != null) {
            UserProfileChangeRequest profileUpdates = null;
            if (type.equals(getString(R.string.type_artist))) {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(artist_object.getUsername())
                        .setPhotoUri(Uri.parse(downloadimageUri))
                        .build();
            }
            else if (type.equals(getString(R.string.type_basic))) {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(basicUser.getUsername())
                        .setPhotoUri(Uri.parse(downloadimageUri))
                        .build();
            }

            if (profileUpdates != null) {
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (type.equals(getString(R.string.type_artist))) {
                                        registerArtistFirebase();
                                    } else if (type.equals(getString(R.string.type_basic))) {
                                        registerBasicFirebase();
                                    }
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
            }
            // else block
            // ask to log in again(Invalid login)
        }
    }

    private void registerArtistFirebase() {
        Log.e(TAG, "register Artist came out of validation" + type);

        Log.e(TAG, " new photo details:" + user.getPhotoUrl() + " new profile photo: " + user.getEmail());

        FirebaseDatabase.getInstance().getReference("ArtistAccounts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(artist_object)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Account.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Account.this, "Update Failed, Please try again", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadFromUri(final Uri fileUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.e(TAG, "Uri selected" +fileUri);
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
                            Toast.makeText(Account.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void uploadBackgroundImage (final Uri fileUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.e(TAG, "Background Uri selected" +fileUri);
            backgroundRef = mStorageRef.child("backgroundPictures").child(user.getUid());
            mstorageTask = backgroundRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot takeSnapshot) {
                            backgroundRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadBackgroundUri = uri.toString();
                                            Log.e(TAG, "Download url"+downloadBackgroundUri);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Account.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    //On click change password option
    private void selectChangePassword() {

        Intent newPasswordIntent = new Intent(getApplicationContext(), ChangePassword.class);
        newPasswordIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        startActivity(newPasswordIntent);

    }

    /**
     * Check if first name input is valid.
     *
     * @param firstname first name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateFirstName(String firstname) {
        if (firstname.isEmpty()) {
            EditTextFirstName.setError("Field can't be empty");
            return false;
        } else if (firstname.length() <= 26) {
            if (!(checkAlphabet(firstname))) {
                EditTextFirstName.setError("First name must only have letters");
                return false;
            }
            return true;
        } else {
            EditTextFirstName.setError(null);
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
            EditTextLastName.setError("Field can't be empty");
            return false;
        } else if (lastname.length() <= 26) {
            if (!(checkAlphabet(lastname))) {
                EditTextLastName.setError("Last name must only have letters");
                return false;
            }
            return true;
        } else {
            EditTextLastName.setError(null);
            return true;
        }
    }

    /**
     * Check if last name input is valid.
     *
     * @param artistname artist name
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateArtistName(String artistname) {
        if (artistname.isEmpty()) {
            EditTextName.setError("Field can't be empty");
            return false;
        } else if (artistname.length() <= 30) {
            if (!(checkAlphaNumericSymbol(artistname))) {
                EditTextName.setError("Name cannot exceed 30 Characters");
                return false;
            }
            return true;
        } else {
            EditTextName.setError(null);
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
            EditTextAddress.setError("Field can't be empty");
            return false;
        } else if (!checkAlphaNumeric(address)) {
            EditTextAddress.setError("Address is not valid.");
            return false;
        } else {
            EditTextAddress.setError(null);
            return true;
        }
    }

    /**
     * Check if email input is valid.
     *
     * @param email email
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            EditTextEmail.setError("Field can't be empty");
            return false;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            EditTextEmail.setError("Email is not valid!");
            return false;
        } else {
            EditTextEmail.setError(null);
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
            EditTextPhone.setError("Field can't be empty");
            return false;
        } else if (!VALID_PHONE_NUMBER_REGEX.matcher(phone).matches()) {
            EditTextPhone.setError("Phone Number must be in the form XXX-XXX-XXXX");
            return false;
        } else {
            EditTextPhone.setError(null);
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
            EditTextCity.setError("Field can't be empty");
            return false;
        } else {
            EditTextCity.setError(null);
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
            EditTextZip.setError("Field can't be empty");
            return false;
        } else if (!VALID_ZIP_REGEX.matcher(zip).matches()) {
            EditTextZip.setError("Zip is invalid");
            return false;
        } else {
            EditTextZip.setError(null);
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
            EditTextUsername.setError("Field can't be empty");
            return false;
        } else if (!checkUsername(artist)) {
            EditTextUsername.setError("Username cannot contain special characters.");
            return false;
        } else if (artist.length() <= 6) {
            EditTextUsername.setError("Username is too short.");
            return false;
        } else {
            EditTextUsername.setError(null);
            return true;
        }
    }

    /**
     * Only checks when bio is not empty if it is valid.
     *
     * @param bio profile's bio
     * @return true if valid, otherwise false and display an error message
     */
    private boolean validateBio(String bio) {
        if (!bio.isEmpty()) {
            if (EditTextBio.length() >= 160) {
                EditTextBio.setError("160 characters maximum.");
                return false;
            }
        }
        return true;
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

    /**
     * Method to validate the Street Address of any unwanted characters
     */
    public boolean checkAlphaNumericSymbol(String s) {

        String AlphaNumericSymbol = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=[]{}<>,./ ";
        boolean[] value_for_each_comparison = new boolean[s.length()];

        for (int i = 0; i < s.length(); i++) {
            for (int count = 0; count < AlphaNumericSymbol.length(); count++) {
                if (s.charAt(i) == AlphaNumericSymbol.charAt(count)) {
                    value_for_each_comparison[i] = true;
                    break;
                } else {
                    value_for_each_comparison[i] = false;
                }
            }
        }
        return checkStringCmpvalues(value_for_each_comparison);
    }

    private void choosePhoto() {
        selectPhoto = true;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select your profile picture"), 123);
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
                            ImageViewPhoto.setImageURI(selectedImagePath);
                            photoChanged = true;
                            Log.e(TAG, "On Activity Result entered" + selectedImagePath);
                            //uploadFromUri(selectedImagePath);

                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (requestCode == 1234) {
                Log.e(TAG, "Entered background uri gallery");
                if (data != null) {
                    if (data.getData() != null) {
                        selectedBackgroundPath = data.getData();
                        try {
                            backgroundChanged = true;
                            Log.e(TAG, "On Activity Result entered" + selectedBackgroundPath);
                            //uploadFromUri(selectedImagePath);

                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void chooseBackgroundPhoto() {
        Log.e(TAG, "Entered Background chooser");
        selectBackground = true;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select your Background picture"), 1234);
    }

    private void deleteAccount(){
        mAuth.signOut();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hitstreamr.com/delete-account"));
        startActivity(browserIntent);
        finish();
    }


    @Override
    public void onClick(View view) {
        if (view == SaveAccountBtn) {
            if (type.equals(getString(R.string.type_artist))) {
                registerArtist();
            } else if (type.equals(getString(R.string.type_basic))) {
                registerUser();
            }
        } else if (view == ChangePwdBtn) {
            selectChangePassword();
        } else if (view == ChangePhotoBtn) {
            choosePhoto();
        } else if(view == ChangeBackgroundBtn){
            chooseBackgroundPhoto();
        } else if (view == DeleteMyAccount){
            deleteAccount();


        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}