package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{
    private static final String TAG = "HistoryAdapter";
    private ArrayList<Video> HistoryList;
    private Context mContext;
    private Library.ItemClickListener mlistner;
    private Intent mIntent;

    public  HistoryAdapter(Context context, ArrayList<Video> HistoryList, Library.ItemClickListener mlistner, Intent type) {
        Log.e(TAG, "Enterd History adapter" );
        this.HistoryList = HistoryList;
        this.mlistner = mlistner;
        this.mContext = context;
        this.mIntent = type;
    }

    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watch_later_results, parent, false);

        return new HistoryAdapter.HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.HistoryViewHolder holder, int position) {
        holder.title.setText(HistoryList.get(position).getTitle());
        holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.thumbnail).load(HistoryList.get(position).getUrl()).into(holder.thumbnail);
        holder.duration.setText(HistoryList.get(position).getDuration());

        String viewCount = Long.toString(HistoryList.get(position).getViews());
        holder.views.setText(viewCount);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.published.setText(dateFormat.format(HistoryList.get(position).getTimestamp().toDate()));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onHistoryClick(HistoryList.get(position));
            }
        });

        // onClick listeners for History's popup menu
        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mlistner.onOverflowClick(HistoryList.get(position), holder.overflowMenu);
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.history_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addToPlaylist_history:
                                addToPlaylist(HistoryList.get(position));
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        // Get the uploader's username
        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(HistoryList.get(position).getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.username.setText(dataSnapshot.child("username").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Add the video to the playlist.
     * @param video video
     */
    private void addToPlaylist(Video video) {
        Intent playlistIntent = new Intent(mContext, AddToPlaylist.class);
        playlistIntent.putExtra("VIDEO", video);
        playlistIntent.putExtra("TYPE", mIntent.getExtras().getString("TYPE"));
        mContext.startActivity(playlistIntent);
    }

    @Override
    public int getItemCount() {
        return HistoryList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;
        public ImageView thumbnail;
        public TextView duration;
        public RelativeLayout parent;
        public TextView published;
        public Button overflowMenu;
        public TextView views;
        public TextView username;

        public HistoryViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.scrollingTitle);
            author = view.findViewById(R.id.watchLaterAuthor);
            thumbnail = view.findViewById(R.id.watchLaterThumbnail);
            duration = view.findViewById(R.id.watchLaterDuration);
            parent = view.findViewById(R.id.parentRLayout);
            published = view.findViewById(R.id.watchLaterPublished);
            overflowMenu = view.findViewById(R.id.moreBtn);
            views = view.findViewById(R.id.watchLaterViews);
            username = itemView.findViewById(R.id.watchLaterAuthor);
        }
    }



}
