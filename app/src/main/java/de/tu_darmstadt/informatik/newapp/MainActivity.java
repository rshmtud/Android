package de.tu_darmstadt.informatik.newapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.tu_darmstadt.informatik.newapp.Client.Pager_Client;
import de.tu_darmstadt.informatik.newapp.Client.uiClubber;
import de.tu_darmstadt.informatik.newapp.Server.uiHost;
import de.tu_darmstadt.informatik.newapp.database_activity.LoginActivity;
import de.tu_darmstadt.informatik.newapp.GameActivity.Game.GameActivity;
import de.tu_darmstadt.informatik.newapp.database_activity.UserProfileRegistrationActivity;
import de.tu_darmstadt.informatik.newapp.database_activity.ManageUserProfileActivity;
import de.tu_darmstadt.informatik.newapp.javabeans.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    SQLiteDBHelper sqLiteDBHelper;
    private String device_id;
    User user=null;


    //Tabbed Layout Details
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MainActivity_Pager adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        sqLiteDBHelper = new SQLiteDBHelper(this);

        getUserDetails();
        if(user == null || user.getRegistrationStatus()!= 1){
            Intent intent = new Intent(this, UserProfileRegistrationActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        //Tabbed Layout Details
        tabLayout = (TabLayout) findViewById(R.id.maintabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Party"));
        tabLayout.addTab(tabLayout.newTab().setText("DJ"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.mainactivity_pager);
        adapter = new MainActivity_Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void getUserDetails() {
        try{
            Cursor res = sqLiteDBHelper.readUserData(device_id);
            if (res.getCount() > 0) {
                user = new User();
                while (res.moveToNext()) {
                    user.setFirstName(res.getString(2));
                    user.setUserName(res.getString(3));
                    user.setEmail(res.getString(4));
                    //user.setImageData(res.getBlob(5));
                    user.setRegistrationStatus(res.getInt(6));

                }


            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_freeticket) {
            // Handle the camera action
        } else if (id == R.id.nav_myticket) {


        } else if (id == R.id.nav_alert) {
            myAlert();

        } else if (id == R.id.nav_hostparty) {

        } else if(id == R.id.nav_profile){
            Intent intent = new Intent(this, ManageUserProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if (id == R.id.nav_game) {
          Intent GameActivity= new Intent(MainActivity.this, GameActivity.class);
          startActivity(GameActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void myAlert() {
        Intent i = new Intent(MainActivity.this, myAlert.class);
        startActivity(i);
    }

    public void sendClubber(View view) {
        Intent intent = new Intent(this, uiClubber.class);
        startActivity(intent);
    }

    public void sendHost(View view) {
        Intent intent = new Intent(this, uiHost.class);
        startActivity(intent);
    }

    public void sendRegistration(View view) {
        Intent intent = new Intent(this, UserProfileRegistrationActivity.class);
        startActivity(intent);
    }

    public void sendLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
