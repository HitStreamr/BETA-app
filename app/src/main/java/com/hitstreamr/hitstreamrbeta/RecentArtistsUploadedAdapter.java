package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentArtistsUploadedAdapter extends RecyclerView.Adapter<RecentArtistsUploadedAdapter.RecentArtistViewHolder> {

    private List<ArtistUser> artistList;
    private Context mContext;
    private Intent mIntent;

    /**
     * Constructor
     */
    public RecentArtistsUploadedAdapter(List<ArtistUser> artistList, Context mContext, Intent mIntent) {
        this.artistList = artistList;
        this.mContext = mContext;
        this.mIntent = mIntent;
    }

    @NonNull
    @Override
    public RecentArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_uploaded_artists, parent, false);
        return new RecentArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentArtistViewHolder holder, int position) {
        holder.username.setText(artistList.get(position).getUsername());

        // Get the profile picture
        String username = artistList.get(position).getUsername();
        FirebaseDatabase.getInstance().getReference("UsernameUserId").child(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId = dataSnapshot.child("tempUserId").getValue().toString();
                            FirebaseStorage.getInstance().getReference("profilePictures").child(userId)
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        Glide.with(mContext).load(uri).into(holder.profilePicture);
                                    }
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // TODO: handle error
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // onClick Listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent artistProfile = new Intent(mContext, Profile.class);
                artistProfile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                artistProfile.putExtra("artistUsername", artistList.get(position).getUsername());
                artistProfile.putExtra("SearchType", "ArtistAccounts");
                mContext.startActivity(artistProfile);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (artistList != null) {
            return artistList.size();
        } else {
            return 0;
        }
    }

    public class RecentArtistViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profilePicture;
        public TextView username;
        public LinearLayout cardView;

        /**
         * Constructor
         */
        public RecentArtistViewHolder(View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.artistUsername);
            cardView = itemView.findViewById(R.id.recentArtist_cardView);
        }
    }
}
