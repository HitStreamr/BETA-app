package com.hitstreamr.hitstreamrbeta.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hitstreamr.hitstreamrbeta.GenreRecyclerViewAdapter;
import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PickGenre extends AppCompatActivity implements GenreRecyclerViewAdapter.ItemClickListener, View.OnClickListener{

    RecyclerView mRecyclerView;
    Context context;
    RecyclerView.Adapter recyclerView_Adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    ArrayList<Integer> selectedItems;
    String[] images;
    HashMap<String, String> drawableToReadableNames;
    Button skipButton, nextButton;
    List<String> selectedGenres;

    private final int MIN_GENRE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_genre);

        context = getApplicationContext();

        selectedItems = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.genreView);

        images = getResources().getStringArray(R.array.genreImageDrawables);

        drawableToReadableNames = setStrings();

        recyclerViewLayoutManager = new GridLayoutManager(context, 2);

        mRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerView_Adapter = new GenreRecyclerViewAdapter(images,this);

        mRecyclerView.setAdapter(recyclerView_Adapter);

        skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(this);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        selectedGenres = new ArrayList<String>();
    }

    private HashMap<String, String> setStrings (){
        /*
        Genre Order
        8 <item>genre_alternative</item> : 0 <item>Alternative Music</item>
        13 <item>genre_blues</item> : 1 <item>Blues</item>
        14 <item>genre_classical</item> : 2 <item>Classical Music</item>
        4 <item>genre_country</item> : 3 <item>Country Music</item>
        4 <item>Dance Music</item>
        5 <item>Easy Listening</item>
        15 <item>genre_edm</item> : 6 <item>Electronic Music</item>
        7 <item>genre_folk</item> : 7 <item>Folk</item>
        9 <item>genre_inspirational_gospel</item> : 8 <item>Gospel</item>
        9 <item>Hip-Hop/R&amp;B</item>
        0 <item>genre_hiphop_rap</item> : 10 <item>Hip-Hop/Rap</item>
        5 <item>genre_indie</item> : 11 <item>Indie Pop</item>
        12 <item>genre_jazz</item> : 12 <item>Jazz</item>
        10 <item>genre_asian_pop</item> : 13 <item>K-pop</item>
        6 <item>genre_latin</item> : 14 <item>Latin Music</item>
        16 <item>genre_new_age</item> : 15 <item>New Age</item>
        17 <item>genre_opera</item> : 16 <item>Opera</item>
        1 <item>genre_pop</item> : 17 <item>Pop</item>
        3 <item>genre_rnb_soul</item> : 18 <item>R&amp;B/Soul</item>
        11 <item>genre_reggae</item> : 19<item>Reggae</item>
        2 <item>genre_rock</item> : 20 <item>Rock</item>
        21 <item>Trap</item>
        22 <item>World Music/Beats</item>
         */

        //matches the drawable names to user used strings
        String[] genres = getResources().getStringArray(R.array.Genre);
        HashMap<String,String> tmp =  new HashMap<String,String>();
        tmp.put(images[0],genres[10]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        tmp.put(images[0],genres[0]);
        return tmp;

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
        selectedGenres.add(img.getContentDescription().toString());
        checkButton();

    }

    @Override
    public void onRemoveItemClick(CardView view, ImageView img, int position) {
        Log.d("Pick", "Content Removed: " + img.getContentDescription());
        Log.d("Pick", "Content Added: " + img.getContentDescription());
        selectedItems.remove(Integer.valueOf(position));
        selectedGenres.remove(img.getContentDescription().toString());
        Log.d("Pick", "Content Added: " + img.getContentDescription());
        checkButton();
    }

    //button onClick
    @Override
    public void onClick(View v) {
        if(v == skipButton){
            Intent tempIntent = getIntent();
            if (tempIntent.getStringExtra("TYPE") != null){
                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                homeIntent.putExtra("TYPE", tempIntent.getStringExtra("TYPE"));
                homeIntent.putExtra("NUMBER_SELECTED", 0);
                homeIntent.putExtra("GENRES_SELECTED", (String[]) null);
                startActivity(homeIntent);
            }
        }

        if(v == nextButton){

            // Store selected genres to the database
            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SelectedGenres")
                    .child(current_user.getUid());

            databaseReference.setValue(selectedGenres).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(PickGenre.this, "Genres are stored", Toast.LENGTH_LONG).show();
                }
            });

            Intent tempIntent = getIntent();
            if (tempIntent.getStringExtra("TYPE") != null){
                String[] temp = new String[selectedItems.size()];

                for(int i = 0; i < selectedItems.size(); i++ ){
                    temp[i] = images[selectedItems.get(i)];
                }

                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                homeIntent.putExtra("TYPE", tempIntent.getStringExtra("TYPE"));
                homeIntent.putExtra("NUMBER_SELECTED", selectedItems);
                homeIntent.putExtra("GENRES_SELECTED", temp);
                startActivity(homeIntent);
            }

        }
    }

}
