package com.hitstreamr.hitstreamrbeta.DrawerMenuFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;


public class InviteAFriendFragment extends Fragment {
    private static final String TAG = "InviteAFriendFragment";
    private Button inviteBtn;
    private Uri dynamicLink;
    private Intent sendIntent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inviteafriend, container, false);

        inviteBtn = view.findViewById(R.id.invite_button);
        sendIntent = new Intent();
        dynamicLink = buildLongLink();
        inviteBtn.setOnClickListener(v -> onInviteClicked());
        Button close = view.findViewById(R.id.closeBtn);
        close.setOnClickListener(v -> {
            Log.e(TAG, "on invite click"+getArguments().getString("TYPE") );
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("TYPE", getArguments().getString("TYPE"));
            startActivity(intent);
        });

        return view;
    }

    private Uri buildLongLink(){
        return FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(getString(R.string.google_store_link)))
                .setDomainUriPrefix(getString(R.string.domain_url))
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(getString(R.string.package_name))
                                .setMinimumVersion(1)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(getString(R.string.invitation_title))
                                .setDescription(getString(R.string.invitation_message))
                                .build())
                .buildDynamicLink()
                .getUri();
    }
    private void onInviteClicked() {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_message) + dynamicLink.toString());
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_title));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.invitation_title)));
    }
}

