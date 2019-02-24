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

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

import static android.app.Activity.RESULT_OK;


public class InviteAFriendFragment extends Fragment {
    private static final String TAG = "InviteAFriendFragment";
    private static final int REQUEST_INVITE = 100;
    private Button inviteBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inviteafriend, container, false);

        inviteBtn = view.findViewById(R.id.invite_button);
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

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                Log.e(TAG, "Error occurred while sending a message");
            }
        }
    }
}

