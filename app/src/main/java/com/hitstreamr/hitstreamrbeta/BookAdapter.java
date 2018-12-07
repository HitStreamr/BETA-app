package com.hitstreamr.hitstreamrbeta;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{

    private ArrayList<Video> bookList;
    private Context context;

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

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onResultClick(bookList.get(position));
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

        public BookViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.scrollingTitle);
            author = view.findViewById(R.id.watchLaterAuthor);
            thumbnail = view.findViewById(R.id.watchLaterThumbnail);
            duration = view.findViewById(R.id.watchLaterDuration);
            parent = view.findViewById(R.id.parentRLayout);
        }
    }
}