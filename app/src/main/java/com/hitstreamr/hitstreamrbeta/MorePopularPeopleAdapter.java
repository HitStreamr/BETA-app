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
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MorePopularPeopleAdapter extends RecyclerView.Adapter<MorePopularPeopleAdapter.MorePopularPeopleHolder> {

    private List<User> userList;
    private Context mContext;
    private Intent mIntent;
    private FirebaseUser current_user;

    /**
     * Constructor
     */
    public MorePopularPeopleAdapter(List<User> userList, Context mContext, Intent mIntent) {
        this.userList = userList;
        this.mContext = mContext;
        this.mIntent = mIntent;

        current_user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MorePopularPeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_user, parent, false);
        return new MorePopularPeopleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MorePopularPeopleHolder holder, int position) {
        holder.userName.setText(userList.get(position).getUsername());
        holder.fullName.setText(userList.get(position).getFullname());

        // Check if user is verified
        if (userList.get(position).getVerified().equals("true")) {
            holder.verified.setVisibility(View.VISIBLE);
        } else {
            holder.verified.setVisibility(View.GONE);
        }

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

        // Remove follow/un-follow button if it's your own account
        if (current_user.getUid().equals(userList.get(position).getUserID())) {
            holder.follow.setVisibility(View.GONE);
            holder.unfollow.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userList.get(position).getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (current_user.getUid().equals(userList.get(position).getUserID())) {
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
                        .child(userList.get(position).getUserID())
                        .setValue(userList.get(position).getUserID())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference("followers")
                                        .child(userList.get(position).getUserID())
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
                        .child(userList.get(position).getUserID())
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance()
                                        .getReference("followers")
                                        .child(userList.get(position).getUserID())
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
    public class MorePopularPeopleHolder extends RecyclerView.ViewHolder {

        TextView userName, followerCount, fullName;
        LinearLayout cardView;
        CircleImageView profilePicture;
        ImageView verified;
        Button follow, unfollow;

        public MorePopularPeopleHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            cardView = itemView.findViewById(R.id.userCardView);
            profilePicture = itemView.findViewById(R.id.searchImage);
            followerCount = itemView.findViewById(R.id.count);
            verified = itemView.findViewById(R.id.verified);
            follow = itemView.findViewById(R.id.follow_button);
            unfollow = itemView.findViewById(R.id.unfollow_button);
            fullName = itemView.findViewById(R.id.artist_name);
        }
    }
}
