package com.hitstreamr.hitstreamrbeta;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hitstreamr.hitstreamrbeta.BottomNav.ActivityFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PostVideoFeedAdapter extends RecyclerView.Adapter<PostVideoFeedAdapter.VideoPostHolder> {
    private static final String TAG = "PostVideoFeedAdapter";
    private ArrayList<Video> postVideoFeed ;
    private ArrayList<String> postTypeFeed ;
    private ArrayList<String> postLikeFeed;
    private ArrayList<String> postUserFeed;
    private ActivityFragment.ItemClickListener mlistner;

    public PostVideoFeedAdapter(ArrayList<Video> postVideoFeed, ArrayList<String> postTypeFeed, ArrayList<String> postLikeCountFeed, ArrayList<String> postuserFeed, ActivityFragment.ItemClickListener itemClickListener) {
        this.postVideoFeed = postVideoFeed;
        this.postTypeFeed = postTypeFeed;
        this.postLikeFeed = postLikeCountFeed;
        this.postUserFeed = postuserFeed;
        this.mlistner = itemClickListener;
        Log.e(TAG, "Post Video Feed constructor entered ");
    }

    @NonNull
    @Override
    public VideoPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_activity, parent, false);
        Log.e(TAG, "Post Video Feed holder entered ");
        return new VideoPostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostVideoFeedAdapter.VideoPostHolder holder, int position) {

        // Get the number of likes
        FirebaseDatabase.getInstance().getReference("VideoLikes").child(postVideoFeed.get(position).getVideoId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.videoLikes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // Get the number of re-posts
        FirebaseDatabase.getInstance().getReference("Repost").child(postVideoFeed.get(position).getVideoId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.videoReposts.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getApplicationContext()).load(postVideoFeed.get(position).getUrl()).into(holder.thumbnail);
        String viewCount = Long.toString(postVideoFeed.get(position).getViews());
        holder.views.setText(viewCount);
        //holder.username.setText(postVideoFeed.get(position).getUsername() );
        holder.duration.setText(postVideoFeed.get(position).getDuration());
        holder.title.setText(postVideoFeed.get(position).getTitle());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.published.setText(dateFormat.format(postVideoFeed.get(position).getTimestamp().toDate()));

        if(postTypeFeed.get(position).equalsIgnoreCase("Post")) {
            holder.activity.setText(" Posted  a video");
        }
        else if(postLikeFeed.get(position).equals("0")){
            holder.activity.setText(postTypeFeed.get(position) + "  a video");
        }
        else {
            holder.activity.setText(" and " + postLikeFeed.get(position)+"d" + " others " + postTypeFeed.get(position) + "ed  a video");
        }


        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(postVideoFeed.get(position).getUserId()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.artist.setText(dataSnapshot.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(postUserFeed.get(position)+"ed").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.username.setText(dataSnapshot.getValue(String.class));
                }
                else{
                    FirebaseDatabase.getInstance().getReference("BasicAccounts").child(postUserFeed.get(position)).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.username.setText(dataSnapshot.getValue(String.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        StorageReference artistProfReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitstreamr-beta.appspot.com/profilePictures/" + postUserFeed.get(position));
        if (artistProfReference == null) {
            Glide.with(getApplicationContext()).load(R.mipmap.ic_launcher_round).into(holder.image);
        }
        else {
            Glide.with(getApplicationContext()).load(artistProfReference).into(holder.image);
        }

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "on click trending :" +postVideoFeed.get(position));
                mlistner.onActivityClick(postVideoFeed.get(position));
            }
        });

        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onOverflowClick(postVideoFeed.get(position), holder.overflowMenu);
            }
        });



    }

    @Override
    public int getItemCount() {
        return postVideoFeed.size();
    }

    public class VideoPostHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public TextView title;
        public TextView artist;
        public TextView username;
        public TextView duration;
        public TextView views;
        public TextView published;
        public TextView activity;
        public TextView timestampDiff;
        public ImageView image;
        public TextView videoLikes;
        public TextView videoReposts;
        public FrameLayout parent;
        public Button overflowMenu;

        public VideoPostHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.feedThumbnail);
            title = itemView.findViewById(R.id.feedTitle);
            artist = itemView.findViewById(R.id.feedArtist);
            duration = itemView.findViewById(R.id.feedDuration);
            views = itemView.findViewById(R.id.feedViews);
            published = itemView.findViewById(R.id.feedPublished);
            username = itemView.findViewById(R.id.feedUsername);
            activity = itemView.findViewById(R.id.feedActivity);
            timestampDiff = itemView.findViewById(R.id.timestampDifference);
            image = itemView.findViewById(R.id.feedImage);
            videoLikes = itemView.findViewById(R.id.faveAmount);
            videoReposts = itemView.findViewById(R.id.repostAmount);
            parent = itemView.findViewById(R.id.thumbailCard);
            overflowMenu = itemView.findViewById(R.id.moreBtn);
        }
    }

}
