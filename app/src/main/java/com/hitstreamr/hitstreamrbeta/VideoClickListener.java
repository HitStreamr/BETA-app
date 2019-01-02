package com.hitstreamr.hitstreamrbeta;

import android.view.View;

public interface VideoClickListener {
    void onResultClick(Video video);
    void onOverflowClick(Video video, View v);
}
