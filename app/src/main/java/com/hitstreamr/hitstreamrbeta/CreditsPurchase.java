package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.android.billingclient.api.BillingClient;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.List;

public class CreditsPurchase extends AppCompatActivity implements BillingProcessor.IBillingHandler, View.OnClickListener {
    private static final String TAG = "Credits Purchase";
    private IndicatorSeekBar seekBar;
    private IndicatorStayLayout stayLayout;

    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "com.hitstreamr.hitsreamrbeta";
   // private static final String SUBSCRIPTION_ID = "com.anjlab.test.iab.subs1";
    private static final String LICENSE_KEY ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwD7XQYkNbvhKcrFeCVPsdZDisvYU6DAZsif1XG28uqn7KH7Hdo+yGlmT/+Ch25Vk6BuCzEbLwXkWRAL0ifSV+FgoYFpMEYRPu6A6mFsUUgJtbUzwhI1IEl21Pww59OmnXrblXkSrKfWvKxZg7MrB9sAQxdGIUmV+lQ6/COdR3jvYl83mTUdc+KiMMPNp64WEiESmjYGtZTVD+C9XPDL0DCDgRQNVsw7qmo+1XGUTejhWyxmha4CLgB8dcxCfBKYOTcdtF82DNt2MbRW2XjKbaw6VNwsusHR/xJneC6Pvjd97F7oI/wE9B401mhefkhzkDTK9GsZu34eO0s2HanMIPQIDAQAB";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;

    BillingProcessor bp;
    private boolean readyToPurchase = false;
    private int currentValue = 0;

    private Button ConfirmBtn;
    private int responseCode;

    public String creditvalue = "90";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits_purchase);

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
//        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getWindow().setLayout((int) (width), (int) (height));
        getWindow().setBackgroundDrawable(new ColorDrawable(0x4b000000));


        seekBar = findViewById(R.id.seekBar_credits);
        seekBar.setMax(99);
        seekBar.setIndicatorTextFormat("$ ${PROGRESS}.99");

        ConfirmBtn = findViewById(R.id.confirm_credits_button);
        ConfirmBtn.setOnClickListener(this);


        // bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
        bp = new BillingProcessor(this, LICENSE_KEY, this);
        bp.initialize();
        boolean isAvailable = bp.isIabServiceAvailable(this);

        Log.e("billing service ","isIabServiceAvailable->" +isAvailable);
       // SkuDetails sku =  bp.getPurchaseListingDetails("credits_00000002");


        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                currentValue = seekParams.progress;
                currentValue += 1;
                String productID = "";
                // From $0 to $8
                if (currentValue < 9) {
                    productID = "credits_0000000" + currentValue;
                }
                // From $9 to $98
                else if (currentValue >= 9 && currentValue <= 98) {
                    productID = "credits_000000" + currentValue;

                }
                // For $99
                else {
                    productID = "credits_00000" + currentValue;
                }
                Log.e("seek bar ","product id ->" +productID);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

    }

    /**
     * Cancel the purchase session and go back to previous view.
     * @param view view
     */
    public void cancelPurchase(View view) {
        onBackPressed();
    }

    @Override
    public void onBillingInitialized() {

        Log.e("sku","Inside billing init" );
        SkuDetails sku =  bp.getPurchaseListingDetails("credits_00000002");
        String price = sku.priceText;
        String Title = sku.title;
        Log.e("sku","price->" +price);
        Log.e("sku","Title->" +Title);
       // boolean ispurchase = bp.purchase(this, "credits_00000002");
        //Log.e("Billing","ispurchase->" +ispurchase);


    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(CreditsPurchase.this, "Credits Purchased Successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(CreditsPurchase.this, "Error Occured", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void makePurchase() {

        Intent newPurchaseIntent = new Intent(getApplicationContext(), BillingManager.class);
        //newPurchaseIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        startActivity(newPurchaseIntent);

    }
    public void onClick(View view) {
        if (view == ConfirmBtn) {
            makePurchase();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

   /* public void onPurchasesUpdated(@BillingResponse int responseCode, List purchases) {
        if (responseCode == BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
               // handlePurchase(purchase);
            }
        } else if (responseCode == BillingResponse.USER_CANCELED) {
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
}
