package com.hitstreamr.hitstreamrbeta;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BillingManager extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BillingManager";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private Button confirmBtn, cancelBtn;

    private String userID;
    private String newCreditvalue = "90";
    private int newCreditVal =0;
    private String currentCredit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_purchase);

        //Button
        confirmBtn = findViewById(R.id.confirm);
        confirmBtn.setOnClickListener(this);

        cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .4));



        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //Checking for User credits
        FirebaseDatabase.getInstance().getReference("Credits")
                .child(userID).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentCredit = dataSnapshot.getValue(String.class);
                        Log.e(TAG, "Your credit val " + currentCredit);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void confirmPurchase() {
        if (!Strings.isNullOrEmpty(currentCredit)) {
            int previouscredits = Integer.parseInt(currentCredit);
            if (previouscredits > 0) {
                newCreditVal = previouscredits + (Integer.parseInt(newCreditvalue));
                Log.e(TAG, "New credit val " + newCreditVal);
                newCreditvalue = String.valueOf(newCreditVal);
                Log.e(TAG, "New credit val after conversion" + newCreditvalue);
                updateCredits();
            }
        } else {
            Log.e(TAG, "inside else to create credits " + newCreditvalue);
            createCredits();
        }
    }

    private void updateCredits(){
        FirebaseDatabase.getInstance()
                .getReference("Credits")
                .child(userID)
                .child("creditvalue")
                .setValue(newCreditvalue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BillingManager.this, "Your account is  credited with " +newCreditvalue + " credits", Toast.LENGTH_SHORT).show();
                    }
                });
        finish();


    }
    private void createCredits(){
        FirebaseDatabase.getInstance()
                .getReference("Credits")
                .child(userID)
                .child("creditvalue")
                .setValue(newCreditvalue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BillingManager.this, "Your account is  credited with " +newCreditvalue + " credits", Toast.LENGTH_SHORT).show();
                    }
                });
        finish();

    }


    public void onClick(View view) {
        if(view == confirmBtn ){
            confirmPurchase();
        }
        else if(view == cancelBtn){
            super.onBackPressed();
        }
    }

}
