package com.hitstreamr.hitstreamrbeta;

import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Library extends AppCompatActivity   {
    private String accountType;
    private ExpandableRelativeLayout expandableLayout_history, expandableLayout_watchLater, expandableLayout_playlists;
    private BottomNavigationView bottomNavView;
    private RecyclerView recyclerView_watchLater, recyclerView_playlists;

    // For Testing Purposes
    private List<Book> bookList_watchLater = new ArrayList<>();
    private List<Book> bookList_playlists = new ArrayList<>();
    private BookAdapter bookAdapter_watchLater, bookAdapter_playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setTitle("Library");

        bottomNavView = findViewById(R.id.bottomNav);

        getUserType();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        // Profile Picture
        if (current_user.getPhotoUrl() != null) {
            CircleImageView circleImageView = toolbar.getRootView().findViewById(R.id.profilePictureToolbar);
            circleImageView.setVisibility(View.VISIBLE);
            //ImageView profileImageView = findViewById(R.id.profileImage);
            Uri photoURL = current_user.getPhotoUrl();
            Glide.with(getApplicationContext()).load(photoURL).into(circleImageView);
            //Glide.with(getApplicationContext()).load(photoURL).into(profileImageView);
            }

        getWatchLaterList();
        getPlaylists();
            }

    /**
     * Drop Down Menu - History
     * @param view view
     */
    public void expandableButton_history(View view) {
        expandableLayout_history = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout_history);
        expandableLayout_history.toggle(); // toggle expand and collapse
    }

    /**
     * Drop Down - Watch Later
     * @param view view
     */
    public void expandableButton_watchLater(View view) {
        expandableLayout_watchLater = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout_watchLater);
        expandableLayout_watchLater.toggle(); // toggle expand and collapse
    }

    /**
     * Drop Down - Playlist
     * @param view view
     */
    public void expandableButton_playlists(View view) {
        expandableLayout_playlists = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout_playlists);
        expandableLayout_playlists.toggle(); // toggle expand and collapse
    }


    /**
     * Handles back button on toolbar
     * @return true if pressed
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Get the account type of the current user
     */
    private void getUserType() {
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {
            //type = getIntent().getStringExtra("TYPE");

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                accountType = "BasicAccounts";
            } else if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_artist))) {
                // sth
                accountType = "ArtistAccounts";
            } else {
                accountType = "LabelAccounts";
            }
        }
    }

    /**
     * RecyclerView Test
     */
    private void getWatchLaterList() {
        recyclerView_watchLater = (RecyclerView) findViewById(R.id.recyclerView_watchLater);
        bookAdapter_watchLater = new BookAdapter(bookList_watchLater);
        recyclerView_watchLater.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_watchLater.setItemAnimator(new DefaultItemAnimator());
        recyclerView_watchLater.setAdapter(bookAdapter_watchLater);

        initBookData();
    }

    /**
     * RecyclerView Test
     */
    private void getPlaylists() {
       recyclerView_playlists = (RecyclerView) findViewById(R.id.recyclerView_playlists);
        bookAdapter_playlists = new BookAdapter(bookList_playlists);
        recyclerView_playlists.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_playlists.setItemAnimator(new DefaultItemAnimator());
        recyclerView_playlists.setAdapter(bookAdapter_playlists);

        initBookData2();
    }

    /**
     * Test
     */
    private void initBookData() {
        Book book = new Book("Hello Android", "Ed Burnette");
        bookList_watchLater.add(book);

        book = new Book("Beginning Android 3", "Mark Murphy");
        bookList_watchLater.add(book);

        book = new Book("Unlocking Android", " W. Frank Ableson");
        bookList_watchLater.add(book);

        book = new Book("Android Tablet Development", "Wei Meng Lee");
        bookList_watchLater.add(book);

        book = new Book("Android Apps Security", "Sheran Gunasekera");
        bookList_watchLater.add(book);

        book = new Book("Book1", "Author1");
        bookList_watchLater.add(book);

        book = new Book("Book2", "Author2");
        bookList_watchLater.add(book);

        book = new Book("Book3", "Author3");
        bookList_watchLater.add(book);

        book = new Book("Book4", "Author4");
        bookList_watchLater.add(book);

        book = new Book("Book5", "Author5");
        bookList_watchLater.add(book);

        bookAdapter_watchLater.notifyDataSetChanged();
    }

    /**
     * Test
     */
    private void initBookData2() {
        Book book = new Book("Hello Android", "Ed Burnette");
        bookList_playlists.add(book);

        book = new Book("Beginning Android 3", "Mark Murphy");
        bookList_playlists.add(book);

        book = new Book("Unlocking Android", " W. Frank Ableson");
        bookList_playlists.add(book);

        book = new Book("Android Tablet Development", "Wei Meng Lee");
        bookList_playlists.add(book);

        book = new Book("Android Apps Security", "Sheran Gunasekera");
        bookList_playlists.add(book);

        book = new Book("Book1", "Author1");
        bookList_playlists.add(book);

        book = new Book("Book2", "Author2");
        bookList_playlists.add(book);

        book = new Book("Book3", "Author3");
        bookList_playlists.add(book);

        book = new Book("Book4", "Author4");
        bookList_playlists.add(book);

        book = new Book("Book5", "Author5");
        bookList_playlists.add(book);

        bookAdapter_playlists.notifyDataSetChanged();
    }
}
