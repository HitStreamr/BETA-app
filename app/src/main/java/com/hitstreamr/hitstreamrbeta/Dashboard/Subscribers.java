package com.hitstreamr.hitstreamrbeta.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitstreamr.hitstreamrbeta.R;

public class Subscribers extends Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_subscribers, container, false);

        return rootView;
    }
}
