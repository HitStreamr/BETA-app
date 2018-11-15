package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BookAdapter extends ArrayAdapter<Video> {

    private Context mContext;
    private int mResource;
    private ArrayList<Video> objects1 = new ArrayList<>();

    public BookAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Video> objects) {
        super(context, resource, objects);
        this.mContext =  context;
        this.mResource = resource;
        this.objects1 = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.e("TAG", "object values"+objects1.toString());
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        Video currentVideo = objects1.get(position);

        Log.e("TAG", "object current values"+currentVideo.toString());

        TextView title = (TextView) convertView.findViewById(R.id.scrollingTitle);
        TextView author = (TextView) convertView.findViewById(R.id.watchLaterAuthor);
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.watchLaterThumbnail);
        TextView duration = (TextView) convertView.findViewById(R.id.watchLaterDuration);


        title.setText(currentVideo.getTitle());
        author.setText(currentVideo.getUsername());
        thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext).load(currentVideo.getThumbnailUrl()).into(thumbnail);
        duration.setText(currentVideo.getDuration());

        return convertView;
    }
}