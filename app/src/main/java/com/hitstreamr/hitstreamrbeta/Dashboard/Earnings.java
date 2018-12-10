package com.hitstreamr.hitstreamrbeta.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitstreamr.hitstreamrbeta.R;

public class Earnings extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_earnings, container, false);

        return rootView;
    }
}
