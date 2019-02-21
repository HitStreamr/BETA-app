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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentPopularPeopleAdapter extends RecyclerView.Adapter<HomeFragmentPopularPeopleAdapter.PopularPeopleHolder> {

    private List<User> userList;
    private Context mContext;
    private Intent mIntent;

    /**
     * Constructor
     */
    public HomeFragmentPopularPeopleAdapter(List<User> userList, Context mContext, Intent mIntent) {
        this.userList = userList;
        this.mContext = mContext;
        this.mIntent = mIntent;
    }

    @NonNull
    @Override
    public PopularPeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_popular_people, parent, false);
        return  new PopularPeopleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularPeopleHolder holder, int position) {
        holder.userName.setText(userList.get(position).getUsername());
        holder.verified.setVisibility(View.GONE);

        // Listener for the whole user card view
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userProfile = new Intent(mContext, Profile.class);
                userProfile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                userProfile.putExtra("basicUsername", userList.get(position).getUsername());
                userProfile.putExtra("SearchType", "BasicAccounts");
                mContext.startActivity(userProfile);
            }
        });

        // Get users' profile pictures
        String username = userList.get(position).getUsername();
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

        // Listener for view profile button
        holder.watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent artistProfile = new Intent(mContext, Profile.class);
                artistProfile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                artistProfile.putExtra("basicUsername", userList.get(position).getUsername());
                artistProfile.putExtra("SearchType", "BasicAccounts");
                mContext.startActivity(artistProfile);
            }
        });

        // Check if user is verified
        if (userList.get(position).getVerified().equals("true")) {
            holder.verified.setVisibility(View.VISIBLE);
        } else {
            holder.verified.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        } else {
            return 0;
        }
    }

    /**
     * Inner Class - Popular People Holder
     */
    public class PopularPeopleHolder extends RecyclerView.ViewHolder {

        public TextView userName, followerCount;
        public LinearLayout cardView;
        public CircleImageView profilePicture;
        public Button watch;
        public ImageView verified;

        /**
         * Constructor
         */
        public PopularPeopleHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.topArtistName);
            cardView = itemView.findViewById(R.id.topArtistCardView);
            profilePicture = itemView.findViewById(R.id.topArtistImage);
            followerCount = itemView.findViewById(R.id.topArtistFollowerCount);
            watch = itemView.findViewById(R.id.watchArtistButton);
            verified = itemView.findViewById(R.id.verified);
        }
    }
}
