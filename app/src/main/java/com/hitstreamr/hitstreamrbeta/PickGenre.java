package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class PickGenre extends AppCompatActivity implements GenreRecyclerViewAdapter.ItemClickListener, View.OnClickListener{

    RecyclerView mRecyclerView;
    Context context;
    RecyclerView.Adapter recyclerView_Adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    ArrayList<Integer> selectedItems;
    String[] images;

    Button skipButton, nextButton;

    private final int MIN_GENRE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_genre);

        context = getApplicationContext();

        selectedItems = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.genreView);

        images = getResources().getStringArray(R.array.genreImageDrawables);

        recyclerViewLayoutManager = new GridLayoutManager(context, 2);

        mRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerView_Adapter = new GenreRecyclerViewAdapter(images,this);

        mRecyclerView.setAdapter(recyclerView_Adapter);

        skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(this);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
    }


    void checkButton() {
        boolean selected = selectedItems.size() >= MIN_GENRE;
        nextButton.setEnabled(selected);
        if (selected){
            nextButton.setTextColor(getColorStateList(R.color.colorPrimary));
        }else {
            nextButton.setTextColor(getColorStateList(R.color.litgryAccent));
        }

    }


    @Override
    public void onAddItemClick(CardView view, ImageView img, int position) {
        Log.d("Pick", "Content Added: " + img.getContentDescription());
        selectedItems.add(new Integer(position));
        checkButton();

    }

    @Override
    public void onRemoveItemClick(CardView view, ImageView img, int position) {
        Log.d("Pick", "Content Removed: " + img.getContentDescription());
        Log.d("Pick", "Content Added: " + img.getContentDescription());
        selectedItems.remove(Integer.valueOf(position));
        Log.d("Pick", "Content Added: " + img.getContentDescription());
        checkButton();
    }

    //button onClick
    @Override
    public void onClick(View v) {
        if(v == skipButton){
            Intent tempIntent = getIntent();
            if (tempIntent.getStringExtra("TYPE") != null){
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                homeIntent.putExtra("TYPE", tempIntent.getStringExtra("TYPE"));
                homeIntent.putExtra("NUMBER_SELECTED", 0);
                homeIntent.putExtra("GENRES_SELECTED", (String[]) null);
                startActivity(homeIntent);
            }
        }

        if(v == nextButton){
            Intent tempIntent = getIntent();
            if (tempIntent.getStringExtra("TYPE") != null){
                String[] temp = new String[selectedItems.size()];

                for(int i = 0; i < selectedItems.size(); i++ ){
                    temp[i] = images[selectedItems.get(i)];
                }

                Toast.makeText(getApplicationContext(), Arrays.deepToString(selectedItems.toArray())  + "\n" + Arrays.deepToString(temp) , Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                homeIntent.putExtra("TYPE", tempIntent.getStringExtra("TYPE"));
                homeIntent.putExtra("NUMBER_SELECTED", selectedItems);
                homeIntent.putExtra("GENRES_SELECTED", temp);
                startActivity(homeIntent);
            }

        }
    }

}
