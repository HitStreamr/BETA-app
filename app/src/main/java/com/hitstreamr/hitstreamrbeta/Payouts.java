package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Payouts extends AppCompatActivity {

    private FirebaseUser current_user;
    public final String TAG = "Earnings";
    private ArrayList<Pay> paymentdtls;
    private RecyclerView payoutdetailsview;
    private PayoutAdapter payAdapter;
    private TextView noHistrory;


   /* public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_payouts, container, false);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        getPayoutDetails(rootView);
        return rootView;
    }*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payouts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        // Set toolbar profile picture to always be the current user
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user.getPhotoUrl() != null) {
            CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
        }

        noHistrory = findViewById(R.id.noHistory);
        paymentdtls = new ArrayList<>();
        payoutdetailsview = (RecyclerView) findViewById(R.id.payoutsRCV);
        payoutdetailsview.setLayoutManager(new LinearLayoutManager(this));
        getPayoutDetails();

    }

    private void getPayoutDetails(){

        FirebaseDatabase.getInstance().getReference("ArtistPayouts").child(current_user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for(DataSnapshot each : dataSnapshot.getChildren()) {
                                //Pay payment = each.getValue(Pay.class);
                                Pay payment = new Pay();
                                payment.setPayDate(each.getKey().toString());
                                payment.setPaymentAmt(each.child("Payment").getValue().toString());
                                paymentdtls.add(payment);
                            }
                            callAdapter();
                        }
                        else{
                            noHistrory.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void callAdapter(){
        payAdapter = new PayoutAdapter(paymentdtls);
        payAdapter.notifyDataSetChanged();
        payoutdetailsview.setAdapter(payAdapter);
    }

    /**
     * Handles toolbar's back button.
     * @return true if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
