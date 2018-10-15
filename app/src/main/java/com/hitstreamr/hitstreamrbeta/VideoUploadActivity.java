package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.transloadit.android.sdk.AndroidAsyncAssembly;
import com.transloadit.android.sdk.AndroidTransloadit;
import com.transloadit.sdk.async.AssemblyProgressListener;
import com.transloadit.sdk.response.AssemblyResponse;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VideoUploadActivity extends AppCompatActivity implements View.OnClickListener, contributorAdapter.deleteinterface, AssemblyProgressListener {

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

    Map<String, Object> artistVideo = new HashMap<>();

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

    //Transloadit
    AndroidTransloadit transloadit;
    AndroidAsyncAssembly assembly;

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
        ContributorCancelBtn =findViewById(R.id.ContributorCancel);

        //VideoView
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

        //visibility
        addContributorLayout.setVisibility(View.GONE);
        videoCancelLayout.setVisibility(View.GONE);

        //Listeners
        uploadBtn.setOnClickListener(this);
        selectVideoBtn.setOnClickListener(this);
        contributeBtn.setOnClickListener(this);
        addContributorBtn.setOnClickListener(this);
        cancelUploadBtn.setOnClickListener(this);
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

        transloadit = new AndroidTransloadit(BuildConfig.Transloadit_API_KEY, BuildConfig.Transloadit_API_SECRET);

        assembly = transloadit.newAssembly(this,this);

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

    private void uploadFromUri(final Uri fileUri){
        final String storagetitle = EdittextTittle.getText().toString().trim();
        videoRef = mStorageRef.child("videos").child(currentFirebaseUser.getUid()).child("mp4").child(storagetitle);
        String storage  = "videos/" + currentFirebaseUser.getUid()+ "/mp4" + "/" + storagetitle;
        String original = "videos/" + currentFirebaseUser.getUid()+ "/original" + "/" + storagetitle;
        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + storage);

        try {
            assembly.addFile(getContentResolver().openInputStream(fileUri), storagetitle);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        /*
              "video_android": {
              "use": "viruscheck",
              "robot": "/video/encode",
              "ffmpeg_stack": "v3.3.3",
              "preset": "android",
              "width": 320,
              "height": 240
             }
    */

        Map<String, Object> stepOptions = new HashMap<>();
        stepOptions.put("use", ":original");
        stepOptions.put("ffmpeg_stack", "v3.3.3");
        stepOptions.put("preset", "android");
        //stepOptions.put("result","true");
        stepOptions.put("width", 1080);
        stepOptions.put("height", 720);
        assembly.addStep("video_android", "/video/encode", stepOptions);

        /*
        "export": {
      "use": [
        "thumbnail",
        "video_android",
        "video_iphone",
        "video_webm",
        "video_hls",
        "video_dash"
      ],
      "robot": "/google/store",
      "credentials": "gcs_cred"
    }
         */

        Map<String, Object> exportOptions = new HashMap<>();
        exportOptions.put("use", "video_android");
        exportOptions.put("credentials", "gcs_cred");
        exportOptions.put("path", storage);
        assembly.addStep("export", "/google/store", exportOptions);

        Map<String, Object> exportOriginalOptions = new HashMap<>();
        exportOriginalOptions.put("use", ":original");
        exportOriginalOptions.put("credentials", "gcs_cred");
        exportOriginalOptions.put("path", original);
        assembly.addStep("exportOriginal", "/google/store", exportOriginalOptions);


        SaveTask save = new SaveTask(this,assembly);
        save.execute(true);
    }
    public Map<String, Object> contributorVideo = new HashMap<>();

    private void getDownloadURL(){
        final String storagetitle = EdittextTittle.getText().toString().trim();
        videoRef = mStorageRef.child("videos").child(currentFirebaseUser.getUid()).child("mp4").child(storagetitle);

        videoRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        successVideoUpload = true;
                        downloadVideoUri = uri.toString();
                        registerFirebase();
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

    private ArrayList<String> processTitle(String title){
        // ArrayList of characters to remove
        ArrayList<String> remove = new ArrayList<>();
        remove.add(" ");

        ArrayList<String> tmp = new ArrayList<>(Arrays.asList(title.trim().toLowerCase().split("\\s+")));
        tmp.removeAll(remove);

        return tmp;
    }

    private void cancelVideo() {
        //TODO Cancel the assembly
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
        if (view == uploadBtn) {
            registerVideo();
        }

        if (view == cancelUploadBtn) {
            cancelVideo();
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
                getDownloadURL();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadFailed(Exception exception) {
        Log.e(TAG , "Upload Failed: " + exception.getMessage());
    }

    @Override
    public void onAssemblyStatusUpdateFailed(Exception exception) {
        Log.e(TAG , "Assembly Status Update Failed: " + exception.getMessage());
        exception.printStackTrace();

    }


}