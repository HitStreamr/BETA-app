package com.hitstreamr.hitstreamrbeta.Dashboard;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.Video;
import com.hitstreamr.hitstreamrbeta.VideoDelete;
import com.hitstreamr.hitstreamrbeta.VideoEdit;
import com.hitstreamr.hitstreamrbeta.VideoPlayer;
import com.hitstreamr.hitstreamrbeta.VideoUploadActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Uploads extends Fragment{

    private FirebaseUser current_user;
    private FirestoreRecyclerAdapter<Video, DashboardUploadsHolder> firestoreRecyclerAdapter_videos;
    private String userCredit;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_uploads, container, false);

        // Get the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Upload Button Setup/Listener
        Button upload = rootView.findViewById(R.id.dashboard_upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), VideoUploadActivity.class));
            }
        });

        // Populate the recycler view with the corresponding artist's videos
        loadUploadedVideos(rootView);

        // Get the current user's credit value
        FirebaseDatabase.getInstance().getReference("Credits").child(current_user.getUid()).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentCredit = dataSnapshot.getValue(String.class);
                        if (!Strings.isNullOrEmpty(currentCredit)) {

                            userCredit = currentCredit;
                        } else
                            userCredit = "0";
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return rootView;
    }

    /**
     * Populate the recycler view with the corresponding artist's videos.
     * @param view view
     */
    private void loadUploadedVideos(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.dashboard_upload_RCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("Videos").whereEqualTo("userId", current_user.getUid()).whereEqualTo("delete","N")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Video> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(query, Video.class)
                .build();

        firestoreRecyclerAdapter_videos = new FirestoreRecyclerAdapter<Video, DashboardUploadsHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull DashboardUploadsHolder holder, int position, @NonNull Video model) {
                holder.videoTitle.setText(model.getTitle());
                holder.videoUsername.setText(model.getUsername());
                holder.videoYear.setText(String.valueOf(model.getPubYear()));
                holder.videoDuration.setText(model.getDuration());

                // TODO adjust view/views + use K/M
                String videoViews = model.getViews() + " views";
                holder.videoViews.setText(videoViews);

                // Set the video thumbnail
                String URI = model.getThumbnailUrl();
                Glide.with(holder.videoThumbnail.getContext()).load(URI).into(holder.videoThumbnail);

                holder.videoCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent videoPlayerPage = new Intent(getContext(), VideoPlayer.class);
                        videoPlayerPage.putExtra("TYPE", getActivity().getIntent().getStringExtra("TYPE"));
                        videoPlayerPage.putExtra("VIDEO", model);
                        videoPlayerPage.putExtra("CREDIT", userCredit);
                        startActivity(videoPlayerPage);
                    }
                });

                // Video popup menu
                holder.videoMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(getContext(), view);
                        MenuInflater menuInflater = popupMenu.getMenuInflater();
                        menuInflater.inflate(R.menu.dashboard_uploads_menu, popupMenu.getMenu());
                        popupMenu.show();

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    // TODO: finish implementing the video popup menu
                                    case R.id.editVideo:
                                        Intent editVid = new Intent(getApplicationContext(), VideoEdit.class);
                                        //editVid.putExtra("VideoId", vid.getVideoId());
                                        startActivity(editVid);
                                        break;

                                    case R.id.deleteVideo:
                                        Intent deleteVid = new Intent(getApplicationContext(), VideoDelete.class);
                                        deleteVid.putExtra("TYPE", getActivity().getIntent().getStringExtra("TYPE"));
                                        deleteVid.putExtra("VideoId", model.getVideoId());
                                        startActivity(deleteVid);
                                        break;
                                }
                                return false;
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public DashboardUploadsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_video, parent, false);
                return new DashboardUploadsHolder(view);
            }
        };
        recyclerView.setAdapter(firestoreRecyclerAdapter_videos);
    }

    /**
     * Dashboard Uploads - Inner Class
     */
    public class DashboardUploadsHolder extends RecyclerView.ViewHolder {

        public TextView videoTitle, videoUsername, videoYear, videoDuration, videoViews;
        public ImageView videoThumbnail, videoMenu;
        public LinearLayout videoCard;

        public DashboardUploadsHolder(View itemView) {
            super(itemView);

            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoYear = itemView.findViewById(R.id.videoYear);
            videoDuration = itemView.findViewById(R.id.videoTime);
            videoCard = itemView.findViewById(R.id.videoCard);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoMenu = itemView.findViewById(R.id.moreMenu);
        }
    }

    /**
     * Start the FireStore Recycler Adapter.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (firestoreRecyclerAdapter_videos != null) {
            firestoreRecyclerAdapter_videos.startListening();
        }
    }

    /**
     * Stop the FireStore Recycler Adapter.
     */
    @Override
    public void onStop() {
        super.onStop();

        if (firestoreRecyclerAdapter_videos != null) {
            firestoreRecyclerAdapter_videos.stopListening();
        }
    }
}
