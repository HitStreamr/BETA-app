package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DiscoverArtistsResultAdapter extends RecyclerView.Adapter<DiscoverArtistsResultAdapter.ArtistsToWatchHolder> {

    private List<ArtistUser> artistList;
    private Context mContext;
    private Intent mIntent;
    private FirebaseUser current_user;

    /**
     * Constructor
     */
    public DiscoverArtistsResultAdapter(List<ArtistUser> artistList, Context mContext, Intent mIntent) {
        this.artistList = artistList;
        this.mContext = mContext;
        this.mIntent = mIntent;

        current_user = FirebaseAuth.getInstance().getCurrentUser();
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
        holder.artistName.setText(artistList.get(position).getArtistname());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open artist profile page
                Intent artistProfile = new Intent(getApplicationContext(), Profile.class);
                artistProfile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                artistProfile.putExtra("artistUsername", artistList.get(position).getUsername());
                artistProfile.putExtra("SearchType", "ArtistAccounts");
                mContext.startActivity(artistProfile);
            }
        });

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO follow the artist account
            }
        });

        String username = artistList.get(position).getUsername();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsernameUserId")
                .child(username);
        databaseReference.addValueEventListener(new ValueEventListener() {
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

        // Followers count
        // TODO: add thousands/millions k/m feature
        databaseReference = FirebaseDatabase.getInstance().getReference("UsernameUserId").child(username);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.child("tempUserId").getValue().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    db.getReference("followers").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int follower = (int) dataSnapshot.getChildrenCount();
                            if (follower > 1) {
                                holder.followerCount.setText(follower + " Followers");
                            } else {
                                holder.followerCount.setText(follower + " Follower");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Remove follow/un-follow button if it's your own account
        if (current_user.getUid().equals(artistList.get(position).getUserID())) {
            holder.follow.setVisibility(View.GONE);
            holder.unfollow.setVisibility(View.GONE);
        }

        // Check if user is verified
        if (artistList.get(position).getVerified().equals("true")) {
            holder.verified.setVisibility(View.VISIBLE);
        } else {
            holder.verified.setVisibility(View.GONE);
        }

        // Remove follow/un-follow button if it's your own account
        if (current_user.getUid().equals(artistList.get(position).getUserID())) {
            holder.follow.setVisibility(View.GONE);
            holder.unfollow.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(artistList.get(position).getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (current_user.getUid().equals(artistList.get(position).getUserID())) {
                    holder.follow.setVisibility(View.GONE);
                    holder.unfollow.setVisibility(View.GONE);
                } else if (!dataSnapshot.exists()) {
                    holder.follow.setVisibility(View.VISIBLE);
                    holder.unfollow.setVisibility(View.GONE);
                } else if (dataSnapshot.exists()) {
                    holder.follow.setVisibility(View.GONE);
                    holder.unfollow.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("following")
                        .child(current_user.getUid())
                        .child(artistList.get(position).getUserID())
                        .setValue(artistList.get(position).getUserID())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference("followers")
                                        .child(artistList.get(position).getUserID())
                                        .child(current_user.getUid())
                                        .setValue(current_user.getUid())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                holder.follow.setVisibility(View.GONE);
                                                holder.unfollow.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        });
            }
        });

        holder.unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance()
                        .getReference("following")
                        .child(current_user.getUid())
                        .child(artistList.get(position).getUserID())
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance()
                                        .getReference("followers")
                                        .child(artistList.get(position).getUserID())
                                        .child(current_user.getUid())
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                holder.follow.setVisibility(View.VISIBLE);
                                                holder.unfollow.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        });
            }
        });
    }

    /**
     * Discover Result Holder - Inner Class
     */
    public class ArtistsToWatchHolder extends RecyclerView.ViewHolder {

        TextView username, followerCount, artistName;
        LinearLayout cardView;
        Button followButton;
        CircleImageView profilePicture;
        ImageView verified;
        Button follow, unfollow;

        public ArtistsToWatchHolder(View view) {
            super(view);

            username = view.findViewById(R.id.user_name);
            artistName = view.findViewById(R.id.artist_name);
            cardView = view.findViewById(R.id.userCardView);
            followButton = view.findViewById(R.id.follow_button);
            profilePicture = view.findViewById(R.id.searchImage);
            followerCount = view.findViewById(R.id.count);
            verified = itemView.findViewById(R.id.verified);
            follow = itemView.findViewById(R.id.follow_button);
            unfollow = itemView.findViewById(R.id.unfollow_button);
        }
    }
}
