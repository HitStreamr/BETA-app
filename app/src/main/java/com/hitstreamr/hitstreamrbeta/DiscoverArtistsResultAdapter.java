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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    }

    /**
     * Discover Result Holder - Inner Class
     */
    public class ArtistsToWatchHolder extends RecyclerView.ViewHolder {

        public TextView username, followerCount;
        public LinearLayout cardView;
        public Button followButton;
        public CircleImageView profilePicture;

        public ArtistsToWatchHolder(View view) {
            super(view);

            username = view.findViewById(R.id.user_name);
            cardView = view.findViewById(R.id.userCardView);
            followButton = view.findViewById(R.id.follow_button);
            profilePicture = view.findViewById(R.id.searchImage);
            followerCount = view.findViewById(R.id.count);
        }
    }
}
