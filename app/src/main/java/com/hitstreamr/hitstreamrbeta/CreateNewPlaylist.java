package com.hitstreamr.hitstreamrbeta;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width), (int) (height * .8));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
        String ttt = "true";

        FirebaseDatabase.getInstance()
                .getReference("Playlists")
                .child(currentFirebaseUser.getUid())
                .child(playListTitle.getText().toString().trim())
                .setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                        Log.e(TAG, "registered playlist!!!");
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == okBtn) {
            Log.e(TAG, "Creating new Playlist");
            registerPlaylist();
        }
        if (view == cancelBtn) {
            finish();
            Log.e(TAG, "Cancel Creating new Playlist");
        }
    }
}
