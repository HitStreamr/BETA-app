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
import com.hitstreamr.hitstreamrbeta.BottomNav.HomeFragment;

public class TrendingAdapter extends FirestoreRecyclerAdapter<Video, TrendingAdapter.TrendingHolder> {
    private static final String TAG = "TrendingAdapter";

    private HomeFragment.TrendingItemClickListener listner;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TrendingAdapter(@NonNull FirestoreRecyclerOptions<Video> options, HomeFragment.TrendingItemClickListener tlistner) {
        super(options);
        this.listner = tlistner;
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
        String pubYear = Long.toString(model.getPubYear());
        holder.viewsCount.setText(viewCount);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "on click trending :" +model);
                listner.onTrendingVideoClick(model);
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
        public  TextView duration;
        public  TextView viewsCount;
        public LinearLayout parent;

        public TrendingHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.scrollingTitle);
            author = itemView.findViewById(R.id.videoUsername);
            thumbnail = itemView.findViewById(R.id.videoThumbnailOverlay);
            duration = itemView.findViewById(R.id.duration);
            viewsCount = itemView.findViewById(R.id.videoViews);
            parent = itemView.findViewById(R.id.mainBody);
        }
    }
}
