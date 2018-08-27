package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.app.Activity.RESULT_OK;


public class InviteAFriendFragment extends Fragment {

    private static final int Request_Code = 100;
    private Button inviteBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inviteafriend, container, false);

        inviteBtn = (Button) view.findViewById(R.id.invite_button);

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onInviteClicked();
            }
        });

        Button close = (Button) view.findViewById(R.id.closeBtn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("TYPE", getArguments().getString("TYPE"));
                startActivity(intent);
            }
        });



        return view;
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Invite A Friend")
                .setMessage("Hey there, check out this cool app....")
                .setDeepLink(Uri.parse("http://google.com"))
                .setCallToActionText("Invitation CTA")


                .build();

        startActivityForResult(intent, Request_Code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_Code)
        {
            if (resultCode == RESULT_OK)
            {
                String [] ids = AppInviteInvitation.getInvitationIds(resultCode,data);

                for (String id : ids )
                {
                    System.out.println("InviteAFriendFragment.onActivityResult:"+ id);
                }
            }
            else
            {
                //error

            }
        }
    }
}

