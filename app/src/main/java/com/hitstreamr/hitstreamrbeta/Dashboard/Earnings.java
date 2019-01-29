package com.hitstreamr.hitstreamrbeta.Dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.R;
import com.hitstreamr.hitstreamrbeta.Video;

public class Earnings extends Fragment{

    private FirebaseUser current_user;
    private Long viewCount = new Long(0);
    private Double totalEarn = new Double(0);
    private Double artistbal = new Double(0);
    private Double myEarnings = new Double(0);
    private Double myContribution = new Double(0);


    public final String TAG = "Earnings";
    private TextView videoViewCount,artistEarnings, artistBalance, artistSongs,artistContribution;
    private String artistuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_earnings, container, false);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        videoViewCount = view.findViewById(R.id.viewCount);
        artistEarnings = view.findViewById(R.id.earnings);
        artistBalance = view.findViewById(R.id.totalBalance);
        artistSongs = view.findViewById(R.id.mySongs);
        artistContribution = view.findViewById(R.id.myContribution);
        artistuser = current_user.getUid();

        calculateEarnings();


    }

    private void calculateEarnings(){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query queryRef = firebaseFirestore.collection("ArtistsEarnings");

        queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.exists()) {
                        if (artistuser.contains(document.getId())) {
                            viewCount = new Long(document.get("views").toString());
                            totalEarn = new Double(document.get("totearnings").toString());
                            artistbal = new Double(document.get("balance").toString());
                            myEarnings = new Double(document.get("myearnings").toString());
                            myContribution = new Double(document.get("myContributions").toString());

                        }
                    }
                }
                videoViewCount.setText(String.valueOf(viewCount));
                totalEarn = Math.round(totalEarn*100)/100.00;
                artistEarnings.setText(String.valueOf(totalEarn));
                artistBalance.setText(String.valueOf(artistbal));
                artistSongs.setText(String.valueOf(myEarnings));
                artistContribution.setText(String.valueOf(myContribution));
            }
        });
    }
}
