package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoUploadActivity extends AppCompatActivity implements View.OnClickListener, contributorAdapter.deleteinterface {

    private static final String TAG = "MyVideoUploadService";

    private static final String VIDEO_TITLE = "title";
    private static final String VIDEO_DESCRIPTION = "description";
    private static final String VIDEO_GENRE = "genre";
    private static final String VIDEO_SUBGENRE = "subGenre";
    private static final String VIDEO_PRIVACY = "privacy";
    private static final String VIDEO_DOWNLOAD_LINK = "url";
    private static final String USER_ID = "userId";
    private static final String VIDEO_CONTRIBUTOR = "contributors";

    private static final String VIDEO_CONTRIBUTOR_NAME = "contributorName";
    private static final String VIDEO_CONTRIBUTOR_PERCENTAGE = "percentage";
    private static final String VIDEO_CONTRIBUTOR_TYPE = "type";

    private static final String VIDEO_TITLE_TERMS = "terms";



    //Video View
    private VideoView artistUploadVideo;

    //Buttons
    private Button uploadBtn;
    private Button selectVideoBtn;
    private Button contributeBtn;
    private Button addContributorBtn;
    private Button cancelUploadBtn;
    private Button deleteContributorBtn;

    //EditText Inputs
    private EditText EdittextTittle;
    private EditText EditTextDescription;

    //private EditText EdittextContributorName;
    private AutoCompleteTextView EdittextContributorName;
    private EditText EdittextContributorPercentage;

    //Spinner Inputs
    private Spinner SpinnerGenre;
    private Spinner SpinnerSubGenre;
    private Spinner SpinnerPrivacy;
    private Spinner SpinnerContributorType;

    //progressBar
    private ProgressBar progressBar;

    //Layout
    private LinearLayout addContributorLayout;
    private LinearLayout videoLayout;
    private LinearLayout videoCancelLayout;

    //Text View
    private TextView TextViewVideoFilename;
    private TextView TextViewSizeLabel;
    private TextView TextViewProgressLabel;

    private TextView TextViewContributorName;
    private TextView TextViewContributorPercentage;
    private TextView TextViewContributorType;

    //Data Lists
    ArrayList<String> contributorPercentageList;
    ArrayList<String> contributorFirestoreList;
    public ArrayList<Contributor> contributorList;
    public contributorAdapter contributorAdapter;

    //List View
    private ListView ContributorValuesLV;

    private static final int REQUEST_CODE = 1234;
    private Uri selectedVideoPath = null;
    private boolean ContributorSuccess = false;
    public String downloadVideoUri;
    public Boolean successVideoUpload = false;
    public Boolean successFirestoreUpload = false;
    private static int SPLASH_TIME_OUT = 3000;

    //Firestore Database
    private FirebaseFirestore db;
    DatabaseReference database;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    //Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageTask mstorageTask;
    private StorageReference videoRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Upload");
        toolbar.setTitleTextColor(0xFFFFFFFF);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Profile Picture
        if (currentFirebaseUser.getPhotoUrl() != null) {
            CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = currentFirebaseUser.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }

        //FireStore Database
        db = FirebaseFirestore.getInstance();
        mStorageRef = storage.getReference();

        //Layout
        addContributorLayout = findViewById(R.id.add_contributor_layout);
        videoLayout = findViewById(R.id.videoUploadScreenLayout);
        videoCancelLayout = findViewById(R.id.cancelLayout);

        //Buttons
        uploadBtn = findViewById(R.id.uploadVideo);
        selectVideoBtn = findViewById(R.id.selectVideo);
        contributeBtn = findViewById(R.id.ContributorBtn);
        addContributorBtn = findViewById(R.id.AddContributorButton);
        cancelUploadBtn = findViewById(R.id.cancelVideoUpload);
        //deleteContributorBtn =findViewById(R.id.deleteContributor);

        //VideoPlayer
        artistUploadVideo = findViewById(R.id.videoView);

        //Edittext
        EdittextTittle = findViewById(R.id.Title);
        EditTextDescription = findViewById(R.id.Description);
       // EdittextContributorName = findViewById(R.id.ContributorName);
        EdittextContributorPercentage = findViewById(R.id.ContributorPercentage);

        //Spinner
        SpinnerGenre = findViewById(R.id.Genre);
        SpinnerSubGenre = findViewById(R.id.SubGenre);
        SpinnerPrivacy = findViewById(R.id.Privacy);
        SpinnerContributorType = findViewById(R.id.ContributorTypeSpinner);

        //Text View
        TextViewVideoFilename = findViewById(R.id.filenameText);
        TextViewSizeLabel = findViewById(R.id.sizeLabel);
        TextViewProgressLabel = findViewById(R.id.progressLabel);

        TextViewContributorName = findViewById(R.id.firstName);
        TextViewContributorPercentage = findViewById(R.id.thirdLine);
        TextViewContributorType = findViewById(R.id.secondLine);

        //List View
        ContributorValuesLV = findViewById(R.id.ContributorListView);

        //Data Lists
        contributorPercentageList = new ArrayList<>();
        contributorFirestoreList = new ArrayList<>();
        contributorList = new ArrayList<>();
        //contributorAdapter = new contributorAdapter(this, R.layout.activity_contributor_listview, contributorList);

        //progressBar
        progressBar = findViewById(R.id.uploadProgress);

        //visibility,
        addContributorLayout.setVisibility(View.GONE);
        videoCancelLayout.setVisibility(View.GONE);

        //Listeners
        uploadBtn.setOnClickListener(this);
        selectVideoBtn.setOnClickListener(this);
        contributeBtn.setOnClickListener(this);
        addContributorBtn.setOnClickListener(this);
        cancelUploadBtn.setOnClickListener(this);
        //deleteContributorBtn.setOnClickListener(this);

        ContributorValuesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contributorList.remove(position);
                contributorAdapter.notifyDataSetChanged();
            }
        });
        database = FirebaseDatabase.getInstance().getReference();

        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        database.child("ArtistAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                autoComplete.clear();
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    String username = suggestionSnapshot.child("username").getValue(String.class);
                    //Log.e(TAG,"username --"+username);
                    //Add the retrieved string to the list
                    autoComplete.add(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        EdittextContributorName= (AutoCompleteTextView)findViewById(R.id.ContributorName);
        EdittextContributorName.setAdapter(autoComplete);
        autoComplete.notifyDataSetChanged();

        EdittextContributorName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    // on focus off
                    String str = EdittextContributorName.getText().toString();

                    ListAdapter listAdapter = EdittextContributorName.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareTo(temp) == 0) {
                            return;
                        }
                    }

                    EdittextContributorName.setText("");
                    Toast.makeText(VideoUploadActivity.this, "Please choose Contributor name from the suggested list", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    private void selectVideo() {
        /*Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, SELECT_VIDEO); //, getApplicationContext(), VideoUploadActivity.class*/
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //Intent intent = new Intent();
        intent.setType("video/*");
        //intent.setAction(intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent, REQUEST_CODE);

        startActivityForResult(Intent.createChooser(intent, "Select your video"), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    if (data.getData() != null) {
                        //selectedVideoPath = getPath(data.getData()); data.getData()
                        selectedVideoPath = data.getData();
                        try {

                            selectVideoBtn.setBackgroundColor(Color.GREEN);
                            selectVideoBtn.setText(R.string.video_selection);

                            String path = data.getData().toString();
                            artistUploadVideo.setVideoPath(path);
                            artistUploadVideo.requestFocus();
                            artistUploadVideo.start();
                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    } else {
                        selectVideoBtn.setBackgroundColor(Color.RED);
                        selectVideoBtn.setText(R.string.video_not_selection);
                    }
                }
            }
        }
    }

    private void uploadFromUri(final Uri fileUri) {
        final String storagetitle = EdittextTittle.getText().toString().trim();
        videoRef = mStorageRef.child("videos").child(currentFirebaseUser.getUid()).child(storagetitle);
        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + videoRef.getPath());


        mstorageTask = videoRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot takeSnapshot) {
                        registerFirebase();
                        videoRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        successVideoUpload = true;
                                        downloadVideoUri = uri.toString();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoUploadActivity.this, "Video Upload failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        String ProgressText = taskSnapshot.getBytesTransferred() / (1024 * 1024) + " / " + taskSnapshot.getTotalByteCount() / (1024 * 1024) + "mb";
                        //String ProgresspercentText = (taskSnapshot.getBytesTransferred()/(1024 * 1024) / taskSnapshot.getTotalByteCount() * 100 ) + "%";
                        String p = (int) progress + "%";
                        progressBar.setProgress((int) progress);
                        TextViewSizeLabel.setText(ProgressText);
                        TextViewProgressLabel.setText(p);
                    }
                });
    }
    public Map<String, Object> contributorVideo = new HashMap<>();

    private void registerFirebase() {
        final String title = EdittextTittle.getText().toString().trim();
        final String description = EditTextDescription.getText().toString().trim();
        final String genre = SpinnerGenre.getSelectedItem().toString().trim();
        final String subGenre = SpinnerSubGenre.getSelectedItem().toString().trim();
        final String privacy = SpinnerPrivacy.getSelectedItem().toString().trim();
        final String CurrentUserID = currentFirebaseUser.getUid();

        ArrayList<Object> sample = new ArrayList<>();

        //Map<String, Object> sample = new HashMap<>();
        for(i=0; i<contributorFirestoreList.size(); i++) {

            String temp = contributorFirestoreList.get(i);

            String[] tempArray = temp.split(",");

            Map<String, Object> Contributor = new HashMap<>();
            Contributor.put(VIDEO_CONTRIBUTOR_NAME, tempArray[0]);
            Contributor.put(VIDEO_CONTRIBUTOR_PERCENTAGE, tempArray[1]);
            Contributor.put(VIDEO_CONTRIBUTOR_TYPE, tempArray[2]);
            sample.add(Contributor);
        }


        Map<String, Object> artistVideo = new HashMap<>();
        artistVideo.put(VIDEO_TITLE, title);
        artistVideo.put(VIDEO_DESCRIPTION, description);
        artistVideo.put(VIDEO_GENRE, genre);
        artistVideo.put(VIDEO_SUBGENRE, subGenre);
        artistVideo.put(VIDEO_PRIVACY, privacy);
        artistVideo.put(VIDEO_DOWNLOAD_LINK, downloadVideoUri);
        artistVideo.put(VIDEO_CONTRIBUTOR, sample);
        artistVideo.put(USER_ID, CurrentUserID);

        Map<String, Boolean> terms = new HashMap<>();
        ArrayList<String> res = processTitle(title);
        for(int i = 0; i < res.size(); i++){
            terms.put(res.get(i), true);
        }
        artistVideo.put(VIDEO_TITLE_TERMS,terms);


        db.collection("Videos").document()
                //.set(contributorList)
                .set(artistVideo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        successFirestoreUpload = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                homeIntent.putExtra("TYPE", getString(R.string.type_artist));
                                startActivity(homeIntent);
                            }
                        }, SPLASH_TIME_OUT);
                        successMessage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(VideoUploadActivity.this, "Video not uploaded, please try again", Toast.LENGTH_SHORT).show();
                    }
                });

        // Creates like counts for artists if it does not exist yet
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("ArtistsNumbers")
                .document(CurrentUserID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (!documentSnapshot.exists()) {
                    Map<String, Object> artistLikes = new HashMap<>();
                    artistLikes.put("likes", 0);
                    artistLikes.put("timestamp", FieldValue.serverTimestamp());
                    //documentReference.set(artistLikes);
                    firebaseFirestore.collection("ArtistsNumbers").document(CurrentUserID)
                            .set(artistLikes)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("SUCCESS", "SUCCESS");
                        }
                    });
                }
            }
        });
    }

    private ArrayList<String> processTitle(String title){
        // ArrayList of characters to remove
        ArrayList<String> remove = new ArrayList<>();
        remove.add(" ");

        ArrayList<String> tmp = new ArrayList<>(Arrays.asList(title.trim().toLowerCase().split("\\s+")));
        tmp.removeAll(remove);

        return tmp;
    }

    private void cancelVideo() {
        mstorageTask.cancel();
        Toast.makeText(this, "Video Upload Canceled", Toast.LENGTH_SHORT).show();
        finish();
        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
        homeIntent.putExtra("TYPE", getString(R.string.type_artist));
        startActivity(homeIntent);
    }


    private void successMessage() {
        Toast.makeText(this, "Video Uploaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void registerVideo() {
        final String title = EdittextTittle.getText().toString().trim();
        final String description = EditTextDescription.getText().toString().trim();

        if (!validateTitle(title) | !validateDescription(description) | !validateBrowseVideo() | !validateSumPercentage()
                | !validateGenre() | !validateSubGenre()) {
            return;
        } else {
            Log.e("tag", "Enter register video");
            videoLayout.setVisibility(View.GONE);
            videoCancelLayout.setVisibility(View.VISIBLE);
            TextViewVideoFilename.setText(title);

            uploadFromUri(selectedVideoPath);
        }
    }

    private boolean validateBrowseVideo() {
        if (selectedVideoPath != null) {
            return true;
        }
        selectVideoBtn.setBackgroundColor(Color.RED);
        selectVideoBtn.setText(R.string.video_not_selection);
        return false;
    }

    private boolean validateTitle(String title) {
        if (title.isEmpty()) {
            EdittextTittle.setError("Field can't be empty");
            return false;
        } else if (title.length() <= 100) {
            if (!(checkAlphaNumeric(title))) {
                EdittextTittle.setError("Title must only have letters and numbers");
                return false;
            }
            return true;
        } else {
            EdittextTittle.setError(null);
            return true;
        }
    }

    private boolean validateDescription(String description) {
        if (description.isEmpty()) {
            EditTextDescription.setError("Field can't be empty");
            return false;
        } else if (description.length() <= 100) {
            if (!(checkAlphaNumeric(description))) {
                EdittextTittle.setError("Description must only have letters and numbers");
                return false;
            }
            return true;
        } else {
            EditTextDescription.setError(null);
            return true;
        }
    }

    private boolean validateGenre() {
        if (SpinnerGenre.getSelectedItem().toString().trim().equals("--Select--")) {
            Toast.makeText(this, "Genre is not selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateSubGenre() {
        if (SpinnerSubGenre.getSelectedItem().toString().trim().equals("--Select--")) {
            Toast.makeText(this, "SubGenre is not selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    int i, sum = 0, tempvalue = 0;
    String temp;

    private boolean validateSumPercentage() {
        for (i = 1; i < contributorPercentageList.size(); i++) {
            temp = contributorPercentageList.get(i);
            tempvalue = Integer.parseInt(temp);
            sum += tempvalue;
        }
        if (sum > 100) {
            Toast.makeText(this, "Percentage exceding 100, please check", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateContributorName(String Cname) {
        if (Cname.isEmpty()) {
            EdittextContributorName.setError("Field can't be empty");
            return false;
        } else {
            EdittextContributorName.setError(null);
            return true;
        }
    }

    private boolean validatePercentage(String Cpercentage) {
        if (Cpercentage.isEmpty()) {
            EdittextContributorPercentage.setError("Field can't be empty");
            return false;
        } else {
            EdittextContributorPercentage.setError(null);
            return true;
        }
    }

    private boolean validateContributorType() {
        if (SpinnerContributorType.getSelectedItem().toString().trim().equals("Select")) {
            Toast.makeText(this, "Contributor Type is not selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void GetContributor() {

        final String name = EdittextContributorName.getText().toString().trim();
        final String percentage = EdittextContributorPercentage.getText().toString().trim();
        final String type = SpinnerContributorType.getSelectedItem().toString().trim();

        if (!validateContributorName(name) | !validatePercentage(percentage) | !validateContributorType()) {
            {
                ContributorSuccess = false;
            }
        } else {

            contributorVideo.put(VIDEO_CONTRIBUTOR_NAME, name);
            contributorVideo.put(VIDEO_CONTRIBUTOR_PERCENTAGE, percentage);
            contributorVideo.put(VIDEO_CONTRIBUTOR_TYPE, type);

            Contributor contributor1 = new Contributor(name, percentage, type);
            contributorFirestoreList.add(name + "," + percentage + "," + type);
            contributorList.add(contributor1);
            contributorPercentageList.add(percentage);

            contributorAdapter = new contributorAdapter(this, R.layout.activity_contributor_listview, contributorList, this);
            ContributorValuesLV.setAdapter(contributorAdapter);
            contributorAdapter.notifyDataSetChanged();
            //TextViewContributorName.setText(name);
            //TextViewContributorPercentage.setText(percentage);
            //TextViewContributorType.setText(type);
            ContributorSuccess = true;
        }
    }

    private void resetContributor() {
        EdittextContributorName.setText("");
        EdittextContributorPercentage.setText("");
        SpinnerContributorType.setSelection(0);
    }

    @Override
    public void onClick(View view) {
        if (view == contributeBtn) {
            addContributorLayout.setVisibility(View.VISIBLE);
        }

        if (view == addContributorBtn) {
            GetContributor();
            if (ContributorSuccess) {
                resetContributor();
                addContributorLayout.setVisibility(View.GONE);
                Toast.makeText(this, "Contributor added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Contributor not added", Toast.LENGTH_SHORT).show();
            }
        }

        if (view == selectVideoBtn) {
            selectVideo();
        }
        if (view == uploadBtn) {
            registerVideo();
        }

        if (view == cancelUploadBtn) {
            cancelVideo();
        }
    }

    @Override
    public void deleteposition(int deletePosition) {
        contributorList.remove(deletePosition);
        contributorFirestoreList.remove(deletePosition);
        contributorAdapter.notifyDataSetChanged();
    }

    /**
     * Handles back button on toolbar
     * @return true if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}