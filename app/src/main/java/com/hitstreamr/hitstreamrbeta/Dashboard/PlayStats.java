package com.hitstreamr.hitstreamrbeta.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitstreamr.hitstreamrbeta.R;

public class PlayStats extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_playstats, container, false);

        return rootView;
    }

    /**
     * Get the weekly total number of views.
     * @param view view
     */
    private void getWeeklyViews(View view) {
        TextView weeklyViews = view.findViewById(R.id.weekly_total_views);
    }
}
