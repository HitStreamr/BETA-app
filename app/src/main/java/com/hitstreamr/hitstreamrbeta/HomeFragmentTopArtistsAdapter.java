package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.hitstreamr.hitstreamrbeta.UserTypes.ArtistUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentTopArtistsAdapter extends RecyclerView.Adapter<HomeFragmentTopArtistsAdapter.TopArtistsHolder> {

    private List<ArtistUser> artistList;
    private Context mContext;
    private Intent mIntent;

    /**
     * Constructor
     */
    public HomeFragmentTopArtistsAdapter(List<ArtistUser> artistList, Context mContext, Intent mIntent) {
        this.artistList = artistList;
        this.mContext = mContext;
        this.mIntent = mIntent;
    }

    @NonNull
    @Override
    public TopArtistsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_popular_people, parent, false);
        return  new TopArtistsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistsHolder holder, int position) {
        holder.artistName.setText(artistList.get(position).getUsername());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent artistProfile = new Intent(mContext, Profile.class);
                artistProfile.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                artistProfile.putExtra("artistUsername", artistList.get(position).getUsername());
                mContext.startActivity(artistProfile);
            }
        });

//        String username = artistList.get(position).getUsername();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsernameUserId")
//                .child(username);
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.exists()) {
//                    String userId = dataSnapshot.child("tempUserId").getValue(String.class);
//                    FirebaseStorage.getInstance().getReference("profilePictures").child(userId)
//                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            if (uri != null) {
//                                Glide.with(mContext).load(uri).into(holder.profilePicture);
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        // TODO: followers count
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
     * Home Fragment - Top Artists Holder
     */
    public class TopArtistsHolder extends RecyclerView.ViewHolder {

        public TextView artistName;
        public LinearLayout cardView;
        public CircleImageView profilePicture;

        public TopArtistsHolder(View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.topArtistName);
            cardView = itemView.findViewById(R.id.topArtistCardView);
            profilePicture = itemView.findViewById(R.id.topArtistImage);
        }
    }
}
