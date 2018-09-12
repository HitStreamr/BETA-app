package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideoUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyVideoUploadService";

    private static final String VIDEO_TITLE = "title";
    private static final String VIDEO_DESCRIPTION = "description";
    private static final String VIDEO_GENRE = "genre";
    private static final String VIDEO_SUBGENRE = "subGenre";
    private static final String VIDEO_PRIVACY = "privacy";
    private static final String VIDEO_DOWNLOAD_LINK = "url";
    private static final String USER_ID = "userId";
    private static final String VIDEO_CONTRIBUTOR = "contributors";

    //Video View
    private VideoView artistUploadVideo;

    //Buttons
    private Button uploadBtn;
    private Button selectVideoBtn;
    private Button contributeBtn;
    private Button addContributorBtn;
    private Button cancelUploadBtn;

    //EditText Inputs
    private EditText EdittextTittle;
    private EditText EditTextDescription;

    private EditText EdittextContributorName;
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

    //Data Lists
    ArrayList<String> contributorPercentageList;
    ArrayList<String> contributorTypeList;
    ArrayList<String> contributorList;
    ArrayAdapter<String> contributorAdapter;

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

        //VideoView
        artistUploadVideo = findViewById(R.id.videoView);

        //Edittext
        EdittextTittle = findViewById(R.id.Title);
        EditTextDescription = findViewById(R.id.Description);
        EdittextContributorName = findViewById(R.id.ContributorName);
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

        //List View
        ContributorValuesLV = findViewById(R.id.ContributorListView);

        //Data Lists
        contributorPercentageList = new ArrayList<>();
        contributorTypeList = new ArrayList<>();
        contributorList = new ArrayList<>();
        contributorAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, contributorList);

        //progressBar
        progressBar = findViewById(R.id.uploadProgress);

        //visibility
        addContributorLayout.setVisibility(View.GONE);
        videoCancelLayout.setVisibility(View.GONE);

        //Listeners
        uploadBtn.setOnClickListener(this);
        selectVideoBtn.setOnClickListener(this);
        contributeBtn.setOnClickListener(this);
        addContributorBtn.setOnClickListener(this);
        cancelUploadBtn.setOnClickListener(this);
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
        //Toast.makeText(this, "on Activity", Toast.LENGTH_SHORT).show();
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

    private void registerFirebase() {
        final String title = EdittextTittle.getText().toString().trim();
        final String description = EditTextDescription.getText().toString().trim();
        final String genre = SpinnerGenre.getSelectedItem().toString().trim();
        final String subGenre = SpinnerSubGenre.getSelectedItem().toString().trim();
        final String privacy = SpinnerPrivacy.getSelectedItem().toString().trim();

        final String CurrentUserID = currentFirebaseUser.getUid();

        Map<String, Object> artistVideo = new HashMap<>();
        artistVideo.put(VIDEO_TITLE, title);
        artistVideo.put(VIDEO_DESCRIPTION, description);
        artistVideo.put(VIDEO_GENRE, genre);
        artistVideo.put(VIDEO_SUBGENRE, subGenre);
        artistVideo.put(VIDEO_PRIVACY, privacy);
        artistVideo.put(VIDEO_DOWNLOAD_LINK, downloadVideoUri);
        artistVideo.put(VIDEO_CONTRIBUTOR, contributorList);

        artistVideo.put(USER_ID, CurrentUserID);
        db.collection("Videos").document()
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
                                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                homeIntent.putExtra("TYPE", getString(R.string.type_artist));
                                startActivity(homeIntent);
                            }
                        }, SPLASH_TIME_OUT);
                        successMessage();
                        /*finish();
                        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        homeIntent.putExtra("TYPE", getString(R.string.type_artist));
                        startActivity(homeIntent);*/
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(VideoUploadActivity.this, "Video not uploaded, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelVideo() {
        mstorageTask.cancel();
        Toast.makeText(this, "Video Upload Canceled", Toast.LENGTH_SHORT).show();
        finish();
        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        homeIntent.putExtra("TYPE", getString(R.string.type_artist));
        startActivity(homeIntent);
    }


    private void successMessage() {
        Toast.makeText(this, "Video Uploaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void registerVideo() {
        final String title = EdittextTittle.getText().toString().trim();
        final String description = EditTextDescription.getText().toString().trim();
        //final String genre = SpinnerGenre.getSelectedItem().toString().trim();
        //final String subGenre = SpinnerSubGenre.getSelectedItem().toString().trim();
        //final String privacy = SpinnerPrivacy.getSelectedItem().toString().trim();

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
            contributorList.add(name + ", " + percentage + ", " + type);
            contributorPercentageList.add(percentage);
            ContributorValuesLV.setAdapter(contributorAdapter);
            contributorAdapter.notifyDataSetChanged();
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
}