package com.hitstreamr.hitstreamrbeta;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoResultAdapter extends RecyclerView.Adapter<VideoResultAdapter.VideoResultsHolder> implements ListPreloader.PreloadSizeProvider<Video>,
        ListPreloader.PreloadModelProvider<Video>{
    ArrayList<Video> vids;
    MainActivity.ItemClickListener mListener;
    RequestBuilder<Drawable> requestBuilder;

    private int[] actualDimensions;
    public VideoResultAdapter(ArrayList<Video> videos, MainActivity.ItemClickListener mListener, RequestManager gRequests) {
        this.vids = videos;
        this.mListener = mListener;
        requestBuilder = gRequests.asDrawable();
    }

    @NonNull
    @Override
    public VideoResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_video, parent, false);

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

        return new VideoResultsHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoResultsHolder holder, int position) {

        requestBuilder.load(vids.get(position).getThumbnailUrl()).into(holder.videoThumbnail);
        holder.videoTitle.setText(vids.get(position).getTitle());
        holder.videoUsername.setText(vids.get(position).getUsername());
        holder.videoViews.setText("TODO");
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

    class VideoResultsHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoUsername;
        TextView videoViews;
        TextView videoYear;
        TextView videoTime;
        ImageView moreMenu;
        LinearLayout mainSection;
        ImageView overflowMenu;
        MainActivity.ItemClickListener mListener;

        public VideoResultsHolder(View itemView, final MainActivity.ItemClickListener mListener) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
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
