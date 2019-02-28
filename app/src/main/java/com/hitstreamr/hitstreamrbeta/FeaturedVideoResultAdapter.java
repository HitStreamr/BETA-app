package com.hitstreamr.hitstreamrbeta;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeaturedVideoResultAdapter extends RecyclerView.Adapter<FeaturedVideoResultAdapter.FeaturedVideoResultsHolder> implements ListPreloader.PreloadSizeProvider<Video>,
        ListPreloader.PreloadModelProvider<Video>{
    ArrayList<Video> vids;
    VideoClickListener mListener;
    RequestBuilder<Drawable> requestBuilder;

    private int[] actualDimensions;
    public FeaturedVideoResultAdapter(ArrayList<Video> videos, VideoClickListener mListener, RequestManager gRequests) {
        this.vids = videos;
        this.mListener = mListener;
        requestBuilder = gRequests.asDrawable();
    }

    @NonNull
    @Override
    public FeaturedVideoResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_featured_video, parent, false);

        if (actualDimensions == null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (actualDimensions == null) {
                        actualDimensions = new int[] { view.findViewById(R.id.videoThumbnail).getMeasuredWidth(), view.findViewById(R.id.videoThumbnail).getMeasuredHeight() };
                    }
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

        return new FeaturedVideoResultsHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedVideoResultsHolder holder, int position) {
        DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
        requestBuilder.load(vids.get(position).getUrl()).into(holder.videoThumbnail);
        StorageReference artistProfReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitstreamr-beta.appspot.com/profilePictures/" + vids.get(position).getUserId());

        if (artistProfReference == null) {
            requestBuilder.load(R.mipmap.ic_launcher_round).into(holder.artistProfPic);
        } else {
            requestBuilder.load(artistProfReference).into(holder.artistProfPic);
        }

        holder.videoTitle.setText(vids.get(position).getTitle());
        holder.videoViews.setText(formatt(vids.get(position).getViews()));
        holder.videoTime.setText(vids.get(position).getDuration());

        if (vids.get(position).getTimestamp() != null)
            holder.videoPublish.setText(df2.format(vids.get(position).getTimestamp().toDate()));

        holder.videoThumbnail.setOnClickListener(v -> mListener.onResultClick(vids.get(position)));
        holder.overflowMenu.setOnClickListener(v -> mListener.onOverflowClick(vids.get(position), holder.overflowMenu));

        // Get the number of likes
        FirebaseDatabase.getInstance().getReference("VideoLikes").child(vids.get(position).getVideoId())
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
        FirebaseDatabase.getInstance().getReference("Repost").child(vids.get(position).getVideoId())
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

        // Get the uploader's username
        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(vids.get(position).getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.videoUsername.setText(dataSnapshot.child("username").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return vids.size();
    }

    public void clear() {
        final int size = vids.size();
        vids.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public List<Video> getPreloadItems(int position) {
        if (vids.size() == 0){
            return Collections.emptyList();
        }

        return Collections.singletonList(vids.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Video item) {
        return requestBuilder.load(item.getUrl());
    }

    @Nullable
    @Override
    public int[] getPreloadSize(@NonNull Video item, int adapterPosition, int perItemPosition) {
        return actualDimensions;
    }

    //Holder for the Individual featured Videos
    class FeaturedVideoResultsHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUsername;
        TextView videoViews;
        TextView videoPublish;
        TextView videoTime;
        TextView videoLikes;
        TextView videoReposts;
        Button overflowMenu;
        CircleImageView artistProfPic;
        VideoClickListener mListener;

        public FeaturedVideoResultsHolder(View itemView, final VideoClickListener mListener) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.featuredVideoThumbnail);
            videoTitle = itemView.findViewById(R.id.title);
            videoTitle.setSelected(true);
            videoUsername = itemView.findViewById(R.id.artistName);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoPublish = itemView.findViewById(R.id.publishDate);
            videoTime = itemView.findViewById(R.id.videoLength);
            overflowMenu = itemView.findViewById(R.id.overflowButton);
            artistProfPic = itemView.findViewById(R.id.artistProfilePicture);
            videoLikes = itemView.findViewById(R.id.faveAmount);
            videoReposts = itemView.findViewById(R.id.repostAmount);
            this.mListener = mListener;
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatt(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatt(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatt(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
