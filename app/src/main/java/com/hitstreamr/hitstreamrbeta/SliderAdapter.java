package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context1;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context1) {

        this.context1 = context1;
    }

    //Arrays
    public int[] slide_images =  {

            R.drawable.new_hitstreamr_h_logo_wht,
            R.drawable.new_hitstreamr_h_logo_wht,
            R.drawable.new_hitstreamr_h_logo_wht,
            R.drawable.new_hitstreamr_h_logo_wht
    };

    public String[] slide_headings = {

            "WELCOME!",
            "AD-FREE VIDEOS",
            "BACKGROUND VIDEO PLAY",
            "NO SUBSCRIPTIONS"
    };

    public String[] slide_descriptives = {

            "Enjoy music with more control. Be Empowered.",
            "Watch videos without the interruption of ads.",
            "Close the app, keep the music",
            "Purchase credits, use them on your terms, and enjoy free premium features."
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context1.getSystemService(context1.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideViewImage = (ImageView) view.findViewById(R.id.logo);
        TextView slideViewHeader = (TextView) view.findViewById(R.id.headerText);
        TextView slideViewDescriptive = (TextView) view.findViewById(R.id.descriptive);


        slideViewImage.setImageResource(slide_images[position]);
        slideViewHeader.setText(slide_headings[position]);
        slideViewDescriptive.setText(slide_descriptives[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);


    }
}
