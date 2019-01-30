
package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TrendingVideosAdapter extends FirestoreRecyclerAdapter<Video, TrendingVideosAdapter.TrendingVideosHolder> {
    public TrendingVideos.ItemClickListener listner;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TrendingVideosAdapter(@NonNull FirestoreRecyclerOptions<Video> options, TrendingVideos.ItemClickListener mListner) {
        super(options);
        listner = mListner;
    }

    @Override
    protected void onBindViewHolder(@NonNull TrendingVideosAdapter.TrendingVideosHolder holder, int position, @NonNull Video model) {

        holder.videoTitle.setText(model.getTitle());
        holder.videoUsername.setText(model.getUsername());
        String viewCount = Long.toString(model.getViews());
        String pubYear = Long.toString(model.getPubYear());
        holder.videoViewsCount.setText(viewCount);
        holder.videoPublishedYear.setText(pubYear);
        Glide.with(getApplicationContext()).load(Uri.parse((model.getThumbnailUrl()))).into(holder.videoThumbnail);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onTrendingMoreClick(model);
            }
        });

    }

    @NonNull
    @Override
    public TrendingVideosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_video, parent, false);

        return new TrendingVideosAdapter.TrendingVideosHolder(itemView);
    }

    class TrendingVideosHolder extends RecyclerView.ViewHolder{
        private TextView videoTitle;
        private TextView videoUsername;
        private ImageView videoThumbnail;
        private  TextView videoViewsCount;
        private TextView videoPublishedYear;
        private LinearLayout parentLayout;

        public TrendingVideosHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoViewsCount = itemView.findViewById(R.id.videoViews);
            videoPublishedYear = itemView.findViewById(R.id.videoTime);
            parentLayout = itemView.findViewById(R.id.videoCard);
        }
    }
}
