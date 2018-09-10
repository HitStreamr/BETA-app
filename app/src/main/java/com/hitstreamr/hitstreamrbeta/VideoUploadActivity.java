package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class VideoUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyVideoUploadService";

    private static final String VIDEO_TITLE = "Title";
    private static final String VIDEO_DESCRIPTION = "Description";
    private static final String Video_GENRE = "Genre";
    private static final String Video_SUBGENRE = "SubGenre";
    private static final String Video_PRIVACY = "Privacy";
    private static final String VIDEO_DOWNLOAD_LINK = "URL";
    private static final String USER_ID = "UserId";

    //Video View
    private VideoView artistUploadVideo;

    //Buttons
    private Button uploadBtn;
    private Button selectVideoBtn;

    //EditText Inputs
    private EditText EdittextTittle;
    private EditText EditTextDescription;

    //Spinner Inputs
    private Spinner SpinnerGenre;
    private Spinner SpinnerSubGenre;
    private Spinner SpinnerPrivacy;

    //progressBar
    private ProgressBar progressBar;

    private static final int REQUEST_CODE = 1234;
    private Uri selectedVideoPath = null;
    public String downloadVideoUri;

    public Boolean success = false;

    FirebaseFirestore db;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // [START declare_ref]
    private StorageReference mStorageRef;

    StorageReference videoRef = null;
    // [END declare_ref]

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        db = FirebaseFirestore.getInstance();

        // [START get_storage_ref]
        mStorageRef = storage.getReference();
        // [END get_storage_ref]

        //Buttons
        uploadBtn = findViewById(R.id.uploadVideo);
        selectVideoBtn = findViewById(R.id.selectVideo);

        //VideoView
        artistUploadVideo = findViewById(R.id.videoView);

        //Edittext
        EdittextTittle = findViewById(R.id.Title);
        EditTextDescription = findViewById(R.id.Description);

        //Spinner
        SpinnerGenre = findViewById(R.id.Genre);
        SpinnerSubGenre = findViewById(R.id.SubGenre);
        SpinnerPrivacy = findViewById(R.id.Privacy);

        //progressBar
        progressBar = findViewById(R.id.video_progress_bar);
        progressBar.setVisibility(View.GONE);

        // Listeners
        uploadBtn.setOnClickListener(this);
        selectVideoBtn.setOnClickListener(this);
    }

    public void selectVideo(View view) {
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
                            /*if (selectedVideoPath == null) {
                                //Log.e("selected video path = null!");
                                Toast.makeText(this, "filemangerstring Unseccussfull" + selectedVideoPath, Toast.LENGTH_SHORT).show();
                                //uploadFromUri(selectedVideoPath);
                                //Toast.makeText(this, "SUCCESS 000000000000000000000000000", Toast.LENGTH_SHORT).show();
                                //finish();
                            } else {*/
                            selectVideoBtn.setBackgroundColor(Color.GREEN);
                            selectVideoBtn.setText("Video Selected");
                            //Toast.makeText(this, "filemangerstring Unseccussfull" + selectedVideoPath, Toast.LENGTH_SHORT).show();
                            /**
                             * try to do something there
                             * selectedVideoPath is path to the selected video
                             */

                            String path = data.getData().toString();
                            artistUploadVideo.setVideoPath(path);
                            artistUploadVideo.requestFocus();
                            artistUploadVideo.start();
                            //Toast.makeText(this, "filemangerstring selected video path:" + selectedVideoPath, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "uploadFromUri: Upload succeeded");

                            //Toast.makeText(this, "Video path is uploaded seccessfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            //#debug
                            e.printStackTrace();
                        }
                    } else {
                        selectVideoBtn.setBackgroundColor(Color.RED);
                        selectVideoBtn.setText("Video not Selected");
                    }
                }
            }
        }
        //finish();
    }

   /* public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        //Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }*/

    private String uploadFromUri(final Uri fileUri) {
        final String storagetitle = EdittextTittle.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());
        //Toast.makeText(this, "on upload from uri entered", Toast.LENGTH_SHORT).show();Toast.makeText(this, "on upload from uri entered", Toast.LENGTH_SHORT).show();

        // Get a reference to store file at photos/<FILENAME>.jpg
        videoRef = mStorageRef.child("videos").child(currentFirebaseUser.getUid()).child(storagetitle);
        // [END get_child_ref] fileUri.getLastPathSegment()

        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + videoRef.getPath());


        videoRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot takeSnapshot) {
                        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadVideoUri = uri.toString();

                                //Log.e(TAG, "uploadFromUri: URI :" + downloadVideoUri);
                            }
                        });
                        // Upload succeeded
                        //Toast.makeText(this, "on upload from uri exited", Toast.LENGTH_SHORT).show();
                    }
                });
        Toast.makeText(this, "first" +downloadVideoUri, Toast.LENGTH_SHORT).show();
        return downloadVideoUri;
    }



    private void registerVideo() {
        //Toast.makeText(this, "first"+downloadVideoUri, Toast.LENGTH_SHORT).show();

        final String title = EdittextTittle.getText().toString().trim();
        final String description = EditTextDescription.getText().toString().trim();
        final String genre = SpinnerGenre.getSelectedItem().toString().trim();
        final String subGenre = SpinnerSubGenre.getSelectedItem().toString().trim();
        final String privacy = SpinnerPrivacy.getSelectedItem().toString().trim();
        //final String download = downloadVideoUri;

        if (!validateTitle(title) | !validateDescription(description) | validateBrowseVideo()) {
            return;
        } else {

            String CurrentUserID = currentFirebaseUser.getUid();

            final String download = uploadFromUri(selectedVideoPath);
            //Toast.makeText(this, "on firestore enter", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Second"+downloadVideoUri, Toast.LENGTH_SHORT).show();

            Map<String, Object> artistVideo = new HashMap<>();
            artistVideo.put(VIDEO_TITLE, title);
            artistVideo.put(VIDEO_DESCRIPTION, description);
            artistVideo.put(Video_GENRE, genre);
            artistVideo.put(Video_SUBGENRE, subGenre);
            artistVideo.put(Video_PRIVACY, privacy);
            Toast.makeText(this, "third"+downloadVideoUri, Toast.LENGTH_SHORT).show();
            artistVideo.put(VIDEO_DOWNLOAD_LINK, downloadVideoUri);
            artistVideo.put(USER_ID, CurrentUserID);

            Toast.makeText(this, "fourth"+downloadVideoUri, Toast.LENGTH_SHORT).show();

            db.collection("Videos").document()
                    .set(artistVideo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            success = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
            /*finish();
            startActivity(new Intent(getApplicationContext(), Welcome.class));*/
        }

    }

    private boolean validateBrowseVideo() {
        if (selectedVideoPath != null) {
            /*RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.activity_video_upload);
            remoteViews.setTextViewText(R.id.slectVideoBtn, "Video not Selected");*/
            return false;
        }
        selectVideoBtn.setBackgroundColor(Color.RED);
        selectVideoBtn.setText("Video not Selected");
        return true;
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

    @Override
    public void finish() {
        super.finish();
        progressBar.setVisibility(View.GONE);
        startActivity(new Intent(getApplicationContext(), Welcome.class));

    }

   /* private void videoFinish() {
        //Toast.makeText(this, "Video is uploaded"+success, Toast.LENGTH_SHORT).show();
            finish();

    }
*/
    @Override
    public void onClick(View view) {
        if (view == selectVideoBtn) {
            selectVideo();
        }
        if (view == uploadBtn) {
            registerVideo();
            Toast.makeText(this, "Video is uploaded", Toast.LENGTH_SHORT).show();
            //videoFinish();
            finish();
        }
    }
}