package com.hitstreamr.hitstreamrbeta;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DeletePlaylist extends AppCompatActivity {

    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_playlist);

        // Set the display's dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .4));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Delete the playlist from Firebase.
     */
    private void deletePlaylist() {
        // Get the playlist to be deleted
        Playlist playlist = getIntent().getParcelableExtra("PLAYLIST");

        // Delete from 'Playlists' collection in Firebase
        FirebaseDatabase.getInstance().getReference("Playlists")
                .child(current_user.getUid())
                .child(playlist.getPlaylistname())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Delete from 'PlaylistVideos' collection in Firebase
                        FirebaseDatabase.getInstance().getReference("PlaylistVideos")
                                .child(current_user.getUid())
                                .child(playlist.getPlaylistname())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),
                                                playlist.getPlaylistname() + " is now deleted.",
                                                Toast.LENGTH_LONG);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * CANCEL BUTTON
     * Cancel the playlist deletion.
     * @param view view
     */
    public void cancelDeletePlaylist(View view) {
        // Close the display and return to previous activity
        finish();
    }

    /**
     * OK BUTTON
     * Confirm and proceed with the deletion.
     * @param view view
     */
    public void confirmDeletePlaylist(View view) {
        // Proceed with deletion
        deletePlaylist();
    }
}
