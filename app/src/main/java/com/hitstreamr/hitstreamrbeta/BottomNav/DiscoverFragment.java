package com.hitstreamr.hitstreamrbeta.BottomNav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitstreamr.hitstreamrbeta.DiscRecyclerView;
import com.hitstreamr.hitstreamrbeta.R;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment {

    //vars
    private ArrayList<String> mText = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    RecyclerView recyclerView;
    DiscRecyclerView discRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initImageBitmaps();

        discRecyclerAdapter = new DiscRecyclerView(mImageUrls, mText, getContext());

        recyclerView = view.findViewById(R.id.disc_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(discRecyclerAdapter);

    }

    private void initImageBitmaps() {

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("NEWLY ADDED");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_trending.png?alt=media&token=d89e0a20-4e35-4028-99fe-641ec90344a4");
        mText.add("TRENDING NOW");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_hiphop_rap.png?alt=media&token=3a13bd73-00de-42fa-89b2-4f0a83c31724");
        mText.add("HIP-HOP/R&B");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("Newly Added");
    }

}
