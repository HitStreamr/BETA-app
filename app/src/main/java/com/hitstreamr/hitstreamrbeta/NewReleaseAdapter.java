package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NewReleaseAdapter extends RecyclerView.Adapter<NewReleaseAdapter.newUploadHolder> {
    private Context mContext;
    private int mResource;
    private ArrayList<Video> objects1;
    HomeFragment.ItemClickListener mListener;
    RequestBuilder<Drawable> requestBuilder;

    public NewReleaseAdapter(int resource, @NonNull ArrayList<Video> objects, HomeFragment.ItemClickListener mListener, RequestManager gRequests) {
        this.mResource = resource;
        this.objects1 = objects;
        this.mListener = mListener;
        requestBuilder = gRequests.asDrawable();
    }

    @NonNull
    @Override
    public newUploadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate( mResource, parent, false);

        return new newUploadHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull newUploadHolder holder, int position) {

        requestBuilder.load(objects1.get(position).getUrl()).into(holder.videoThumbnail);
        holder.videoTitle.setText(objects1.get(position).getTitle());
        holder.videoTime.setText(objects1.get(position).getDuration());

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.videoYear.setText(dateFormat.format(objects1.get(position).getTimestamp().toDate()));

        holder.videoViews.setText(String.valueOf(objects1.get(position).getViews()));

        holder.mainSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResultClick(objects1.get(position));
            }
        });
        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOverflowClick(objects1.get(position), holder.moreMenu);
            }
        });

        // Get the number of likes
        FirebaseDatabase.getInstance().getReference("VideoLikes").child(objects1.get(position).getVideoId())
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
        FirebaseDatabase.getInstance().getReference("Repost").child(objects1.get(position).getVideoId())
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
        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(objects1.get(position).getUserId())
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
       return objects1.size();
    }

    public void clear() {
        final int size = objects1.size();
        objects1.clear();
        notifyItemRangeRemoved(0, size);
    }

    class newUploadHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUsername;
        TextView videoViews;
        TextView videoYear;
        TextView videoTime;
        TextView videoLikes;
        TextView videoReposts;
        ImageView moreMenu;
        LinearLayout mainSection;
        ImageView overflowMenu;
        HomeFragment.ItemClickListener mListener;

        public newUploadHolder(View itemView, final HomeFragment.ItemClickListener mListener) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoThumbnail.setSelected(true);
            videoTitle = itemView.findViewById(R.id.scrollingTitle);
            videoTitle.setSelected(true);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoYear = itemView.findViewById(R.id.published);
            videoTime = itemView.findViewById(R.id.duration);
            moreMenu = itemView.findViewById(R.id.moreMenu);
            mainSection = itemView.findViewById(R.id.mainBody);
            overflowMenu = itemView.findViewById(R.id.moreMenu);
            videoLikes = itemView.findViewById(R.id.faveAmount);
            videoReposts = itemView.findViewById(R.id.repostAmount);
            this.mListener = mListener;
        }
    }

}
