package com.hitstreamr.hitstreamrbeta;

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
import android.widget.TextView;

//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.BillingClientStateListener;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.List;

public class CreditsPurchase extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private Button cancelPurchaseButton, confirmPurchaseButton;
    private TextView creditsAmount;
    private IndicatorSeekBar seekBar;
    private IndicatorStayLayout stayLayout;
    //    private BillingClient billingClient;
    private BillingProcessor billingProcessor;
    private List SKU_list = new ArrayList<String>();

    // Seek Bar Values
    private int currentValue;

    private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwD7XQYkNbvh" +
            "KcrFeCVPsdZDisvYU6DAZsif1XG28uqn7KH7Hdo+yGlmT/+Ch25Vk6BuCzEbLwXkWRAL0ifSV+FgoYFpMEYRPu6" +
            "A6mFsUUgJtbUzwhI1IEl21Pww59OmnXrblXkSrKfWvKxZg7MrB9sAQxdGIUmV+lQ6/COdR3jvYl83mTUdc+KiMM" +
            "PNp64WEiESmjYGtZTVD+C9XPDL0DCDgRQNVsw7qmo+1XGUTejhWyxmha4CLgB8dcxCfBKYOTcdtF82DNt2MbRW2" +
            "XjKbaw6VNwsusHR/xJneC6Pvjd97F7oI/wE9B401mhefkhzkDTK9GsZu34eO0s2HanMIPQIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits_purchase);

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        creditsAmount = findViewById(R.id.credits_amount);

        // Buttons
        cancelPurchaseButton = findViewById(R.id.cancel_credits_button);
        confirmPurchaseButton = findViewById(R.id.confirm_credits_button);

        // Using GitHub dependency
        // Google Play Billing Library
        billingProcessor = new BillingProcessor(this, base64EncodedPublicKey, this);
        billingProcessor.initialize();
        Log.d("BILLING", "PASSES~~~~~~~~~" + currentValue);

        // Seek Bar
        seekBar = findViewById(R.id.seekBar_credits);
        seekBar.setMax(99);
        seekBar.setMin(0);
        seekBar.setIndicatorTextFormat("$ ${PROGRESS}.99");
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                currentValue = seekParams.progress;
                Log.d("SEEKBAR", "~~~~~~~~~ " + currentValue);
                currentValue += 1;
                Log.d("SEEKBAR", "~~~~~~~~~ " + currentValue);
                String productID;
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

                SkuDetails product = billingProcessor.getPurchaseListingDetails(productID);
                Log.d("PRODUCT ID = ", productID);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        // Get SKU List


        // Establish a connection to Google Play
        // Goes in confirmPurchase method?
//        billingClient = BillingClient.newBuilder(getApplicationContext()).build();
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(int responseCode) {
//                if (responseCode == BillingClient.BillingResponse.OK) {
//                    // Query the purchase(?)
//                }
//            }
//
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//        });

//        SkuDetails productID = billingProcessor.getPurchaseListingDetails("credits_0000000" + (currentValue + 1));
//        creditsAmount.setText(productID.title);

        // Get SKU List
        //SKU_list = billingProcessor.getPurchaseListingDetails();

    }

    /**
     * Cancel the purchase session and go back to previous view.
     * @param view view
     */
    public void cancelPurchase(View view) {
        onBackPressed();
    }

    /**
     *
     * @param view view
     */
    public void confirmPurchase(View view) { }

    /**
     *
     * @param productId product ID
     * @param details details
     */
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
    }

    /**
     *
     */
    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    /**
     *
     * @param errorCode error code
     * @param error error
     */
    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    /**
     *
     */
    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }
}
