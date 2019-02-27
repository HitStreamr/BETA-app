package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {
    private static final String TAG = "FollowersAdapter";

    private ArrayList<String> followersList;
    private Context context;
    private FirebaseUser current_user;
    private Intent mIntent;
    private Library.ItemClickListener mlistner;

    public FollowersAdapter(Context context, ArrayList<String> bookList, Intent intent) {
        this.followersList = bookList;
        this.context = context;
        this.mIntent = intent;

        current_user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public FollowersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_user, parent, false);

        return new FollowersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FollowersViewHolder holder, int position) {
        StorageReference artistProfReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitstreamr-beta.appspot.com/profilePictures/" + followersList.get(position));
        if (artistProfReference == null) {
            Glide.with(getApplicationContext()).load(R.mipmap.ic_launcher_round).into(holder.image);
        } else {
            Glide.with(getApplicationContext()).load(artistProfReference).into(holder.image);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        FirebaseDatabase.getInstance().getReference("ArtistAccounts")
                        .child(followersList.get(position))
                        .child("username")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username;
                        if (dataSnapshot.exists()) {
                            holder.username.setText(dataSnapshot.getValue(String.class));
                            username = dataSnapshot.getValue(String.class);
                            Intent profile = new Intent(context, Profile.class);
                            profile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                            profile.putExtra("artistUsername",username);
                            profile.putExtra("SearchType", "ArtistAccounts");
                            context.startActivity(profile);
                        } else {
                            FirebaseDatabase.getInstance().getReference("BasicAccounts")
                                    .child(followersList.get(position)).child("username")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String username;
                                    holder.username.setText(dataSnapshot.getValue(String.class));
                                    username = dataSnapshot.getValue(String.class);
                                    Intent profile = new Intent(context, Profile.class);
                                    profile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                                    profile.putExtra("basicUsername", username);
                                    profile.putExtra("SearchType", "BasicAccounts");
                                    context.startActivity(profile);
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
        });

        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(followersList.get(position)).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.username.setText(dataSnapshot.getValue(String.class));
                } else {
                    FirebaseDatabase.getInstance().getReference("BasicAccounts").child(followersList.get(position)).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.username.setText(dataSnapshot.getValue(String.class));
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

        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(followersList.get(position)).child("artistname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.name.setText(dataSnapshot.getValue(String.class));
                } else {
                    FirebaseDatabase.getInstance().getReference("BasicAccounts").child(followersList.get(position)).child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.name.setText(dataSnapshot.getValue(String.class));
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

        FirebaseDatabase.getInstance().getReference("followers")
                .child(followersList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.followersCount.setText(Long.toString(dataSnapshot.getChildrenCount()) + " followers");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        FirebaseDatabase.getInstance().getReference("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(followersList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (current_user.getUid().equals(followersList.get(position))) {
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
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("following")
                        .child(current_user.getUid())
                        .child(followersList.get(position))
                        .setValue(followersList.get(position))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference("followers")
                                        .child(followersList.get(position))
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
                        .child(followersList.get(position))
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance()
                                        .getReference("followers")
                                        .child(followersList.get(position))
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

        // Check if user is verified
        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(followersList.get(position))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("verified").getValue(String.class).equals("true")) {
                                holder.verified.setVisibility(View.VISIBLE);
                            } else {
                                holder.verified.setVisibility(View.GONE);
                            }
                        } else {
                            FirebaseDatabase.getInstance().getReference("BasicAccounts").child(followersList.get(position))
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (dataSnapshot.child("verified").getValue(String.class).equals("true")) {
                                                    holder.verified.setVisibility(View.VISIBLE);
                                                } else {
                                                    holder.verified.setVisibility(View.GONE);
                                                }
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


    @Override
    public int getItemCount() {
        return followersList.size();
    }

    public class FollowersViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView username;
        public ImageView image, verified;
        public TextView followersCount;
        public Button follow, unfollow;
        LinearLayout cardView;

        public FollowersViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.artist_name);
            username = view.findViewById(R.id.user_name);
            cardView = itemView.findViewById(R.id.userCardView);
            image = view.findViewById(R.id.searchImage);
            followersCount = view.findViewById(R.id.count);
            follow = view.findViewById(R.id.follow_button);
            unfollow = itemView.findViewById(R.id.unfollow_button);
            verified = itemView.findViewById(R.id.verified);
        }
    }
}
