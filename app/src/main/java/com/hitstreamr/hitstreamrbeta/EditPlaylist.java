package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditPlaylist extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EditPlaylist";

    private RecyclerView recyclerView_EditPlaylistVideos;
    private EditPlaylistContentAdapter playlistContent;
    Playlist p;
    private TextView playlistName;
    private FirebaseUser current_user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference videosCollectionRef;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);

        p = new Playlist();
        p = getIntent().getParcelableExtra("playlist");

        playlistName = findViewById(R.id.editPlaylistTitle);
        recyclerView_EditPlaylistVideos = findViewById(R.id.editPlaylistRecyclerView);
        updateBtn = findViewById(R.id.update_account);
        updateBtn.setOnClickListener(this);

        Log.e(TAG, "Edit playlist" + p.getPlayVideoIds());
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        playlistName.setText(p.getPlaylistname());
        videosCollectionRef = db.collection("Videos");

        getPlayVideos();

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();
                Collections.swap(p.playVideos, position_dragged, position_target);
                playlistContent.notifyItemMoved(position_dragged, position_target);
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView_EditPlaylistVideos);

    }

    private void getPlayVideos() {
        //Log.e(TAG, "Entered onsuceess" +Play);
        ArrayList<Task<QuerySnapshot>> queryy = new ArrayList<>();
        for (int j = 0; j < p.getPlayVideoIds().size(); j++) {
            queryy.add(videosCollectionRef.whereEqualTo("videoId", p.getPlayVideoIds().get(j)).get());
        }

        Task<List<QuerySnapshot>> task = Tasks.whenAllSuccess(queryy);
        task.addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                ArrayList<Video> bb = new ArrayList<>();
                for (QuerySnapshot document : task.getResult()) {
                    for (DocumentSnapshot docume : document.getDocuments()) {
                        Log.e(TAG, "Playlist videos " + docume.toObject(Video.class).getVideoId());
                        bb.add(docume.toObject(Video.class));
                    }
                }
                p.setPlayVideos(bb);
                setupRecyclerView();
            }
        });
    }

    private void setupRecyclerView() {
        if (p.getPlayVideos().size() > 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView_EditPlaylistVideos.setLayoutManager(layoutManager);
            playlistContent = new EditPlaylistContentAdapter(this, p);
            recyclerView_EditPlaylistVideos.setAdapter(playlistContent);
        }
    }


    @Override
    public void onClick(View view) {
        if(view == updateBtn){
            Log.e(TAG, "on update btn" +p.getPlayVideos().get(0).getVideoId() +  p.getPlayVideos().get(1).getVideoId()
            + p.getPlayVideos().get(2).getVideoId());
        }
    }
}
