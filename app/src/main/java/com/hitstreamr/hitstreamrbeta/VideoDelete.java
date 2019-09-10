package com.hitstreamr.hitstreamrbeta;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoDelete extends AppCompatActivity implements View.OnClickListener {

    private Button cancel, ok;
    private static final String TAG = "DeleteVideo";
    private String VideoIdDel;

    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_delete);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.confirm);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .4));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void deleteVideo(){
        Bundle extras = getIntent().getExtras();
        VideoIdDel = getIntent().getStringExtra("VideoId");
        Log.e(TAG, "videoid to delete "+VideoIdDel);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Videos").document(VideoIdDel)
                .update("delete","Y")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                        // onBackPressed();
                        Toast.makeText(VideoDelete.this, "Video will be Deleted in few minutes", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VideoDelete.this, "Failed to delete the Video", Toast.LENGTH_SHORT).show();

            }
        });
        //finish();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                deleteVideo();
                break;

        }

    }

}
