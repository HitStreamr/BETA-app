package com.hitstreamr.hitstreamrbeta;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNewPlaylist extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CreateNewPlayList";

    FirebaseUser currentFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private Button okBtn, cancelBtn;
    private EditText playListTitle;
    private Spinner playlistPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_playlist);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        playListTitle = findViewById(R.id.createPlayListTitle);
        playlistPrivacy = findViewById(R.id.createPlaylistPrivacy);

        okBtn = (Button) findViewById(R.id.createPlayListConfirm);
        cancelBtn = (Button) findViewById(R.id.createPlayListCancel);

        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void registerPlaylist() {
        //Log.e(TAG, "Your video Id is:" + vid.getVideoId());
        //Toast.makeText(VideoPlayer.this, "You liked" + vid.getVideoId(), Toast.LENGTH_SHORT).show();
        String temp = "true";

        if(playlistPrivacy.getSelectedItemPosition() == 0){
            temp = "public";
        }
        else{
            temp = "private";
        }

        FirebaseDatabase.getInstance()
                .getReference("Playlists")
                .child(currentFirebaseUser.getUid())
                .child(playListTitle.getText().toString().trim())
                .setValue(temp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                        Log.e(TAG, "registered playlist!!!");
                    }
                });
    }

    private boolean validatePlaylistName(String playlistName) {
        if (playlistName.isEmpty()) {
            playListTitle.setError("Playlist Name can't be empty");
            return false;
        } else if (playlistName.length() >= 30) {
            playListTitle.setError("Playlist can only have 30 characters");
            return false;
        }
        else {
            playListTitle.setError(null);
            return true;
        }
    }

    private void validatePlaylist(){
        if ( !validatePlaylistName((playListTitle.getText().toString().trim()))){
            Toast.makeText(getApplicationContext(), "Please fix indicated errors.", Toast.LENGTH_LONG).show();
        }
        else{
            registerPlaylist();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == okBtn) {
            Log.e(TAG, "Creating new Playlist");
            validatePlaylist();
        }
        if (view == cancelBtn) {
            finish();
            Log.e(TAG, "Cancel Creating new Playlist");
        }
    }
}
