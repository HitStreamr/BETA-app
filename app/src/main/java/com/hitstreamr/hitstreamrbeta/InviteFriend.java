package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class InviteFriend extends AppCompatActivity {
    private static final String TAG = "InviteAFriendFragment";
    private static final int REQUEST_INVITE = 100;
    private Button inviteBtn;
    private Uri dynamicLink;
    private Intent sendIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        inviteBtn = findViewById(R.id.invite_button);
        sendIntent = new Intent();
        dynamicLink = buildLongLink();
        inviteBtn.setOnClickListener(v -> onInviteClicked());
        Button close = findViewById(R.id.closeBtn);
        close.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("TYPE", getIntent().getExtras().getString("TYPE"));
            startActivity(intent);
        });
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
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_message) +  dynamicLink.toString());
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_title));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.invitation_title)));
    }
}
