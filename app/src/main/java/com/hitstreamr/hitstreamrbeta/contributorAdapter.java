package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class contributorAdapter extends ArrayAdapter<Contributor> {
    private static final String TAG = "contributorAdapter";

    private Context mContext;
    private int mResource;
    private ArrayList<Contributor> objects1 =new ArrayList<>();
    deleteinterface deleteInter;

    public contributorAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contributor> objects, deleteinterface deleteInter) {
        super(context, resource, objects);
        this.mContext =  context;
        this.mResource = resource;
        this.objects1 = objects;
        this.deleteInter = deleteInter;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Contributor Contributors = getItem(position);
        Log.e("TAG", "object values"+objects1.toString());
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        Contributor currentContributors = objects1.get(position);

        Log.e("TAG", "object current values"+currentContributors.toString());
        TextView TextViewContributorName = convertView.findViewById(R.id.firstLine);
        TextView TextViewContributorPercentage = convertView.findViewById(R.id.thirdLine);
        TextView TextViewContributorType = convertView.findViewById(R.id.secondLine);
        CircleImageView contributorProfilePicture = convertView.findViewById(R.id.icon);

        Button deleteContributorBtn = convertView.findViewById(R.id.deleteContributor);
        deleteContributorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInter.deleteposition(position);

            }
        });

        TextViewContributorName.setText(currentContributors.getContributorName());
        TextViewContributorPercentage.setText(currentContributors.getContributorPercentage());
        TextViewContributorType.setText(currentContributors.getContributorType());

        // Get contributor's profile picture
        String username = currentContributors.getContributorName();
        FirebaseDatabase.getInstance().getReference("UsernameUserId").child(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId = dataSnapshot.child("tempUserId").getValue().toString();
                            FirebaseStorage.getInstance().getReference("profilePictures").child(userId)
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        Glide.with(mContext).load(uri).into(contributorProfilePicture);
                                    }
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // TODO: handle error
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return convertView;
    }
    public interface deleteinterface{
        void deleteposition(int deletePosition);

    }

}
