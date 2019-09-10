package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPage extends AppCompatActivity {

    private String accountType, username, replyToCommentID;
    private FirebaseUser current_user;
    private Uri photoURL;
    private Video vid;
    private RecyclerView recyclerView_comment;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private ItemClickListener mListener;
    private boolean isReplyingTo = false;

    private TextView replyingToDisplay;
    private Button cancelReplyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        vid = getIntent().getParcelableExtra("VIDEO");

        // Profile Picture
        if (current_user.getPhotoUrl() != null) {
            CircleImageView circleImageView_comment = findViewById(R.id.profilePicture_comment);
            photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView_comment);
        }

        getUserType();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(accountType)
                .child(current_user.getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(eventListener);

        mListener = new ItemClickListener() {
            @Override
            public void getCorrespondingCommentInfo(Comment comment) {
                TextView mCommentMessage = findViewById(R.id.commentMessage);
                String replyingTo = "@" + comment.getUsername() + " ";
                mCommentMessage.setText(replyingTo);
                isReplyingTo = true;
                replyToCommentID = comment.getCommentID();

                replyingToDisplay.setText("Replying to " + "@" + comment.getUsername());
                replyingToDisplay.setVisibility(View.VISIBLE);
                cancelReplyButton.setVisibility(View.VISIBLE);

                cancelReplyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCommentMessage.setText("");
                        isReplyingTo = false;
                        replyingToDisplay.setVisibility(View.GONE);
                        cancelReplyButton.setVisibility(View.GONE);
                    }
                });

            }
        };

        loadComments();

        replyingToDisplay = findViewById(R.id.replyingToDisplay);
        replyingToDisplay.setVisibility(View.GONE);
        cancelReplyButton = findViewById(R.id.cancelReplyButton);
        cancelReplyButton.setVisibility(View.GONE);
    }

    /**
     * Go back to VideoPlayer page.
     * @param view view
     */
    public void onBack(View view) {
        onBackPressed();
    }

    /**
     * Check if it is a new comment/reply.
     * @param view view
     */
    public void onPostingCommentReply(View view) {
        if (isReplyingTo) {
            onPostReply(view);
        } else {
            onPostComment(view);
        }
    }

    /**
     * Post and store a comment to the database.
     * @param view view
     */
    public void onPostComment(View view) {
        // Get user info + comment
        TextView mCommentMessage = findViewById(R.id.commentMessage);
        String commentMessage = mCommentMessage.getText().toString();

        if (commentMessage.isEmpty()) {
            return;
        }

        String userID = current_user.getUid();
        String photoURI = photoURL.toString();

        // Get the commentID before adding to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(vid.getVideoId());
        String commentID = databaseReference.push().getKey();

        // Create the comment object to store
        Comment comment_object = new Comment(username, commentMessage, userID, getTimestamp(), commentID);

        // Add to the database
        databaseReference.child(commentID).setValue(comment_object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mCommentMessage.setText("");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentPage.this, "Failed to post. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Get the date and time of posting.
     * @return date and time
     */
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return sdf.format(new Date());
    }

    /**
     * Get the account type of the current user
     */
    private void getUserType() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                accountType = "BasicAccounts";
            } else if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                accountType = "ArtistAccounts";
            } else {
                accountType = "LabelAccounts";
            }
        }
    }

    /**
     * Load and show comment(s) for the corresponding music video from the database.
     */
    public void loadComments() {
        recyclerView_comment = findViewById(R.id.postedComments_recyclerView);
        recyclerView_comment.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, mListener, vid);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Comments").child(vid.getVideoId());
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                commentList.add(comment);
                commentAdapter.notifyDataSetChanged();
                recyclerView_comment.setAdapter(commentAdapter);
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

    /**
     * Post a reply and store to the database.
     * @param view view
     */
    public void onPostReply(View view) {
        TextView mCommentMessage = findViewById(R.id.commentMessage);
        String commentMessage = mCommentMessage.getText().toString();

        String userID = current_user.getUid();
        String photoURI = photoURL.toString();

        // Get the commentID before adding to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CommentReplies")
                .child(vid.getVideoId()).child(replyToCommentID);
        String commentID = databaseReference.push().getKey();

        // Create the comment object to store
        Comment comment_object = new Comment(username, commentMessage, userID, getTimestamp(), commentID);

        // Add to the database
        databaseReference.child(commentID).setValue(comment_object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mCommentMessage.setText("");
                isReplyingTo = false;
                replyingToDisplay.setVisibility(View.GONE);
                cancelReplyButton.setVisibility(View.GONE);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CommentPage.this, "Failed to post. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Interface
     */
    public interface ItemClickListener {
        void getCorrespondingCommentInfo(Comment comment);
    }
}
