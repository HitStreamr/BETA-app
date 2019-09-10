package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.view.MenuItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{
    private static final String TAG = "BookAdapter";
    private ArrayList<Video> bookList;
    private Context mContext;
    private Intent mIntent;

    private Library.ItemClickListener mlistner;

    public BookAdapter(Context context, ArrayList<Video> bookList, Library.ItemClickListener mlistner,
                       Intent intent) {
        this.bookList = bookList;
        this.mlistner = mlistner;
        this.mContext = context;
        this.mIntent = intent;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watch_later_results, parent, false);

        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        holder.title.setText(bookList.get(position).getTitle());
        holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.thumbnail).load(bookList.get(position).getUrl()).into(holder.thumbnail);
        holder.duration.setText(bookList.get(position).getDuration());

        String viewCount = Long.toString(bookList.get(position).getViews());
        holder.views.setText(viewCount);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.published.setText(dateFormat.format(bookList.get(position).getTimestamp().toDate()));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onResultClick(bookList.get(position));
            }
        });

        // onClick listeners for Watch Later's popup menu
        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mlistner.onOverflowClick(bookList.get(position), holder.overflowMenu);
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.watchlater_library_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addToPlaylist_watchLater:
                                addToPlaylist(bookList.get(position));
                                break;

                            case R.id.remove_watchlLater:
                                removeWatchLater(bookList.indexOf(bookList.get(position)));
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        // Get the uploader's username
        FirebaseDatabase.getInstance().getReference("ArtistAccounts").child(bookList.get(position).getUserId())
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
     * Remove a video from the user's Watch Later list.
     * @param pos index
     */
    private void removeWatchLater(int pos) {
        FirebaseDatabase.getInstance()
                .getReference("WatchLater")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(bookList.get(pos).getVideoId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
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
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;
        public ImageView thumbnail;
        public TextView duration;
        public RelativeLayout parent;
        public TextView published;
        public TextView views;
        public TextView username;
        public Button overflowMenu;

        public BookViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.scrollingTitle);
            author = view.findViewById(R.id.watchLaterAuthor);
            thumbnail = view.findViewById(R.id.watchLaterThumbnail);
            duration = view.findViewById(R.id.watchLaterDuration);
            parent = view.findViewById(R.id.parentRLayout);
            published = view.findViewById(R.id.watchLaterPublished);
            views = view.findViewById(R.id.watchLaterViews);
            overflowMenu = view.findViewById(R.id.moreBtn);
            username = itemView.findViewById(R.id.watchLaterAuthor);
        }
    }
}