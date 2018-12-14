package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.List;

public class ArtistsToWatchAdapter extends RecyclerView.Adapter<ArtistsToWatchAdapter.TopArtistsHolder> {

    private List<ArtistUser> artistList;
    private Context mContext;
    private Intent mIntent;

    public ArtistsToWatchAdapter(List<ArtistUser> artistList, Context mContext, Intent mIntent) {
        this.artistList = artistList;
        this.mContext = mContext;
        this.mIntent = mIntent;
    }

    @NonNull
    @Override
    public TopArtistsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_user, parent, false);
        return new TopArtistsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistsHolder holder, int position) {
        holder.artistName.setText(artistList.get(position).getUsername());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open artist profile page
                Intent artistProfile = new Intent(mContext, Profile.class);
                artistProfile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                artistProfile.putExtra("artistUsername", artistList.get(position).getUsername());
                mContext.startActivity(artistProfile);
            }
        });

        // TODO: profile picture, followers count
    }

    @Override
    public int getItemCount() {
        if (artistList != null) {
            return artistList.size();
        } else {
            return 0;
        }
    }

    /**
     * Inner Class - Top Artists Holder
     */
    public class TopArtistsHolder extends RecyclerView.ViewHolder {

        public TextView artistName;
        public LinearLayout cardView;

        public TopArtistsHolder(View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.user_name);
            cardView = itemView.findViewById(R.id.userCardView);
        }
    }
}
