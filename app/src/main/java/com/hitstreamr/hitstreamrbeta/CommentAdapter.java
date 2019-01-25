package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private List<Comment> commentList;
    private CommentPage.ItemClickListener mListener;
    private Video video;

    /**
     * Constructor
     */
    public CommentAdapter(List<Comment> commentList, CommentPage.ItemClickListener mListener, Video video) {
        this.commentList = commentList;
        this.mListener = mListener;
        this.video = video;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_comment_layout, parent, false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentHolder holder, int position) {
        // Get the username
        holder.username.setText(commentList.get(position).getUsername());

        // Get the message
        holder.message.setText(commentList.get(position).getMessage());

        // Set the timestamp difference
        String timestampDifference = getTimestampDifference(commentList.get(position));
        if (!timestampDifference.equals("0")) {
            // TODO: add more detailed time difference
            holder.commentTimeStamp.setText(timestampDifference + " d");
        } else {
            holder.commentTimeStamp.setText("today");
        }

        // Set the profile picture
        FirebaseStorage.getInstance().getReference("profilePictures").child(commentList.get(position).getUserID())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Glide.with(holder.circleImageView.getContext()).load(uri).into(holder.circleImageView);
                }
            }
        });

        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.getCorrespondingCommentInfo(commentList.get(position));
            }
        });

        holder.show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = (Integer) holder.show_hide.getTag();
                if (status == 1) {
                    holder.show_hide.setText("Hide Replies ");
                    holder.show_hide.setTag(0);
                    holder.recyclerView_reply.setVisibility(View.VISIBLE);

                    Comment comment = commentList.get(position);
                    List<Comment> replyList = new ArrayList<>();
                    CommentReplyAdapter replyAdapter = new CommentReplyAdapter(replyList);
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                    DatabaseReference databaseReference = firebaseDatabase.getReference("CommentReplies")
                            .child(video.getVideoId()).child(comment.getCommentID());
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Comment reply = dataSnapshot.getValue(Comment.class);
                            replyList.add(reply);
                            replyAdapter.notifyDataSetChanged();
                            holder.recyclerView_reply.setAdapter(replyAdapter);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    holder.show_hide.setText("Show Replies ");
                    holder.show_hide.setTag(1);
                    holder.recyclerView_reply.setVisibility(View.GONE);
                }
            }
        });

        // Get the replies count for each comment
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("CommentReplies")
                .child(video.getVideoId()).child(commentList.get(position).getCommentID());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.replyCount.setText("(" + dataSnapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (commentList != null) {
            return commentList.size();
        }
        return 0;
    }

    /**
     * Comment Holder - Inner Class
     */
    public class CommentHolder extends RecyclerView.ViewHolder {
        public TextView username, message, replyCount, show_hide, commentTimeStamp;
        public CircleImageView circleImageView;
        public Button replyButton;
        public LinearLayout showMoreReplies;
        public RecyclerView recyclerView_reply;

        public CommentHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username_commentList);
            message = view.findViewById(R.id.message_commentList);
            circleImageView = view.findViewById(R.id.profilePicture_commentList);
            replyButton = view.findViewById(R.id.replyButton);
            showMoreReplies = view.findViewById(R.id.showMoreReply);
            replyCount = view.findViewById(R.id.replyCount);
            commentTimeStamp = view.findViewById(R.id.commentTimeStamp);

            show_hide = view.findViewById(R.id.viewReply);
            show_hide.setTag(1);
            show_hide.setText("Show Replies ");

            recyclerView_reply = view.findViewById(R.id.postedReplies);
            recyclerView_reply.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }
    }

    /**
     * Calculate the time difference between when a comment is posted and the present time.
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

        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            difference = "0";
        } catch (NullPointerException e) {
            difference = "null";
        }
        return difference;
    }
}
