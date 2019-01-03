package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.google.type.Date;
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;

import com.google.firebase.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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

        requestBuilder.load(objects1.get(position).getThumbnailUrl()).into(holder.videoThumbnail);
        holder.videoTitle.setText(objects1.get(position).getTitle());
        holder.videoUsername.setText(objects1.get(position).getUsername());
        //holder.videoViews.setText("TODO");
        holder.videoTime.setText(objects1.get(position).getDuration());

        String sdate = objects1.get(position).getTimestamp().toDate().toString();

        holder.videoYear.setText(sdate);
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
            this.mListener = mListener;
        }
    }

}
