package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class CreditsPurchase extends AppCompatActivity {
    private IndicatorSeekBar seekBar;
    private IndicatorStayLayout stayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits_purchase);

        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width), (int) (height * .8));

        seekBar = findViewById(R.id.seekBar_credits);
        seekBar.setMax(99);
        seekBar.setMin(0);
        seekBar.setIndicatorTextFormat("$ ${PROGRESS}.99");

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

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

}
