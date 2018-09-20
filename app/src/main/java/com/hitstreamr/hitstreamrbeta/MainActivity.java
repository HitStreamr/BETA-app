package com.hitstreamr.hitstreamrbeta;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.DashboardFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.GeneralSettingsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.HelpCenterFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.InviteAFriendFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.LegalAgreementsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.NotificationSettingsFragment;
import com.hitstreamr.hitstreamrbeta.DrawerMenuFragments.PaymentPrefFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button logout;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private String type;
    FloatingActionButton fab;
    private ItemClickListener mListener;

    private MenuItem profileItem;


    RecyclerView suggestionsRecyclerView;
    RecyclerView resultsRecyclerView;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter suggestionAdapter;
    VideoResultAdapter resultAdapter;
    TextView noRes;
    ProgressBar searching;


    public final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        noRes = findViewById(R.id.emptyView);
        searching = findViewById(R.id.loadingSearch);


        //Recycler View
        resultsRecyclerView = findViewById(R.id.rcv_results);
        suggestionsRecyclerView = findViewById(R.id.rcv_suggestions);

        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Listener for RCVs
        mListener = new ItemClickListener() {
            @Override
            public void onSuggestionClick(String title) {
                getVideoResults(title);
            }

            @Override
            public void onResultClick(String title) {
                //figure out what to do next
            }
        };


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fab = findViewById(R.id.fab);

        //get menu & extras
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("TYPE") && getIntent().getStringExtra("TYPE") != null) {
            //check that type exists and set it.
            type = getIntent().getStringExtra("TYPE");

            if (getIntent().getStringExtra("TYPE").equals(getString(R.string.type_basic))) {
                //Hide Dash if Basic User & don't show floating action buttton

                Log.d("HIDE_DASH", getIntent().getStringExtra("TYPE"));
                //nav_Menu.findItem(R.id.dashboard).setVisible(false);
                navigationView.getMenu().findItem(R.id.dashboard).setVisible(false);
                fab.setVisibility(View.GONE);
            } else {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, VideoUploadActivity.class));
                    }
                });

            }
        }

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.profile:
                Intent homeIntent = new Intent(getApplicationContext(), pro.class);
                homeIntent.putExtra("TYPE", getString(R.string.type_artist));
                startActivity(homeIntent);
                break;*/
            case R.id.account:

                Intent accountIntent = new Intent(getApplicationContext(), Account.class);
                accountIntent.putExtra("TYPE", getIntent().getStringExtra("TYPE"));
                startActivity(accountIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Items
        profileItem = findViewById(R.id.profile);


        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        MenuItem search = menu.findItem(R.id.search);


        SearchView mSearchView = (SearchView) search.getActionView();
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getVideoResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //mAdapter.getFilter().filter(newText);
                //check if the results view is visibile (prior search) and reset
                if (resultsRecyclerView.getVisibility() == View.VISIBLE) {
                    resultsRecyclerView.setVisibility(View.GONE);
                    resultAdapter.clear();
                }
                if (newText.length() > 0) {
                    //search
                    searchVideoFirestore(autocompleteQuery(newText));
                    // Update suggestionAdapter/Set Adapter/Show/Listen
                    suggestionAdapter.notifyDataSetChanged();
                    suggestionsRecyclerView.setAdapter(suggestionAdapter);
                    suggestionsRecyclerView.setVisibility(View.VISIBLE);
                    suggestionAdapter.startListening();
                } else {
                    // if all the text is deleted, stop listing on suggestionAdapter so that the view empties
                    if (suggestionAdapter != null) {
                        suggestionAdapter.stopListening();
                    }
                }
                return true;
            }
        });


        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                Log.e("HOME", "On Close Intitiated");
                suggestionAdapter.stopListening();
                resultAdapter.clear();
                suggestionsRecyclerView.setVisibility(View.GONE);
                resultsRecyclerView.setVisibility(View.GONE);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;  // Return true to expand action view
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public Query autocompleteQuery(String query) {
        int strlength = query.length();
        String strFrontCode = query.substring(0, strlength);
        String strEndCode = query.substring(strlength - 1);

        String endcode = strFrontCode + Character.toString((char) (strEndCode.charAt(0) + 1));

        return db.collection("videos").whereGreaterThanOrEqualTo("title", query).whereLessThan("title", query + "\uf8ff");
    }

    public void searchVideoFirestore(Query searchRequest) {

        //New RecyclerOptions and Adapter, based on Query
        FirestoreRecyclerOptions<Video> options = new FirestoreRecyclerOptions.Builder<Video>()
                .setQuery(searchRequest, Video.class)
                .build();

        suggestionAdapter = adapterMaker(options);
    }

    public FirestoreRecyclerAdapter adapterMaker(FirestoreRecyclerOptions<Video> options) {
        return new FirestoreRecyclerAdapter<Video, MainActivity.VideoSuggestionsHolder>(options) {
            @NonNull
            @Override
            public void onBindViewHolder(MainActivity.VideoSuggestionsHolder holder, int position, Video model) {
                holder.videoTitle.setText(model.getTitle());
            }

            @Override
            public MainActivity.VideoSuggestionsHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.search_suggestion_video, group, false);

                return new MainActivity.VideoSuggestionsHolder(view, mListener);
            }
        };

    }

    public void getVideoResults(String query) {
        //These Tasks are Task<QuerySnapShot>
        ArrayList<String> terms = processQuery(query);
        Log.e(TAG, terms.toString());
        final Task<QuerySnapshot> exactmatch = db.collection("videos").whereEqualTo("title", query).get();

        searching.setVisibility(View.VISIBLE);
        //Stop using the old adapter
        suggestionsRecyclerView.setVisibility(View.GONE);
        suggestionAdapter.stopListening();

        // build a dynamic query that has all the words

        Query allWords = db.collection("videos").whereEqualTo("terms." + terms.get(0), true);
        for (int i = 0; i < terms.size(); i++) {
            allWords = allWords.whereEqualTo("terms." + terms.get(i), true);
        }

        Task<QuerySnapshot> allWordsTask = allWords.get();

        //ArrayList<Task<QuerySnapshot>> tasks = new ArrayList<>();
        // tasks.add(exactmatch);
        //tasks.add(allWordsTask);

        //allResultsRetreived is only successful, when all are succesful
        Task<List<QuerySnapshot>> allResultsRetrieved = Tasks.whenAllSuccess(exactmatch, allWordsTask);

        //When everything is done, then send to adapter
        allResultsRetrieved.addOnSuccessListener(
                new OnSuccessListener<List<QuerySnapshot>>() {
                    @Override
                    public void onSuccess(List<QuerySnapshot> tasks) {
                        ArrayList<Video> videos = new ArrayList();
                        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
                        for (QuerySnapshot qTasks : tasks) {
                            if (!qTasks.isEmpty()) {
                                ArrayList<DocumentSnapshot> tmp = new ArrayList<>(qTasks.getDocuments());
                                for (DocumentSnapshot ds : tmp) {
                                    if (!docs.contains(ds)) {
                                        docs.add(ds);
                                    }
                                }
                            }
                        }

                        for (DocumentSnapshot d : docs) {
                            //if doc exists
                            if (d.exists()) {
                                Log.e(TAG, d.toObject(Video.class).toString());
                                videos.add(d.toObject(Video.class));
                            } else {
                                Log.e(TAG, "Document " + d.toString() + "does not exist");
                            }
                        }
                        //set up results
                        resultAdapter = new VideoResultAdapter(videos, mListener);
                        resultAdapter.notifyDataSetChanged();
                        resultsRecyclerView.setAdapter(resultAdapter);
                        resultsRecyclerView.setVisibility(View.VISIBLE);
                        searching.setVisibility(View.GONE);
                        //Stop using the old adapter
                        suggestionsRecyclerView.setVisibility(View.GONE);
                        suggestionAdapter.stopListening();

                    }

                }
        );
    }

    private ArrayList<String> processQuery(String query) {
        // ArrayList of characters to remove
        ArrayList<String> remove = new ArrayList<>();
        remove.add(" ");

        ArrayList<String> tmp = new ArrayList<>(Arrays.asList(query.trim().toLowerCase().split("\\s+")));
        //tmp.removeAll(remove);

        return tmp;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction;
        Bundle bundle;
        switch (item.getItemId()) {
            case R.id.dashboard:
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                DashboardFragment dashFrag = new DashboardFragment();
                dashFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, dashFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.general_setting:
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                GeneralSettingsFragment genSettingsFrag = new GeneralSettingsFragment();
                genSettingsFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, genSettingsFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.notification_settings:
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                NotificationSettingsFragment notifSettingsFrag = new NotificationSettingsFragment();
                notifSettingsFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, notifSettingsFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.payment_pref:
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                PaymentPrefFragment payPrefFrag = new PaymentPrefFragment();
                payPrefFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, payPrefFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.invite_a_friend:
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                InviteAFriendFragment inviteFrag = new InviteAFriendFragment();
                inviteFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, inviteFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.help_center:
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                HelpCenterFragment helpFrag = new HelpCenterFragment();
                helpFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, helpFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.legal_agreements:
                transaction = getSupportFragmentManager().beginTransaction();
                bundle = new Bundle();
                bundle.putString("TYPE", type);
                LegalAgreementsFragment legalFrag = new LegalAgreementsFragment();
                legalFrag.setArguments(bundle);
                transaction.replace(R.id.fragment_container, legalFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.logout:
                startActivity(new Intent(this, Pop.class));
                //IdentityManager.getDefaultIdentityManager().signOut();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (suggestionAdapter != null) {
            suggestionAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (suggestionAdapter != null) {
            suggestionAdapter.stopListening();
        }
    }


    class VideoSuggestionsHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;

        public VideoSuggestionsHolder(View itemView, final ItemClickListener mListener) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.video_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSuggestionClick(videoTitle.getText().toString());
                }
            });
        }
    }

    public interface ItemClickListener {
        void onSuggestionClick(String title);

        void onResultClick(String title);
    }


}
