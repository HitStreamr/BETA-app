package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.ReplyHolder> {

    private List<Comment> replyList;

    /**
     * Constructor
     */
    public CommentReplyAdapter(List<Comment> replyList) { this.replyList = replyList; }

    @Override
    public void onBindViewHolder(@NonNull ReplyHolder holder, int position) {
        holder.username.setText(replyList.get(position).getUsername());
        holder.message.setText(replyList.get(position).getMessage());

        // Set the profile picture
        FirebaseStorage.getInstance().getReference("profilePictures").child(replyList.get(position).getUserID())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Glide.with(holder.circleImageView.getContext()).load(uri).into(holder.circleImageView);
                }
            }
        });

        // Set the timestamp difference
        String timestampDifference = getTimestampDifference(replyList.get(position));
        if (!timestampDifference.equals("0")) {
            // TODO: if needed, add more detailed time difference
            holder.replyTimeStamp.setText(timestampDifference + "d");
        } else {
            holder.replyTimeStamp.setText("today");
        }
    }

    @NonNull
    @Override
    public ReplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_comment_reply_layout, parent, false);
        return new ReplyHolder(itemView);
    }

    @Override
    public int getItemCount() {
        if (replyList != null) {
            return replyList.size();
        }
        return 0;
    }

    /**
     * Reply Holder - Inner Class
     */
    public class ReplyHolder extends RecyclerView.ViewHolder {
        public TextView username, message, replyTimeStamp;
        public CircleImageView circleImageView;

        public ReplyHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_comment_reply);
            message = itemView.findViewById(R.id.message_comment_reply);
            circleImageView = itemView.findViewById(R.id.profilePicture_commentReply);
            replyTimeStamp = itemView.findViewById(R.id.replyTimeStamp);
        }
    }

    /**
     * Calculate the time difference between when a reply is posted and the present time.
     * @return the time difference
     */
    private String getTimestampDifference(Comment comment) {
        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getTimePosted();

        if (photoTimestamp != null) {
            try {
                timestamp = sdf.parse(photoTimestamp);
                difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
            } catch (ParseException e) {
                difference = "0";
            }
        }
        return difference;
    }
}
