package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RelatedVideosAdapter extends RecyclerView.Adapter<RelatedVideosAdapter.RelatedVideosHolder> {

    private List<Video> videoList;
    private Context mContext;
    private Intent mIntent;

    /**
     * Constructor
     */
    public RelatedVideosAdapter(List<Video> videoList, Context mContext, Intent mIntent) {
        this.videoList = videoList;
        this.mContext = mContext;
        this.mIntent = mIntent;
    }

    @NonNull
    @Override
    public RelatedVideosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_video, parent, false);
        return new RelatedVideosHolder(itemView);
    }

    @Override
    public int getItemCount() {
        if (videoList != null) {
            return videoList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedVideosHolder holder, int position) {
        holder.videoTitle.setText(videoList.get(position).getTitle());
        holder.videoUsername.setText(videoList.get(position).getUsername());
        holder.videoYear.setText(String.valueOf(videoList.get(position).getPubYear()));
        holder.videoDuration.setText(videoList.get(position).getDuration());

        // TODO adjust view/views + use K/M
        String videoViews = videoList.get(position).getViews() + " views";
        holder.videoViews.setText(videoViews);

        // Set the video thumbnail
        String URI = videoList.get(position).getThumbnailUrl();
        Glide.with(holder.videoThumbnail.getContext()).load(URI).into(holder.videoThumbnail);

        holder.videoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoPlayerPage = new Intent(mContext, VideoPlayer.class);
                videoPlayerPage.putExtra("TYPE", mIntent.getStringExtra("TYPE"));
                videoPlayerPage.putExtra("VIDEO", videoList.get(position));
                videoPlayerPage.putExtra("CREDIT", mIntent.getStringExtra("CREDIT"));
                mContext.startActivity(videoPlayerPage);
            }
        });

        holder.videoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.video_menu_pop_up, popupMenu.getMenu());
                popupMenu.show();

                // TODO: implement the video popup menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addToWatchLater_videoMenu:
                                return true;

                            case R.id.addToPlaylist_videoMenu:
                                return true;

                            case R.id.repost_videoMenu:
                                return true;

                            case R.id.report_videoMenu:
                                Intent reportVideo = new Intent(mContext, ReportVideoPopup.class);
                                reportVideo.putExtra("VideoId", videoList.get(position).getVideoId());
                                mContext.startActivity(reportVideo);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    /**
     * Get the first video on the list.
     * @return video
     */
    public Video getFirstFromList() {
        return videoList.get(0);
    }

    /**
     * Related Videos Holder - Inner Class
     */
    public class RelatedVideosHolder extends RecyclerView.ViewHolder {

        public TextView videoTitle, videoUsername, videoYear, videoDuration, videoViews;
        public ImageView videoThumbnail, videoMenu;
        public LinearLayout videoCard;

        public RelatedVideosHolder(View itemView) {
            super(itemView);

            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoUsername = itemView.findViewById(R.id.videoUsername);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoYear = itemView.findViewById(R.id.videoYear);
            videoDuration = itemView.findViewById(R.id.videoTime);
            videoCard = itemView.findViewById(R.id.videoCard);
            videoViews = itemView.findViewById(R.id.videoViews);
            videoMenu = itemView.findViewById(R.id.moreMenu);
        }
    }
}
