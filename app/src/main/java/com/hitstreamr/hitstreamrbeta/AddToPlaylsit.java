package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddToPlaylsit extends AppCompatActivity implements View.OnClickListener {

    private Button cancel, ok;
    private TextView createPlaylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlsit);

        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.confirm);
        createPlaylist = findViewById(R.id.createplaylist);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        createPlaylist.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .4));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();

                break;

            case R.id.confirm:
                finish();
                startActivity(new Intent(getApplicationContext(), CreateNewPlaylist.class));
                break;
        }

    }
}
//