package com.hitstreamr.hitstreamrbeta;
import android.content.Context;
import android.content.Intent;
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
import android.widget.PopupMenu;
import android.view.MenuItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{
    private static final String TAG = "BookAdapter";
    private ArrayList<Video> bookList;
    private Context context;
    //private Intent mIntent;

    private Library.ItemClickListener mlistner;

    public BookAdapter(Context context, ArrayList<Video> bookList, Library.ItemClickListener mlistner) {
        this.bookList = bookList;
        this.mlistner = mlistner;
        this.context = context;
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
        holder.author.setText(bookList.get(position).getUsername());
        holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.thumbnail).load(bookList.get(position).getThumbnailUrl()).into(holder.thumbnail);
        holder.duration.setText(bookList.get(position).getDuration());

        String viewCount = Long.toString(bookList.get(position).getViews());
        holder.views.setText(viewCount);

        //holder.views.setText(Long.toString((bookList.get(position).getViews())));


        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.published.setText(dateFormat.format(bookList.get(position).getTimestamp().toDate()));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onResultClick(bookList.get(position));
            }
        });

        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onOverflowClick(bookList.get(position), holder.overflowMenu);
            }
        });
    }

    private void RemoveWatchLater(int pos){
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


    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;
        public ImageView thumbnail;
        public  TextView duration;
        public RelativeLayout parent;
        public TextView published;
        public TextView views;
        private Button overflowMenu;

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
        }
    }
}