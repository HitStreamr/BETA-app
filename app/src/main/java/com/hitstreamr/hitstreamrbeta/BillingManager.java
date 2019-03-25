package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
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

public class BillingManager extends AppCompatActivity  implements BillingProcessor.IBillingHandler, View.OnClickListener {

    private static final String TAG = "BillingManager";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private Button confirmBtn, cancelBtn;

    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "com.hitstreamr.hitsreamrbeta";
    private static final String LICENSE_KEY ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwD7XQYkNbvhKcrFeCVPsdZDisvYU6DAZsif1XG28uqn7KH7Hdo+yGlmT/+Ch25Vk6BuCzEbLwXkWRAL0ifSV+FgoYFpMEYRPu6A6mFsUUgJtbUzwhI1IEl21Pww59OmnXrblXkSrKfWvKxZg7MrB9sAQxdGIUmV+lQ6/COdR3jvYl83mTUdc+KiMMPNp64WEiESmjYGtZTVD+C9XPDL0DCDgRQNVsw7qmo+1XGUTejhWyxmha4CLgB8dcxCfBKYOTcdtF82DNt2MbRW2XjKbaw6VNwsusHR/xJneC6Pvjd97F7oI/wE9B401mhefkhzkDTK9GsZu34eO0s2HanMIPQIDAQAB";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;
    BillingProcessor bp;
    private boolean readyToPurchase = false;

    private String userID;
    private String newCreditvalue;
    private int newCreditVal =0;
    private String currentCredit;
    private String purchaseProductId;
    private String sMessage;
    private TextView sMessageId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_purchase);



        newCreditvalue = getIntent().getStringExtra("CREDIT");
        Log.e("Billing "," inside credit value "+newCreditvalue);

        purchaseProductId = getIntent().getStringExtra("PRDTID");
        Log.e("Billing "," inside product id "+purchaseProductId);


        sMessageId = (TextView)findViewById(R.id.MessageText);
        sMessageId.setText(newCreditvalue +" Credits");

        //Button
        confirmBtn = findViewById(R.id.confirm);
        confirmBtn.setOnClickListener(this);

        cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(this);

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
//        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getWindow().setLayout((int) (width), (int) (height));
        getWindow().setBackgroundDrawable(new ColorDrawable(0x4b000000));



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


    private void makePurchase() {
        bp = new BillingProcessor(this, LICENSE_KEY, this);
        bp.initialize();
    }


    private void confirmPurchase() {

        if (!Strings.isNullOrEmpty(currentCredit)) {
            int previouscredits = Integer.parseInt(currentCredit);
            if (previouscredits > 0) {
                newCreditVal = previouscredits + (Integer.parseInt(newCreditvalue));
                newCreditvalue = String.valueOf(newCreditVal);
                updateCredits();
            }
        } else {
            createCredits();
        }
    }

    @Override
    public void onBillingInitialized() {
        boolean isAvailable = bp.isIabServiceAvailable(this);
        Log.e("billing service ","isIabServiceAvailable->" +isAvailable);
        SkuDetails sku =  bp.getPurchaseListingDetails(purchaseProductId);
        String price =sku.priceText;
        String Title = sku.title;
        boolean ispurchase = bp.purchase(this, purchaseProductId);
      //  Log.e("Billing","ispurchase->" +ispurchase);


    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(this, "Credits Purchased Successfully", Toast.LENGTH_SHORT).show();
        confirmPurchase();

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /* public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, List purchases) {
         if (responseCode == BillingClient.BillingResponse.OK
                 && purchases != null) {
             for (Purchase purchase : purchases) {
                // handlePurchase(purchase);
             }
         } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
             // Handle an error caused by a user cancelling the purchase flow.
         } else {
             // Handle any other error codes.
         }
     }*/
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
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
                        Toast.makeText(BillingManager.this, "You now have " +newCreditvalue + " credits", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(BillingManager.this, "You now have " +newCreditvalue + " credits", Toast.LENGTH_SHORT).show();
                    }
                });
        finish();

    }


    public void onClick(View view) {
        if(view == confirmBtn ){
            makePurchase();
        }
        else if(view == cancelBtn){
            super.onBackPressed();
        }
    }

}
