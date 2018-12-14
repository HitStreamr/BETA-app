package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TrendingAdapter extends FirestoreRecyclerAdapter<Video, TrendingAdapter.TrendingHolder> {
    private static final String TAG = "TrendingAdapter";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TrendingAdapter(@NonNull FirestoreRecyclerOptions<Video> options) {
        super(options);
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
        holder.publishedYear.setText(pubYear);


    }

    @NonNull
    @Override
    public TrendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watch_later_results, parent, false);

        return new TrendingHolder(itemView);
    }

    class TrendingHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView author;
        public ImageView thumbnail;
        public  TextView duration;
        public  TextView viewsCount;
        public TextView publishedYear;


        public TrendingHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.scrollingTitle);
            author = itemView.findViewById(R.id.watchLaterAuthor);
            thumbnail = itemView.findViewById(R.id.watchLaterThumbnail);
            duration = itemView.findViewById(R.id.watchLaterDuration);
            viewsCount = itemView.findViewById(R.id.watchLaterViews);
            publishedYear = itemView.findViewById(R.id.watchLaterPublished);
        }
    }
}
