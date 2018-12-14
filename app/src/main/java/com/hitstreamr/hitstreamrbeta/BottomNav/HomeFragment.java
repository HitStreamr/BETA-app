package com.hitstreamr.hitstreamrbeta.BottomNav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.Video;

import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    //Featured Artist
    RecyclerView featuredArtistRCV;
    FirestoreRecyclerAdapter minVideos;
    Query featuredVideosQuery;
    FirestoreRecyclerOptions<Video> featuredArtistOptions;
    private final int FEATURED_LOAD = 5;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * Featured Artist Start
         */
         //Recycler View
         featuredArtistRCV = view.findViewById(R.id.featuredVideosRCV);

        featuredVideosQuery = FirebaseFirestore.getInstance()
                .collection("Videos")
                .orderBy("views")
                .whereEqualTo("privacy","Public (everyone can see)")
                .limit(FEATURED_LOAD);

         //Options
        featuredArtistOptions = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(featuredVideosQuery, Video.class)
                .build();


         //Firestore Recycler Adaptor
        FirestoreRecyclerAdapter minVideos = new FirestoreRecyclerAdapter<Video,FeaturedVideoViewHolder>(featuredArtistOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FeaturedVideoViewHolder holder, int position, @NonNull Video model) {
                Log.d("HOME", model.toString());
                Glide.with(getContext())
                        .load(model.getThumbnailUrl())
                        .into(holder.thumbnail);
            }

            @NonNull
            @Override
            public FeaturedVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FeaturedVideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_featured_video, parent, false));
            }
        };

        minVideos.startListening();
        featuredArtistRCV.setAdapter(minVideos);
        featuredArtistRCV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        /**
         *  Featured Artist End
         */
    }

    /**
     * Featured Video Holder
     */
    public class  FeaturedVideoViewHolder extends RecyclerView.ViewHolder {
        private View view;
        ImageView thumbnail;

        FeaturedVideoViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            thumbnail = view.findViewById(R.id.videoThumbnail);
        }

        void setUserName(final String userName) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //click the video, go to player?
                }
            });

        }
    }

    /**
     * Timestamp utility functions
     */
    public static Date dateFromUTC(Date date){
        return new Date(date.getTime() + Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public static Date dateToUTC(Date date){
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }
}
