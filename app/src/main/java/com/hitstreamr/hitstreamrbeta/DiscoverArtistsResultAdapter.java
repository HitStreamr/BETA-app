package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.List;

public class DiscoverArtistsResultAdapter extends RecyclerView.Adapter<DiscoverArtistsResultAdapter.ArtistsToWatchHolder> {

    private List<ArtistUser> artistList;

    /**
     * Constructor
     */
    public DiscoverArtistsResultAdapter(List<ArtistUser> artistList) {
        this.artistList = artistList;
    }

    @Override
    public int getItemCount() {
        if (artistList != null) {
            return artistList.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public ArtistsToWatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_user, parent, false);
        return new ArtistsToWatchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsToWatchHolder holder, int position) {
        holder.username.setText(artistList.get(position).getUsername());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open artist profile page
            }
        });

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO follow the artist account
            }
        });
    }

    /**
     * Discover Result Holder - Inner Class
     */
    public class ArtistsToWatchHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public LinearLayout cardView;
        public Button followButton;

        public ArtistsToWatchHolder(View view) {
            super(view);

            username = view.findViewById(R.id.user_name);
            cardView = view.findViewById(R.id.userCardView);
            followButton = view.findViewById(R.id.follow_button);
        }
    }
}