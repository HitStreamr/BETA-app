package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{
    private static final String TAG = "HistoryAdapter";
    private ArrayList<Video> HistoryList;
    private Context context;
    private Library.ItemClickListener mlistner;

    public HistoryAdapter(Context context, ArrayList<Video> HistoryList, Library.ItemClickListener mlistner) {
        Log.e(TAG, "Enterd History adapter" );
        this.HistoryList = HistoryList;
        this.mlistner = mlistner;
        this.context = context;
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
        holder.author.setText(HistoryList.get(position).getUsername());
        holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.thumbnail).load(HistoryList.get(position).getThumbnailUrl()).into(holder.thumbnail);
        holder.duration.setText(HistoryList.get(position).getDuration());

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        holder.published.setText(dateFormat.format(HistoryList.get(position).getTimestamp().toDate()));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onHistoryClick(HistoryList.get(position));
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addToPlaylist:
                                Intent playListAct = new Intent(context, AddToPlaylist.class);
                                playListAct.putExtra("VideoId", HistoryList.get(position).getVideoId());
                                context.startActivity(playListAct);
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.history_menu);
                popupMenu.show();
            }
        });

       }

    @Override
    public int getItemCount() {
        return HistoryList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;
        public ImageView thumbnail;
        public  TextView duration;
        public RelativeLayout parent;
        public TextView published;
        public Button more;

        public HistoryViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.scrollingTitle);
            author = view.findViewById(R.id.watchLaterAuthor);
            thumbnail = view.findViewById(R.id.watchLaterThumbnail);
            duration = view.findViewById(R.id.watchLaterDuration);
            parent = view.findViewById(R.id.parentRLayout);
            published = view.findViewById(R.id.watchLaterPublished);
            more = view.findViewById(R.id.moreBtn);
        }
    }

}
