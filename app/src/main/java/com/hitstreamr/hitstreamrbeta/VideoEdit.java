package com.hitstreamr.hitstreamrbeta;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoEdit extends AppCompatActivity implements View.OnClickListener {

    // ArrayList
    private ArrayList<TextView> contributorList;

    // Buttons
    private Button saveEditVideo;

    // EditText
    private EditText title, description;

    // ImageView
    private ImageView videoThumbnail;

    // LinearLayout
    private LinearLayout contributorLayout;

    // Spinner
    private Spinner genre, subGenre, privacy;

    // Video being edited
    private Video video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);

        // Prevent keyboard from showing up automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Private method to bind views and their IDs
        bindViewsIDs();

        // Get the video object to be edited
        video = getIntent().getParcelableExtra("VIDEO");

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Edit Video");
        toolbar.setTitleTextColor(0xFFFFFFFF);

        // Get the current video's data
        loadCurrentVideoData();
    }

    /**
     * Load the current video's data.
     */
    private void loadCurrentVideoData() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").document(video.getVideoId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Video video_object = documentSnapshot.toObject(Video.class);

                            // Set the video thumbnail
                            String URI = video_object.getThumbnailUrl();
                            Glide.with(getApplicationContext()).load(URI).into(videoThumbnail);

                            title.setText(video_object.getTitle());
                            description.setText(video_object.getDescription());
                            genre.setSelection(getIndex(genre, video_object.getGenre()));
                            subGenre.setSelection(getIndex(subGenre, video_object.getSubGenre()));
                            privacy.setSelection(getIndex(privacy, video_object.getPrivacy()));

                            // Display the contributors
                            contributorList = new ArrayList<>();
                            ArrayList<HashMap<String,String>> temp = (ArrayList<HashMap<String,String>>) documentSnapshot.get("contributors");

                            for (HashMap<String,String> contributor : temp) {
                                TextView TVtemp = new TextView(getApplicationContext());
                                TVtemp.setText(contributor.get("contributorName") + " (" +  contributor.get("type") + ")" + ", ");
                                TVtemp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));

                                contributorList.add(TVtemp);
                            }

                            if (contributorList.size() > 0) {
                                TextView last = contributorList.get(contributorList.size() - 1);
                                last.setText(last.getText().toString().substring(0,last.getText().toString().length() - 2));
                                contributorList.set(contributorList.size() - 1,last);

                                for(TextView tv : contributorList){
                                    contributorLayout.addView(tv);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /**
     * Get and update the new video data to the database.
     */
    private void updateVideoData() {
        String title_update = title.getText().toString().trim();
        String description_update = description.getText().toString().trim();
        String genre_update = genre.getSelectedItem().toString();
        String subGenre_update = subGenre.getSelectedItem().toString();
        String privacy_update = privacy.getSelectedItem().toString();

        // TODO: validate title, description, genre, sub-genre
        if (!validateTitle(title_update) | !validateDescription(description_update) | !validateGenre() |
                !validateSubGenre()) {
            return;
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").document(video.getVideoId())
                .update(
                        "title", title_update,
                        "description", description_update,
                        "genre", genre_update,
                        "subGenre", subGenre_update,
                        "privacy", privacy_update
                );
        // TODO: add success/failure

        Toast.makeText(getApplicationContext(), "Video updated successfully.", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    /**
     * Method to get the index of a specified string in the spinner.
     * @param spinner spinner object
     * @param myString specified string
     * @return index
     */
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Saves the video's edits.
     * @param view view
     */
    @Override
    public void onClick(View view) {
        if (view == saveEditVideo){
            updateVideoData();
        }
    }

    /**
     * Handles the back button on toolbar.
     * @return true, if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Find and bind the views with their respective IDs.
     */
    private void bindViewsIDs() {
        videoThumbnail = findViewById(R.id.videoThumbnail_editVideo);
        title = findViewById(R.id.title_editVideo);
        description = findViewById(R.id.description_editVideo);
        genre = findViewById(R.id.genre_editVideo);
        subGenre = findViewById(R.id.subGenre_editVideo);
        contributorLayout = findViewById(R.id.contributor_editVideo);
        privacy = findViewById(R.id.privacy_editVideo);

        saveEditVideo = findViewById(R.id.update_account);
        saveEditVideo.setOnClickListener(this);
    }

    /**
     * Validate the title field.
     * @param title_input edited title
     * @return true if valid
     */
    private boolean validateTitle(String title_input) {
        if (title_input.isEmpty()) {
            title.setError("Field can't be empty");
            return false;
        } else if (title_input.length() > 100) {
            title.setError("Cannot exceed 100 characters");
            return false;
        }
        title.setError(null);
        return true;
    }

    /**
     * Validate the description field.
     * @param description_input edited description
     * @return true if valid
     */
    private boolean validateDescription(String description_input) {
        if (description_input.isEmpty()) {
            description.setError("Field can't be empty");
            return false;
        } else if (description_input.length() > 1000) {
            description.setError("Cannot exceed 1000 characters");
            return false;
        }
        description.setError(null);
        return true;
    }

    /**
     * Validate the genre field.
     * @return true if valid
     */
    private boolean validateGenre() {
        if (genre.getSelectedItem().toString().trim().equals("--Select--")) {
            TextView errorText = (TextView) genre.getSelectedView();
            errorText.setTextColor(Color.RED);
            errorText.setText("None selected");
            return false;
        }
        return true;
    }

    /**
     * Validate the sub-genre field.
     * @return true if valid
     */
    private boolean validateSubGenre() {
        if (subGenre.getSelectedItem().toString().trim().equals("--Select--")) {
            TextView errorText = (TextView) subGenre.getSelectedView();
            errorText.setTextColor(Color.RED);
            errorText.setText("None selected");
            return false;
        }
        return true;
    }
}
