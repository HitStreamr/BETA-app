package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.SeekBar;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;

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
        seekBar.setMax(100);
        seekBar.setTickCount(10);
        seekBar.setIndicatorTextFormat("$ ${PROGRESS}");
    }


}
