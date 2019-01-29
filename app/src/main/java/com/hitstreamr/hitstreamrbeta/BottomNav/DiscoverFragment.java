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

        discRecyclerAdapter = new DiscRecyclerView(mImageUrls, mText, getContext(), getActivity().getIntent());

        recyclerView = view.findViewById(R.id.disc_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(discRecyclerAdapter);

    }

    private void initImageBitmaps() {

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_new.png?alt=media&token=47980929-057f-4f44-82d5-8eddfc167c88");
        mText.add("NEWLY ADDED");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_trending.png?alt=media&token=d89e0a20-4e35-4028-99fe-641ec90344a4");
        mText.add("TRENDING NOW");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fartists_to_watch.jpg?alt=media&token=bd2c7fa1-2eb3-42ed-9d23-d01aaf4cbe40");
        mText.add("ARTISTS TO WATCH");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_hiphop_rap.png?alt=media&token=3a13bd73-00de-42fa-89b2-4f0a83c31724");
        mText.add("HIP-HOP/RAP");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_rnb_soul.png?alt=media&token=3e38ebfc-eddd-46da-a0af-884231ed1fba");
        mText.add("R&B/SOUL");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_pop.png?alt=media&token=fb287067-9ca0-4515-acb6-a5969a8ea5ce");
        mText.add("POP");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_indie.png?alt=media&token=0d2ef8cb-be15-4655-8559-3b4e05d7f712");
        mText.add("INDIE/ROCK");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_country.png?alt=media&token=3de8c019-9690-4fcc-892f-ef114339dae9");
        mText.add("COUNTRY");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Ffunk.jpg?alt=media&token=3c23b42a-9a23-4f57-9b92-cfa7ad453326");
        mText.add("SOUL/FUNK");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_latin.png?alt=media&token=61ffbe98-5dd3-423d-acea-f0e13f45d1a8");
        mText.add("LATIN");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_edm.png?alt=media&token=793a4fbe-9085-40de-a801-884dfd22b7ca");
        mText.add("DANCE/ELECTRONIC");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_kpop.jpg?alt=media&token=0e983e80-989d-4efd-a9c5-7b2b89e62a94");
        mText.add("K-POP");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_folk.png?alt=media&token=f29928b8-6155-4ca4-a4ba-34b1cfaa577e");
        mText.add("FOLK");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_reggae.png?alt=media&token=a57852be-76b5-470f-89d4-f3d44415a46c");
        mText.add("REGGAE/AFRO");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fgospelchoir.jpg?alt=media&token=889c385f-6bbb-4118-a6e2-c4a320c67de9");
        mText.add("GOSPEL/INSPIRATIONAL");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_blues.png?alt=media&token=3a61cd5e-ed07-468d-b103-089272ec88e6");
        mText.add("JAZZ/BLUES");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_alternative.png?alt=media&token=baa7b206-e45f-4c4b-9586-30387d9b81f7");
        mText.add("ALTERNATIVE");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_new_age.png?alt=media&token=d579372a-140a-48d5-b7b7-9e332bb343b8");
        mText.add("NEW AGE");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/discoverImages%2Fw_genre_opera.png?alt=media&token=33110c32-f564-479c-bb94-56d68ba6e0d2" +
                "");
        mText.add("OPERA");
    }

}
