package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TrendingAdapter extends FirestoreRecyclerAdapter<Video, TrendingAdapter.TrendingHolder> {
    private static final String TAG = "TrendingAdapter";

    private HomeFragment.TrendingItemClickListener listner;
    private HomeFragment.ItemClickListener mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TrendingAdapter(@NonNull FirestoreRecyclerOptions<Video> options, HomeFragment.TrendingItemClickListener tlistner,
                           HomeFragment.ItemClickListener mListener) {
        super(options);
        this.listner = tlistner;
        this.mListener = mListener;
        Log.e(TAG, "listner value "+tlistner);
    }

    @Override
    protected void onBindViewHolder(@NonNull TrendingHolder holder, int position, @NonNull Video model) {

        holder.title.setText(model.getTitle());
        holder.author.setText(model.getUsername());
        holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.thumbnail).load(model.getThumbnailUrl()).into(holder.thumbnail);
        holder.duration.setText(model.getDuration());
        String viewCount = Long.toString(model.getViews());
        holder.viewsCount.setText(viewCount);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.published.setText(dateFormat.format(model.getTimestamp().toDate()));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "on click trending :" +model);
                listner.onTrendingVideoClick(model);
            }
        });

        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOverflowClick(model, holder.overflowMenu);
            }
        });

        // Get the number of likes
        FirebaseDatabase.getInstance().getReference("VideoLikes").child(model.getVideoId())
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
        FirebaseDatabase.getInstance().getReference("Repost").child(model.getVideoId())
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
    }

    @NonNull
    @Override
    public TrendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_categoried_video, parent, false);

        return new TrendingHolder(itemView);
    }

    class TrendingHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView author;
        public ImageView thumbnail;
        public TextView duration;
        public TextView viewsCount;
        public LinearLayout parent;
        public TextView videoLikes;
        public TextView videoReposts;
        public ImageView overflowMenu;
        public TextView published;

        public TrendingHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.scrollingTitle);
            title.setSelected(true);
            author = itemView.findViewById(R.id.videoUsername);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
            duration = itemView.findViewById(R.id.duration);
            viewsCount = itemView.findViewById(R.id.videoViews);
            parent = itemView.findViewById(R.id.mainBody);
            videoLikes = itemView.findViewById(R.id.faveAmount);
            videoReposts = itemView.findViewById(R.id.repostAmount);
            overflowMenu = itemView.findViewById(R.id.moreMenu);
            published = itemView.findViewById(R.id.published);
        }
    }
}
