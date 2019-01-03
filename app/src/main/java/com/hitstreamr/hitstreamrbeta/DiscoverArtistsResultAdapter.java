package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class DiscoverArtistsResultAdapter extends RecyclerView.Adapter<DiscoverArtistsResultAdapter.ArtistsToWatchHolder> {

    private List<ArtistUser> artistList;
    private Context mContext;
    private Intent mIntent;

    /**
     * Constructor
     */
    public DiscoverArtistsResultAdapter(List<ArtistUser> artistList, Context mContext, Intent mIntent) {
        this.artistList = artistList;
        this.mContext = mContext;
        this.mIntent = mIntent;
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

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open artist profile page
                Intent artistProfile = new Intent(getApplicationContext(), Profile.class);
                artistProfile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                artistProfile.putExtra("artistUsername", artistList.get(position).getUsername());
                mContext.startActivity(artistProfile);
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
        public LinearLayout layout;
        public Button followButton;

        public ArtistsToWatchHolder(View view) {
            super(view);

            username = view.findViewById(R.id.user_name);
            layout = view.findViewById(R.id.userResultLayout);
            followButton = view.findViewById(R.id.follow_button);
        }
    }
}
