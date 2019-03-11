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

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_new.png?alt=media&token=1662140a-9893-4a79-bd00-035edffb4267");
        mText.add("NEWLY ADDED");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_trending.png?alt=media&token=20ae522b-720d-47a0-ad44-77ad7708ea02");
        mText.add("TRENDING NOW");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2F2019-CARDI-B-EP-665X374-4042156030.jpg?alt=media&token=02c8947f-c347-42fd-9cb7-ad5f78f198bd");
        mText.add("ARTISTS TO WATCH");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_hiphop_rap.png?alt=media&token=3618fd22-2e38-4b92-bb1a-631565127744");
        mText.add("HIP-HOP/RAP");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_rnb_soul.png?alt=media&token=a46317da-48cd-4812-9020-b9dcea518953");
        mText.add("R&B/SOUL");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_pop.png?alt=media&token=828b7b46-9e94-4d86-a23d-7db2b52d2c50");
        mText.add("POP");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_indie.png?alt=media&token=f46a2551-0422-4c4d-bdc3-47d8fed7a772");
        mText.add("INDIE");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_indie.png?alt=media&token=f46a2551-0422-4c4d-bdc3-47d8fed7a772");
        mText.add("ROCK");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_country.png?alt=media&token=16eee7ec-b183-4f5c-b03e-4033593ad5dd");
        mText.add("COUNTRY");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_funk.png?alt=media&token=2bb07ea3-93ec-4770-b644-4e72b4594d9a");
        mText.add("FUNK");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_latin.png?alt=media&token=95bc2b97-c52f-4d76-8855-6d46604f2512");
        mText.add("LATIN");

        // EDM
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_edm.png?alt=media&token=6ff37003-2ee5-43ac-8738-c673824d00ec");
        mText.add("DANCE/ELECTRONIC");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_asian_pop.png?alt=media&token=acf3208e-7aa4-4e9d-a897-fb386f01d24d");
        mText.add("K-POP");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_folk.png?alt=media&token=19f3f761-b808-4134-b2d2-edc5d2919dda");
        mText.add("FOLK");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_afro.png?alt=media&token=b4de5e78-26ef-4cf8-92a3-1d04f28be284");
        mText.add("REGGAE");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_afro.png?alt=media&token=b4de5e78-26ef-4cf8-92a3-1d04f28be284");
        mText.add("AFRO");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fnot-ashamed-of-the-gospel-bujdcfub-e2250b5dc9f8f2036312c4c51d5e4c36.jpg?alt=media&token=7379135a-d129-4698-ac07-f9247d1f4bd0");
        mText.add("GOSPEL/INSPIRATIONAL");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_jazz.png?alt=media&token=29942595-1f5c-498d-a27c-38bdeb6622a4");
        mText.add("JAZZ");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_jazz.png?alt=media&token=29942595-1f5c-498d-a27c-38bdeb6622a4");
        mText.add("BLUES");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_alternative.png?alt=media&token=1dfcab2f-5ea3-49fc-9d50-d55abb7c0d25");
        mText.add("ALTERNATIVE");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_new_age.png?alt=media&token=7c042c02-3aad-4e8c-90fe-d20b6bdbb315");
        mText.add("NEW AGE");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_opera.png?alt=media&token=716d2ea6-4954-4687-8007-3e81eb1af3e4" +
                "");
        mText.add("CLASSICAL");

        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/hitstreamr-beta.appspot.com/o/DisocverImages%2Fw_genre_opera.png?alt=media&token=716d2ea6-4954-4687-8007-3e81eb1af3e4" +
                "");
        mText.add("OPERA");

        // Still need images for these genres. Delete later when they're fixed :)
        mImageUrls.add(null);
        mText.add("TRAP");

        mImageUrls.add(null);
        mText.add("EASY LISTENING");

        mImageUrls.add(null);
        mText.add("WORLD MUSIC");

        mImageUrls.add(null);
        mText.add("METAL");
    }

}
