package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DiscRecyclerView extends RecyclerView.Adapter<DiscRecyclerView.ViewHolder>{

    private static final String TAG = "DiscRecyclerView";

    private ArrayList<String> mImageView = new ArrayList<>();
    private ArrayList<String> mImageText = new ArrayList<>();
    private Context mContext;
    private Intent mIntent;

    public DiscRecyclerView(ArrayList<String> mImageView, ArrayList<String> mImageText, Context mContext, Intent mIntent) {
        this.mImageView = mImageView;
        this.mImageText = mImageText;
        this.mContext = mContext;
        this.mIntent = mIntent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_discover_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        Glide.with(mContext)
                .load(mImageView.get(position))
                .into(holder.imageView);

        holder.imageText.setText(mImageText.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mImageText.get(position));

                Toast.makeText(mContext, mImageText.get(position), Toast.LENGTH_SHORT).show();

                Intent discoverResult = new Intent(mContext, DiscoverResultPage.class);
                discoverResult.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                discoverResult.putExtra("DISCOVER", mImageText.get(position));
                mContext.startActivity(discoverResult);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageText.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView imageText;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.discoverimage);
            imageText = itemView.findViewById(R.id.discovertext);
            parentLayout = itemView.findViewById(R.id.discover_layout);
        }
    }
}
