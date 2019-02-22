package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlayListHolder>{
    private static final String TAG = "PlayListAdapter";
    private ArrayList<String> Playlists;
    private Context mContext;

    private AddToPlaylist.ItemClickListener mlistner;

    public PlaylistAdapter(Context context, ArrayList<String> playlists, AddToPlaylist.ItemClickListener mlistner) {
        this.mContext = context;
        this.Playlists = playlists;
        this.mlistner = mlistner;
        Log.e(TAG, "Playlists adapter holder entered "+getItemCount());
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list, parent, false);
        Log.e(TAG, "Playlists adapter holder entered ");

        return new PlayListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, int position) {
        holder.singlePlaylist.setText(Playlists.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "Itemclicked and selcted");
                Toast.makeText(mContext, Playlists.get(position) + " selected", Toast.LENGTH_SHORT).show();

                for(int i = 0; i<Playlists.size(); i++){
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                holder.parentLayout.setBackgroundColor(Color.parseColor("#0000FF"));


                mlistner.onResultClick(Playlists.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return Playlists.size();
    }

    public class PlayListHolder extends RecyclerView.ViewHolder {
        public TextView singlePlaylist;
        public LinearLayout parentLayout;

        public PlayListHolder(View view) {
            super(view);
            singlePlaylist = view.findViewById(R.id.playlistItem);
            parentLayout = view.findViewById(R.id.parentLayout);
        }
    }
}