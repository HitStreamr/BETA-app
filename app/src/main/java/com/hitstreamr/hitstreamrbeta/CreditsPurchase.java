package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.Purchase;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;

import java.util.ArrayList;

public class CreditsPurchase extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Credits Purchase";
    private IndicatorSeekBar seekBar;
    private IndicatorStayLayout stayLayout;


    private int currentValue = 0;

    private Button ConfirmBtn;
    private int responseCode;

    public String creditvalue;
    private MyItemClickListener mListener;


    private ArrayList<String> mCreditVal = new ArrayList<>();
    private ArrayList<String> mCreditPrice = new ArrayList<>();
    private ArrayList<String> mDescription = new ArrayList<>();
    private ArrayList<String> mProductId = new ArrayList<>();
    private RecyclerView recyclerView;
    private CreditsRCVAdapter creditAdapter;

    private String purchaseProductId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits_purchase);

        recyclerView = findViewById(R.id.creditsRCV);

        mListener = new MyItemClickListener() {
            @Override
            public void onCreditsSelected(String sProductId, String sCreditValue) {
                creditvalue = sCreditValue;
                purchaseProductId = sProductId;
            }

        };

        getCredtiVal();

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width), (int) (height));
        getWindow().setBackgroundDrawable(new ColorDrawable(0x4b000000));

        ConfirmBtn = findViewById(R.id.confirm_credits_button);
        ConfirmBtn.setOnClickListener(this);

    }

        public interface MyItemClickListener {
           void onCreditsSelected(String sCreditId, String sCreditValue);
        }



    /**
     * Cancel the purchase session and go back to previous view.
     * @param view view
     */
    public void cancelPurchase(View view) {
        onBackPressed();
    }

     private void makePurchase() {

        Intent newPurchaseIntent = new Intent(getApplicationContext(), BillingManager.class);
        newPurchaseIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
        newPurchaseIntent.putExtra("CREDIT", creditvalue);
         newPurchaseIntent.putExtra("PRDTID", purchaseProductId);

         startActivity(newPurchaseIntent);

    }
    public void onClick(View view) {
        if (view == ConfirmBtn) {
            if( creditvalue != null) {
                makePurchase();
            }
            else{
                Toast.makeText(getApplicationContext(), "Please select a credit value", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    private void getCredtiVal() {

        mCreditVal.add("35");
        mCreditPrice.add("$.99");
        mDescription.add("Lasts 1 - 3 Days");
        mProductId.add("credits_0000000001");

        mCreditVal.add("90");
        mCreditPrice.add("$1.99");
        mDescription.add("Lasts 3-7 Days");
        mProductId.add("credits_0000000002");

        mCreditVal.add("140");
        mCreditPrice.add("$2.99");
        mDescription.add("Lasts about 10 Days");
        mProductId.add("credits_0000000003");

        mCreditVal.add("190");
        mCreditPrice.add("$3.99");
        mDescription.add("Lasts about 2 Weeks");
        mProductId.add("credits_0000000004");

        mCreditVal.add("240");
        mCreditPrice.add("$4.99");
        mDescription.add("Lasts about 2.5 Weeks");
        mProductId.add("credits_0000000005");

        mCreditVal.add("290");
        mCreditPrice.add("$5.99");
        mDescription.add("Lasts about 3 Weeks");
        mProductId.add("credits_0000000006");

        mCreditVal.add("345");
        mCreditPrice.add("$6.99");
        mDescription.add("Lasts 1 Month");
        mProductId.add("credits_0000000007");

        mCreditVal.add("705");
        mCreditPrice.add("$13.99");
        mDescription.add("Last 2 Months\n+15 FREE CREDITS");
        mProductId.add("credits_0000000014");

        mCreditVal.add("1075");
        mCreditPrice.add("$20.99");
        mDescription.add("Lasts 3 Months\n+40 FREE CREDITS");
        mProductId.add("credits_0000000021");

        mCreditVal.add("2180");
        mCreditPrice.add("$41.99");
        mDescription.add("Lasts 6 Months\n+110 FREE CREDITS");
        mProductId.add("credits_0000000042");

        mCreditVal.add("3300");
        mCreditPrice.add("$62.99");
        mDescription.add("Lasts 9 Months\n+195 FREE CREDITS");
        mProductId.add("credits_0000000063");

        mCreditVal.add("4375");
        mCreditPrice.add("$83.99");
        mDescription.add("Lasts 12 Months\n+235 FREE CREDITS");
        mProductId.add("credits_0000000084");

       initRCV();

    }

    private void initRCV(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        creditAdapter = new CreditsRCVAdapter(this, mCreditVal, mCreditPrice,mDescription, mProductId, mListener);
        creditAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(creditAdapter);
    }
}
