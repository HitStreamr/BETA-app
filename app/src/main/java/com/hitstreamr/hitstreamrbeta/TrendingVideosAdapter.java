
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

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TrendingVideosAdapter(@NonNull FirestoreRecyclerOptions<Video> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TrendingVideosAdapter.TrendingVideosHolder holder, int position, @NonNull Video model) {

        holder.videoTitle.setText(model.getTitle());
        holder.videoUsername.setText(model.getUsername());
        //holder.videoViews.setText(playlist.getPlayVideos().get(position).getvideoViews);
        Glide.with(getApplicationContext()).load(Uri.parse((model.getThumbnailUrl()))).into(holder.videoThumbnail);

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
        private TextView videoViews;
        private ImageView videoThumbnail;
        private LinearLayout parentLayout;

        public TrendingVideosHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            parentLayout = itemView.findViewById(R.id.videoCard);
        }
    }
}
