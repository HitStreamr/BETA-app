package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.hitstreamr.hitstreamrbeta.NewReleases;

import java.util.ArrayList;

public class NewReleasesListAdapter extends RecyclerView.Adapter<NewReleasesListAdapter.newUploadHolder> {
    private Context mContext;
    private int mResource;
    private ArrayList<Video> objects1;
    NewReleases.ItemClickListener mListener;
    RequestBuilder<Drawable> requestBuilder;

    public NewReleasesListAdapter(int resource, @NonNull ArrayList<Video> objects, NewReleases.ItemClickListener mListener, RequestManager gRequests) {
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

        requestBuilder.load(objects1.get(position).getThumbnailUrl()).into(holder.videoThumbnail);
        holder.videoTitle.setText(objects1.get(position).getTitle());
        holder.videoUsername.setText(objects1.get(position).getUsername());
        //holder.videoViews.setText("TODO");
        holder.videoTime.setText(objects1.get(position).getDuration());
        //holder.videoYear.setText(String.valueOf(objects1.get(position).getPubYear()));
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

    }

    @Override
    public int getItemCount() {
        Log.e("Adapter ", "Home array : " + objects1.size());
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
        ImageView moreMenu;
        LinearLayout mainSection;
        ImageView overflowMenu;
        NewReleases.ItemClickListener mListener;

        public newUploadHolder(View itemView, final NewReleases.ItemClickListener mListener) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoYear = itemView.findViewById(R.id.published);
            videoTime = itemView.findViewById(R.id.videoTime);
            moreMenu = itemView.findViewById(R.id.moreMenu);
            mainSection = itemView.findViewById(R.id.mainBody);
            overflowMenu = itemView.findViewById(R.id.moreMenu);
            this.mListener = mListener;
        }
    }


}

