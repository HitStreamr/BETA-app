package com.hitstreamr.hitstreamrbeta.Dashboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hitstreamr.hitstreamrbeta.R;

public class Earnings extends Fragment {

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
        Task<DocumentSnapshot> queryRef = firebaseFirestore.collection("ArtistsEarnings").document(current_user.getUid()).get();

        queryRef.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()) {
                    DocumentSnapshot document = task.getResult();

                    viewCount = new Long(document.get("views").toString());
                    totalEarn = new Double(document.get("totearnings").toString());
                    artistbal = new Double(document.get("balance").toString());
                    myEarnings = new Double(document.get("myearnings").toString());
                    myContribution = new Double(document.get("myContributions").toString());

                }

                videoViewCount.setText(String.valueOf(viewCount));
                //totalEarn = Math.round(totalEarn*100)/100.00;
                artistEarnings.setText(String.valueOf(totalEarn));
                artistBalance.setText(String.valueOf(artistbal));
                artistSongs.setText(String.valueOf(myEarnings));
                artistContribution.setText(String.valueOf(myContribution));
            }
        });
    }
}
