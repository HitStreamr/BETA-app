package com.hitstreamr.hitstreamrbeta.Authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitstreamr.hitstreamrbeta.R;

import static android.view.View.VISIBLE;

public class Welcome extends AppCompatActivity implements View.OnClickListener {

    private Button sign_in, create_account;
    private TextView terms, privacy, text;
    private FrameLayout create_account_;
    private ProgressBar progressBar;
    private View reveal;


    final String TAG = "WELCOME";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        RelativeLayout relativeLayout = findViewById(R.id.animLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        sign_in = (Button) findViewById(R.id.signin_button);
        create_account = (Button) findViewById(R.id.create_account_button);
        terms = findViewById(R.id.terms);
        privacy = findViewById(R.id.privacyPolicy);

        //BUTTON ANIMATIION
//        create_account_ = findViewById(R.id.create_account_button_);
//        progressBar = findViewById(R.id.prog1);
//        reveal = findViewById(R.id.reveal);
//        text = (TextView) findViewById(R.id.textCA);


        sign_in.setOnClickListener(this);
       create_account.setOnClickListener(this);
       terms.setOnClickListener(this);
       privacy.setOnClickListener(this);

        //user not logged in, because Splash redirects

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_button:
                Intent openAuthenticator = new Intent(this, SignInActivity.class);
                startActivity(openAuthenticator);
                break;

            case R.id.create_account_button:
                Intent openCreateAcct = new Intent(this, AccountType.class);
                startActivity(openCreateAcct);
                break;

            case R.id.terms:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hitstreamr.com/terms-of-service"));
                startActivity(browserIntent);
                break;

            case R.id.privacyPolicy:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hitstreamr.com/terms-of-service"));
                startActivity(browserIntent2);
                break;
        }

    }


    // Animated 'Create Account' Button ----------------------------------------------------------
    //--------------------------------------------------------------------------------------------

    /*public void createAccount(View view) {
        animateButtonWidth();

        fadeTextLoadProgBar();

        nextAction();
    }

    private void animateButtonWidth() {
        ValueAnimator animator = ValueAnimator.ofInt(create_account_.getMeasuredWidth(), getFabWidth());

       animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
           public void onAnimationUpdate(ValueAnimator valueAnimator) {
               int val = (Integer) valueAnimator.getAnimatedValue();
               ViewGroup.LayoutParams layoutParams = create_account_.getLayoutParams();
               layoutParams.width = val;
               create_account_.requestLayout();

           }
       });
       animator.setDuration(200);
       animator.start();
    }

    private void fadeTextLoadProgBar() {
        text.animate().alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .start();
    }

    private void showProgDialog(){
        progressBar.getIndeterminateDrawable()
        .setColorFilter(Color.parseColor("#fffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(VISIBLE);
    }

    private void nextAction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                revealButton();

                fadeoutProgBar();

                delayedNextActivity();
            }

            }, 650);
        }


    private void revealButton(){
        create_account_.setElevation(0f);

        reveal.setVisibility(VISIBLE);

        int cx = reveal.getWidth();
        int cy = reveal.getHeight();

        int x = (int) (getFabWidth()/ 2 + create_account_.getX());
        int y = (int) (getFabWidth()/ 2 + create_account_.getY());

        float finalRadius = Math.max(cx, cy) * 1.2f;


        Animator reVeal = ViewAnimationUtils
                .createCircularReveal(reveal, x, y, getFabWidth(), finalRadius);

        reVeal.setDuration(350);
        reVeal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });

        reVeal.start();

    }

    private void fadeoutProgBar(){
        progressBar.animate().alpha(0f).setDuration(200).start();

    }

    private void delayedNextActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Welcome.this, AccountType.class));
            }
        }, 100);

    }

    private int getFabWidth(){
        return (int) 0d;
    }*/
}
