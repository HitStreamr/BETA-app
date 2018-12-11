package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.transloadit.android.sdk.AndroidAsyncAssembly;
import com.transloadit.android.sdk.AndroidTransloadit;
import com.transloadit.sdk.async.AssemblyProgressListener;
import com.transloadit.sdk.response.AssemblyResponse;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
public class VideoUploadActivity extends AppCompatActivity implements View.OnClickListener, contributorAdapter.deleteinterface, AssemblyProgressListener {

    private static final String TAG = "MyVideoUploadService";

    private static final String VIDEO_TITLE = "title";
    private static final String VIDEO_DESCRIPTION = "description";
    private static final String VIDEO_GENRE = "genre";
    private static final String VIDEO_SUBGENRE = "subGenre";
    private static final String VIDEO_PRIVACY = "privacy";
    private static final String VIDEO_DOWNLOAD_LINK = "url";
    private static final String THUMBNAIL_DOWNLOAD_LINK = "thumbnailUrl";
    private static final String VIDEO_PUB_YEAR = "pubYear";
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "username";
    private static final String VIDEO_CONTRIBUTOR = "contributors";
    private static final String VIDEO_DURATION = "duration";

    private static final String VIDEO_CONTRIBUTOR_NAME = "contributorName";
    private static final String VIDEO_CONTRIBUTOR_PERCENTAGE = "percentage";
    private static final String VIDEO_CONTRIBUTOR_TYPE = "type";

    private static final String VIDEO_TITLE_TERMS = "terms";



    //Video View
    private VideoView artistUploadVideo;

    //Image View
    private ImageView thumbnailImage;

    //Buttons
    private Button uploadBtn;
    private Button selectVideoBtn;
    private Button selectThumbBtn;
    private Button contributeBtn;
    private Button addContributorBtn;
    private Button retryUploadBtn;
    private Button ContributorCancelBtn;

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
    Map<String, Object> contributorVideo;
    Map<String, Object> artistVideo;

    //List View
    private ListView ContributorValuesLV;

    private static final int VID_REQUEST_CODE = 1234;
    private static final int IMG_REQUEST_CODE = 5678;

    private Uri selectedVideoPath = null;
    private Uri selectedImagePath = null;
    private long duration;
    private boolean ContributorSuccess = false;

    private String downloadVideoUri;
    private String thumbnailVideoUri;
    private AtomicBoolean successVideoUpload;
    private AtomicBoolean successFirestoreUpload;
    private AtomicBoolean videoSelected;
    private AtomicBoolean thumbnailSelected;
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
    private StorageReference thumbnailRef = null;

    //Transloadit
    AndroidTransloadit transloadit;
    AndroidAsyncAssembly assembly;

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

        //Video Duration
        duration = 0;

        //FireStore Database
        db = FirebaseFirestore.getInstance();
        mStorageRef = storage.getReference();

        //Boolean Status Checks
        successVideoUpload = new AtomicBoolean(false);
        successFirestoreUpload = new AtomicBoolean(false);
        videoSelected = new AtomicBoolean(false);
        thumbnailSelected = new AtomicBoolean(false);

        //Layout
        addContributorLayout = findViewById(R.id.add_contributor_layout);
        videoLayout = findViewById(R.id.videoUploadScreenLayout);
        videoCancelLayout = findViewById(R.id.cancelLayout);

        //Buttons
        uploadBtn = findViewById(R.id.uploadVideo);
        selectVideoBtn = findViewById(R.id.selectVideo);
        selectThumbBtn = findViewById(R.id.selectThumbnail);
        contributeBtn = findViewById(R.id.ContributorBtn);
        addContributorBtn = findViewById(R.id.AddContributorButton);
        retryUploadBtn = findViewById(R.id.retryVideoUpload);
        ContributorCancelBtn =findViewById(R.id.ContributorCancel);

        //VideoView
        artistUploadVideo = findViewById(R.id.videoView);

        //ImageView
        thumbnailImage = findViewById(R.id.imageView);

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
        contributorVideo = new HashMap<>();
        artistVideo = new HashMap<>();

        //contributorAdapter = new contributorAdapter(this, R.layout.activity_contributor_listview, contributorList);

        //progressBar
        progressBar = findViewById(R.id.uploadProgress);

        //visibility
        addContributorLayout.setVisibility(View.GONE);
        videoCancelLayout.setVisibility(View.GONE);

        //Listeners
        uploadBtn.setOnClickListener(this);
        selectVideoBtn.setOnClickListener(this);
        selectThumbBtn.setOnClickListener(this);
        contributeBtn.setOnClickListener(this);
        addContributorBtn.setOnClickListener(this);
        retryUploadBtn.setOnClickListener(this);
        ContributorCancelBtn.setOnClickListener(this);

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


        /* Transloadit Credentials */
        transloadit = new AndroidTransloadit(BuildConfig.Transloadit_API_KEY, BuildConfig.Transloadit_API_SECRET);

        assembly = transloadit.newAssembly(this,this);

        //set template Id
        assembly.addOption("template_id", BuildConfig.Transloadit_TEMPLATE_ID);

    }


    /*
     * Select Video and Callback from Video
     */
    private void selectVideo() {
        videoSelected.set(false);
        /*Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, SELECT_VIDEO); //, getApplicationContext(), VideoUploadActivity.class*/
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //Intent intent = new Intent();
        intent.setType("video/*");
        //intent.setAction(intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent, VID_REQUEST_CODE);

        startActivityForResult(Intent.createChooser(intent, "Select your video"), VID_REQUEST_CODE);
    }

    /*
     * Select Thumbnail
     */
    private void selectThumbnail() {
        thumbnailSelected.set(false);
        /*Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, SELECT_VIDEO); //, getApplicationContext(), VideoUploadActivity.class*/
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //Intent intent = new Intent();
        intent.setType("image/*");
        //intent.setAction(intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent, VID_REQUEST_CODE);

        startActivityForResult(Intent.createChooser(intent, "Select your image"), IMG_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == VID_REQUEST_CODE) {
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
                            artistUploadVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    //prepare to get duration when ready
                                    duration = artistUploadVideo.getDuration();
                                    //Log.e(TAG, duration+"");
                                    //Log.e(TAG, millisecondsToString(duration));
                                }
                            });
                            // Video has been selected succesfully
                            videoSelected.set(true);


                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    } else {
                        selectVideoBtn.setBackgroundColor(Color.RED);
                        selectVideoBtn.setText(R.string.video_not_selection);
                        // Video has not  been selected succesfully
                        videoSelected.set(false);
                    }
                }
            }else if (requestCode == IMG_REQUEST_CODE) {
                if (data != null) {
                    if (data.getData() != null) {
                        //selectedVideoPath = getPath(data.getData()); data.getData()
                        selectedImagePath = data.getData();
                        try {
                            thumbnailImage.setImageURI(selectedImagePath);
                            selectThumbBtn.setBackgroundColor(Color.GREEN);
                            selectThumbBtn.setText("Thumbnail Selected");
                            //String path = data.getData().toString();

                            // Image has been selected succesfully
                            thumbnailSelected.set(true);


                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    } else {
                        selectVideoBtn.setBackgroundColor(Color.RED);
                        selectVideoBtn.setText(R.string.video_not_selection);
                        // Image has not  been selected succesfully
                        thumbnailSelected.set(false);
                    }
                }
            }
        }
    }

    private void uploadFromUri(final Uri videoUri, final Uri imageUri){

        final String storagetitle = EdittextTittle.getText().toString().trim();
        videoRef = mStorageRef.child("videos").child(currentFirebaseUser.getUid()).child("mp4").child(storagetitle);
        String storage  = "videos/" + currentFirebaseUser.getUid()+ "/mp4" + "/" + storagetitle;
        String original = "videos/" + currentFirebaseUser.getUid()+ "/original" + "/" + storagetitle;
        String thumbnail = "thumbnails/" + currentFirebaseUser.getUid() + "/" + storagetitle;
        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + storage);

        try {
            assembly.addFile(getContentResolver().openInputStream(videoUri), storagetitle);
            assembly.addFile(getContentResolver().openInputStream(imageUri), "thumbnail_" + storagetitle);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<String, Object> exportOptions = new HashMap<>();
        exportOptions.put("path", storage);
        assembly.addStep("store_encoded", "/google/store", exportOptions);

        Map<String, Object> exportOriginalOptions = new HashMap<>();
        exportOriginalOptions.put("path", original);
        assembly.addStep("store_original", "/google/store", exportOriginalOptions);

        Map<String, Object> exportThumbnailOptions = new HashMap<>();
        exportThumbnailOptions.put("path", thumbnail);
        assembly.addStep("store_thumbnail", "/google/store", exportThumbnailOptions);


        ReportVideoPopup.SaveTask save = new ReportVideoPopup.SaveTask(this,assembly);
        save.execute(true);
    }


    private void getDownloadURL(){
        final String storagetitle = EdittextTittle.getText().toString().trim();
        videoRef = mStorageRef.child("videos").child(currentFirebaseUser.getUid()).child("mp4").child(storagetitle);
        videoRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadVideoUri = uri.toString();
                        getThumbnailURL();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successFirestoreUpload.set(false);
                retryUploadBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getThumbnailURL(){
        final String storagetitle = EdittextTittle.getText().toString().trim();
        thumbnailRef = mStorageRef.child("thumbnails").child(currentFirebaseUser.getUid()).child(storagetitle);
        thumbnailRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        thumbnailVideoUri = uri.toString();
                        registerFirebase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                retryUploadBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.",Toast.LENGTH_LONG).show();
                successFirestoreUpload.set(false);
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
        final int pubYear = Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR));

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

        artistVideo.put(VIDEO_TITLE, title);
        artistVideo.put(VIDEO_DESCRIPTION, description);
        artistVideo.put(VIDEO_GENRE, genre);
        artistVideo.put(VIDEO_SUBGENRE, subGenre);
        artistVideo.put(VIDEO_PRIVACY, privacy);
        artistVideo.put(VIDEO_DOWNLOAD_LINK, downloadVideoUri);
        artistVideo.put(THUMBNAIL_DOWNLOAD_LINK, thumbnailVideoUri);
        artistVideo.put(VIDEO_PUB_YEAR, pubYear);
        artistVideo.put(VIDEO_CONTRIBUTOR, sample);
        artistVideo.put(USER_ID, CurrentUserID);
        artistVideo.put(USER_NAME, currentFirebaseUser.getDisplayName());
        artistVideo.put(VIDEO_DURATION,millisecondsToString(duration));

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
                        successFirestoreUpload.set(true);
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
                        retryUploadBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(VideoUploadActivity.this, "Video not uploaded, please try again", Toast.LENGTH_SHORT).show();
                    }
                });

        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String strDate = simpleFormat.format(now.getTime());

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
                    artistLikes.put("timestamp",strDate);

                    //artistLikes.put("timestamp", FieldValue.serverTimestamp());
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

    private void retryVideo() {
        Toast.makeText(this, "Retrying Upload", Toast.LENGTH_SHORT).show();
        // succesful upload, but some step of the firestore upload failed for whatever reason
        if (successVideoUpload.get() && !successFirestoreUpload.get()){
            getDownloadURL();
        }else {
            //if the upload failed, then nothing else matters restart the whole process
            registerVideo();
        }
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

            uploadFromUri(selectedVideoPath, selectedImagePath);
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
        } else if (title.length() >= 100) {
            EdittextTittle.setError("Title length has crossed 100 characters");
        } else if (!(checkAlphaNumeric(title))) {
                EdittextTittle.setError("Title must only have letters and numbers");
                return false;
        } else {
            EdittextTittle.setError(null);
            return true;
        }
        return true;
    }

    private boolean validateDescription(String description) {
        if (description.isEmpty()) {
            EditTextDescription.setError("Field can't be empty");
            return false;
        } else if (description.length() >= 1000) {
            EdittextTittle.setError("Description length has crossed 1000 characters");
        } else if (!(checkAlphaNumeric(description))) {
            EdittextTittle.setError("Title must only have letters and numbers");
            return false;
        } else {
            EditTextDescription.setError(null);
            return true;
        }
        return true;
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

        String AlphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 ";
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

        if(view == selectThumbBtn){
            selectThumbnail();
        }

        if (view == uploadBtn) {
            if (videoSelected.get() && thumbnailSelected.get()){
                registerVideo();
            }else{
                if (!videoSelected.get() && thumbnailSelected.get()) {
                    Toast.makeText(this, "Please Select a Video.", Toast.LENGTH_SHORT).show();
                }else if (!thumbnailSelected.get() && videoSelected.get()){
                    Toast.makeText(this, "Please Select a Thumbnail.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Please Select a Video and Thumbnail.", Toast.LENGTH_SHORT).show();
                }
            }

        }

        if (view == retryUploadBtn) {
            retryVideo();
            retryUploadBtn.setVisibility(View.INVISIBLE);
        }
        if(view == ContributorCancelBtn){
            resetContributor();
            addContributorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void deleteposition(int deletePosition) {
        contributorList.remove(deletePosition);
        contributorFirestoreList.remove(deletePosition);
        contributorAdapter.notifyDataSetChanged();
    }

    //TRANSLOADIT IMPLEMENTATION

    @Override
    public void onUploadFinished() {
        Log.e(TAG, "Your AndroidAsyncAssembly Upload is done and it's now executing");
    }

    @Override
    public void onUploadPogress(long uploadedBytes, long totalBytes) {
        progressBar.setProgress((int) (((double) uploadedBytes) / totalBytes * 100.0));
        Log.e(TAG, "Percentage: " + (int) (((double) uploadedBytes)/ totalBytes * 100.0));
    }

    @Override
    public void onAssemblyFinished(AssemblyResponse response) {
        try {
            String res = response.json().getString("ok");
            Log.e(TAG , "Your AndroidAsyncAssembly is done executing with status: " + res);
            if (res.equalsIgnoreCase("ASSEMBLY_COMPLETED")){
                successVideoUpload.set(true);
                getDownloadURL();
            }else {
                successVideoUpload.set(false);
                retryUploadBtn.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadFailed(Exception exception) {
        successVideoUpload.set(false);
        retryUploadBtn.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Upload Failed. Please Try Again.",Toast.LENGTH_LONG).show();
        Log.e(TAG , "Upload Failed: " + exception.getMessage());
    }

    @Override
    public void onAssemblyStatusUpdateFailed(Exception exception) {
        successVideoUpload.set(false);
        retryUploadBtn.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Upload Failed. Please Try Again.",Toast.LENGTH_LONG).show();
        Log.e(TAG , "Assembly Status Update Failed: " + exception.getMessage());
        exception.printStackTrace();

    }

    private String millisecondsToString(long duration){
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;
        return minutes + ":" + seconds;
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