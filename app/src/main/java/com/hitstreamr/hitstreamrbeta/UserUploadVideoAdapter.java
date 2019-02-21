package com.hitstreamr.hitstreamrbeta;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;


public class UserUploadVideoAdapter extends RecyclerView.Adapter<UserUploadVideoAdapter.VideoUploadHolder> {
    ArrayList<Video> vids;
    Profile.ItemClickListener mListener;
    RequestBuilder<Drawable> requestBuilder;

    public UserUploadVideoAdapter(ArrayList<Video> videos, Profile.ItemClickListener mListener, RequestManager gRequests) {
        this.vids = videos;
        this.mListener = mListener;
        requestBuilder = gRequests.asDrawable();
    }

    @NonNull
    @Override
    public VideoUploadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_upload_layout, parent, false);

        return new VideoUploadHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoUploadHolder holder, int position) {

        requestBuilder.load(vids.get(position).getUrl()).into(holder.videoThumbnail);
        holder.videoTitle.setText(vids.get(position).getTitle());
        //TODO needs to be a callback (or however follows are done)
//        holder.videoUsername.setText(vids.get(position).getUsername());
        holder.videoViews.setText(String.valueOf(vids.get(position).getViews()) + " views");
        holder.videoTime.setText(vids.get(position).getDuration());
        holder.videoYear.setText(String.valueOf(vids.get(position).getPubYear()));
        holder.mainSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResultClick(vids.get(position));
            }
        });
        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOverflowClick(vids.get(position), holder.moreMenu);
            }
        });
        holder.videoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResultClick(vids.get(position));
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

    class VideoUploadHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUsername;
        TextView videoViews;
        TextView videoYear;
        TextView videoTime;
        ImageView moreMenu;
        LinearLayout mainSection;
        ImageView overflowMenu;
        Profile.ItemClickListener mListener;

        public VideoUploadHolder(View itemView, final Profile.ItemClickListener mListener) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle1);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoYear = itemView.findViewById(R.id.videoYear);
            videoTime = itemView.findViewById(R.id.videoTime);
            moreMenu = itemView.findViewById(R.id.moreMenu);
            mainSection = itemView.findViewById(R.id.mainBody);
            overflowMenu = itemView.findViewById(R.id.moreMenu);
            this.mListener = mListener;
        }
    }



}
