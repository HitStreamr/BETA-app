package com.hitstreamr.hitstreamrbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button logout;
    private DrawerLayout drawer;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, VideoUploadActivity.class));
            }
        });

    //    logout = (Button) findViewById(R.id.logout_button);

    //    logout.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_main, menu);
        return true;
    }
    // @Override
  //  public void onClick(View v) {
  //      switch (v.getId()) {
   //         case R.id.logout_button:
   //             IdentityManager.getDefaultIdentityManager().signOut();
   //             break;
  //      }
 //   }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DashboardFragment()).commit();
                break;
            case R.id.general_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new GeneralSettingsFragment()).commit();
                break;
            case R.id.notification_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NotificationSettingsFragment()).commit();
                break;
            case R.id.payment_pref:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PaymentPrefFragment()).commit();
                break;
            case R.id.invite_a_friend:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new InviteAFriendFragment()).commit();
                break;
            case R.id.help_center:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HelpCenterFragment()).commit();
                break;
            case R.id.legal_agreements:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LegalAgreementsFragment()).commit();
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

}
