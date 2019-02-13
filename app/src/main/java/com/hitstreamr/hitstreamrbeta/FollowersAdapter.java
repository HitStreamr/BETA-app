package com.hitstreamr.hitstreamrbeta;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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

    private Library.ItemClickListener mlistner;

    public FollowersAdapter(Context context, ArrayList<String> bookList) {
        this.followersList = bookList;
        this.context = context;
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

        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(followersList.get(position)).child("firstname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.name.setText(dataSnapshot.getValue(String.class));
                } else {
                    FirebaseDatabase.getInstance().getReference("BasicAccounts").child(followersList.get(position)).child("firstname").addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (!dataSnapshot.exists()) {
                    holder.followBtn.setText("Followed");
                    //holder.followBtn.setBackgroundColor(drawable/button1);
                    //holder.followBtn.setBackground(new ColorDrawable(R.drawable.button1));
                    //holder.followBtn.setBackgroundColor(Color.parseColor("#e35bec"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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
        public ImageView image;
        public TextView followersCount;
        public Button followBtn;

        public FollowersViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.artist_name);
            username = view.findViewById(R.id.user_name);
            image = view.findViewById(R.id.searchImage);
            followersCount = view.findViewById(R.id.count);
            followBtn = view.findViewById(R.id.follow_button);
        }
    }
}