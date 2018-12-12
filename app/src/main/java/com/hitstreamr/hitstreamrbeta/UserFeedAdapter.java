package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserFeedAdapter extends ArrayAdapter<Video> {
    private Context mContext;
    private int mResource;
    private ArrayList<Video> objects1 = new ArrayList<>();
    private ArrayList<Feed> objects2 = new ArrayList<>();
    LinearLayout FeedLikes, FeedRepost;
    Profile.ItemClickListener mListener;

    public UserFeedAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Video> objects, @NonNull ArrayList<Feed> UserFeedDtls, Profile.ItemClickListener mListener) {
        super(context, resource, objects);
        this.mContext =  context;
        this.mResource = resource;
        this.objects1 = objects;
        this.objects2 = UserFeedDtls;
        this.mListener = mListener;
    }

    public interface ItemClickListener {
        void onResultClick(Video title);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String userLike;
        String UserRepost;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        Video currentVideo = objects1.get(position);

        Feed feedDtls = objects2.get(position);

        TextView title = convertView.findViewById(R.id.videoTitle);
        TextView userName = convertView.findViewById(R.id.videoUsername);
        ImageView thumbnail = convertView.findViewById(R.id.videoThumbnail);

        LinearLayout mainSection = convertView.findViewById(R.id.mainBody);

        title.setText(currentVideo.getTitle());
        userName.setText(currentVideo.getUsername());
        thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext).load(currentVideo.getThumbnailUrl()).into(thumbnail);

        FeedLikes =convertView.findViewById(R.id.Like);
        FeedRepost = convertView.findViewById(R.id.Repost);

        userLike = feedDtls.getFeedLike();
        UserRepost = feedDtls.getFeedRepost();

        if(userLike.equals("Y"))
        {
            FeedLikes.setVisibility(View.VISIBLE);
        }
        if(UserRepost.equals("Y"))
        {
            FeedRepost.setVisibility(View.VISIBLE);
        }

        mainSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResultClick(objects1.get(position));
            }
        });

        return convertView;
    }

}


